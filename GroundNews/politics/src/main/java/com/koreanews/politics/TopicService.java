package com.koreanews.politics;

import java.util.*;
import org.springframework.stereotype.Service;

/** 
 * 다른 클래스들에 일 시키는 총괄 클래스
 */
@Service
public class TopicService {
    //일 시킬 부품들:
    private ConfigRss configRss; 
    private RssParser rssParser; 
    private AiTopicMatcher aiTopicMatcher; 

    //캐시 (저장된 결과, 저장된 시간):
    private List<TopicGrouper> cachedResult; 
    private long lastFetchTime; 

    // 언론사당 기사 10개, 캐시 유효시간 20분.
    private static final int MAX_PER_SOURCE = 10;
    private static final long CACHE_DURATION = 1200000;

    //부품 세팅:
    public TopicService(AiTopicMatcher aiTopicMatcher){
        this.configRss = new ConfigRss();
        this.rssParser = new RssParser();
        this.aiTopicMatcher = aiTopicMatcher;
    }
    
    // 전체 총괄 지휘 (public)
    public List<TopicGrouper> getTopics() throws Exception {
        long now = System.currentTimeMillis();

        // 이미 뽑은 결과가 없거나(1번째), 20분 안지났으면 바로 반환:
        if (cachedResult != null && now - lastFetchTime < CACHE_DURATION){
            return cachedResult;
        }

        // 각 성향별 기사 리스트: 
        List<Article> libArticles = collectArticles("liberal");
        List<Article> conservArticles = collectArticles("conservative");

        // 기사 수 많은 순으로 정렬:
        List<TopicGrouper> result = aiTopicMatcher.aiTopicMatch(libArticles, conservArticles);
        result.sort((a, b) -> 
        (b.getLiberalArticles().size() + b.getConservArticles().size()) 
        - (a.getLiberalArticles().size() + a.getConservArticles().size())
        );

        // 캐시 업데이트 후 반환:
        cachedResult = result; 
        lastFetchTime = now; 
        return result;

    }

    // 카테고리 하나의 기사 모으기:
    private List<Article> collectArticles(String category){
        // 언론사 명단 불러오기:
        List<NewsSource> sources = configRss.getMapOfMedia().get(category);
        List<Article> articles = new ArrayList<>();

        // 없으면 빈 리스트 내기 (혹시나 null이면):
        if (sources == null){
            return articles;
        }

        // RSS 읽고, 10개만 내고, 모으기: 
        for (NewsSource source : sources){
            List<Article> fromSource = rssParser.parseFeed(source);
            articles.addAll(limit(fromSource));
        }
        return articles;
    }
    
    // 10개로 제한하기:
    private List<Article> limit(List<Article> articles){
        if (articles.size() <= MAX_PER_SOURCE){
            return articles;
        }
        return articles.subList(0,MAX_PER_SOURCE); 
    }

}
