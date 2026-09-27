package com.koreanews.politics;

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

    // getter methods for NewsSource private field
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
