package com.koreanews.politics;

import java.util.*;

/**
주제 하나관련 Liberal & Conservative articles 보관.
getter and adder method 가 있다.
 **/

public class TopicGrouper {
    private String topicName; 
    private List<Article> liberalArticles;
    private List<Article> conservativeArticles; 

    // TopicGrouper Constructor
    public TopicGrouper(String topicName){
        this.topicName = topicName;
        this.liberalArticles = new ArrayList<>();
        this.conservativeArticles = new ArrayList<>();
    }

    // getter for topic name
    public String getTopicName(){
        return topicName; 
    }

    // Liberal Articles getter & adder
    public List<Article> getLiberalArticles() {
        return liberalArticles;
    }
    public void addLiberalArticle(Article article){
        liberalArticles.add(article);
    }


    // Conservative Articles getter & adder
    public List<Article> getConservArticles(){
        return conservativeArticles;  
    }
    public void addConservArticle(Article article){
        conservativeArticles.add(article);
    }

}
