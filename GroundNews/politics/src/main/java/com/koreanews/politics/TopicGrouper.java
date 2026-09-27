package com.koreanews.politics;

import java.util.*;

public class TopicGrouper {
    private String topicName; 
    private List<Article> liberalArticles;
    private List<Article> conservativeArticles; 

    public TopicGrouper(String topicName){
        this.topicName = topicName;
        this.liberalArticles = new ArrayList<>();
        this.conservativeArticles = new ArrayList<>();
    }

    public String getTopicName(){
        return topicName; 
    }

    // Liberal Articles
    public List<Article> getLiberalArticles() {
        return liberalArticles;
    }
    public void addLiberalArticle(Article article){
        liberalArticles.add(article);
    }

    // Conservative Articles
    public List<Article> getConservArticles(){
        return conservativeArticles;  
    }
    public void addConservArticle(Article article){
        conservativeArticles.add(article);
    }

}
