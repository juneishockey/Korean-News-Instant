package com.koreanews.politics;

import java.util.*;


import org.springframework.stereotype.Service;

@Service
public class TopicService {
    private ConfigRss configRss; 
    private RssParser rssParser; 
    private AiTopicMatcher aiTopicMatcher; 

    private List<TopicGrouper> cachedResult; 
    private long lastFetchTime; 

    private static final int MAX_PER_SOURCE = 10;
    private static final long CACHE_DURATION = 1200000;

    public TopicService(AiTopicMatcher aiTopicMatcher){
        this.configRss = new ConfigRss();
        this.rssParser = new RssParser();
        this.aiTopicMatcher = aiTopicMatcher;
    }
    
    public List<TopicGrouper> getTopics() throws Exception {
        long now = System.currentTimeMillis();
        if (cachedResult != null && now - lastFetchTime < CACHE_DURATION){
            return cachedResult;
        }

        List<Article> libArticles = collectArticles("liberal");
        List<Article> conservArticles = collectArticles("conservative");

        // sorted the result
        List<TopicGrouper> result = aiTopicMatcher.aiTopicMatch(libArticles, conservArticles);
        result.sort((a, b) -> 
        (b.getLiberalArticles().size() + b.getConservArticles().size()) 
        - (a.getLiberalArticles().size() + a.getConservArticles().size())
        );

        cachedResult = result; 
        lastFetchTime = now; 
        return result;

    }

    private List<Article> collectArticles(String category){
        List<NewsSource> sources = configRss.getMapOfMedia().get(category);
        List<Article> articles = new ArrayList<>();

        if (sources == null){
            return articles;
        }

        for (NewsSource source : sources){
            List<Article> fromSource = rssParser.parseFeed(source);
            articles.addAll(limit(fromSource));
        }
        return articles;
    }
    
    private List<Article> limit(List<Article> articles){
        if (articles.size() <= MAX_PER_SOURCE){
            return articles;
        }
        return articles.subList(0,MAX_PER_SOURCE); 
    }



}
