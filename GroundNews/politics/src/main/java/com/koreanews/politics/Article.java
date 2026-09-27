package com.koreanews.politics;

import java.util.Date;

public class Article {
    private String title;
    private String link; 
    private Date pubDate; 
    private String sourceName; 

    public Article(String title, String link, Date pubDate, String sourceName){
        this.title = title; 
        this.link = link;
        this.pubDate = pubDate; 
        this.sourceName = sourceName;
    }
    
    // getter methods for Article private field
    public String getTitle(){
        return title;
    }
     public String getLink(){
        return link;
    }
     public Date getPubDate(){
        return pubDate;
    }
     public String getSourceName(){
        return sourceName;
    }
}
