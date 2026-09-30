package com.koreanews.politics;

import java.util.Date;

/** 
 Article 클래스 
 - 기사 하나를 title, link, published Date, source name 로 묶어 정리한다
 -  getter methods 로 field 를 부른다. 

 **/

public class Article {
    private String title;
    private String link; 
    private Date pubDate; 
    private String sourceName; 

    // Article constructor
    public Article(String title, String link, Date pubDate, String sourceName){
        this.title = title; 
        this.link = link;
        this.pubDate = pubDate; 
        this.sourceName = sourceName;
    }
    
    // getter methods for Article parameters
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
