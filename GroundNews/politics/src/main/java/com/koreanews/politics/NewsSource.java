package com.koreanews.politics;

/** 
 * 언론사 하나의 RSS 정보를 저장.
url 을 name과 category랑 같이 저장. 
**/ 

public class NewsSource {
    private String name;
    private String url; 
    private String category; 

    // NewsSource constructor
    public NewsSource(String name, String url, String category){
        this.name = name;
        this.url = url; 
        this.category = category; 
    }

    // getter methods for NewsSource each parameter 
    public String getName(){
        return name;
    }
    
    public String getUrl(){
        return url;
    }

    public String getCategory(){
        return category; 
    }
    
}
