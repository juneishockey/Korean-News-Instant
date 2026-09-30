package com.koreanews.politics;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * /api/... 경로로 오는 요청을 받고, 누구에게 일을 시킬지 정하고, 
 * 화면용은 HTML ViewController 가 담당.
 * 반환값을 JSON으로 바꿔서 응답본문에 담는다 - API 나 다른것과 연계시킬때
 */
@RestController 
public class Controller {
    private final TopicService topicService;
    private ConfigRss configrss;
    private RssParser rssParser;
    
    public Controller(TopicService topicService){
        this.configrss = new ConfigRss(); 
        this.rssParser = new RssParser();
        this.topicService = topicService; 
    }

    // GET 피드 요청이 브라우저에서 왔을때 로직
    @GetMapping("/api/feeds")
    public List<Article> getsFeed(@RequestParam String category) {
        List<NewsSource> sources = configrss.getMapOfMedia().get(category);
        
        if (sources == null){
            sources = new ArrayList<>();
        }

        List<Article> allArticles = new ArrayList<>();

        for (NewsSource source : sources){
            List<Article> articlesFromSource = rssParser.parseFeed(source); 
            allArticles.addAll(articlesFromSource); 
        }
        return allArticles;
    }
    
    // GET 토픽 요청이 왔을때 로직, TopicService 한테 넘긴다
    @GetMapping("/api/topics")
    public List<TopicGrouper> getTopics() throws Exception {
        return topicService.getTopics();
    }

}
