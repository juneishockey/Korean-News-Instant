package com.koreanews.politics;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

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
    
    @GetMapping("/api/topics")
    public List<TopicGrouper> getTopics() throws Exception {
        return topicService.getTopics();
    }

}
