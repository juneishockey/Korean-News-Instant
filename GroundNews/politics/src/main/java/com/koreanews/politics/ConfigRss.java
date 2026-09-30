package com.koreanews.politics;

import java.util.*;

/**
 * 언론사 명단을 제공하는 클래스.
 * 실제 RSS 링크와 liberal/conservative 별 하드코딩된 명단이 있다.
 */

public class ConfigRss{
    private Map<String, List<NewsSource>> mapOfMedia = new HashMap<>();
    private List<NewsSource> liberal = new ArrayList<>();
    private List<NewsSource> conservative = new ArrayList<>();

    public ConfigRss(){
        setUpMedia();
    }
    
    //add each media + url, category to liberal or conservative, save it to mapOfMedia HashMap. 
    private void setUpMedia(){

        liberal.add(new NewsSource("한겨레", "https://www.hani.co.kr/rss/politics/", "liberal"));
        liberal.add(new NewsSource("경향신문", "https://www.khan.co.kr/rss/rssdata/politic_news.xml", "liberal"));
        liberal.add(new NewsSource("오마이뉴스", "https://rss.ohmynews.com/rss/politics.xml", "liberal"));
        
        conservative.add(new NewsSource("조선일보", "https://www.chosun.com/arc/outboundfeeds/rss/category/politics/?outputType=xml", "conservative"));
        conservative.add(new NewsSource("동아일보", "https://rss.donga.com/politics.xml", "conservative" ));
        conservative.add(new NewsSource("중앙일보", "https://www.jasm.co.kr/rss/S1N1.xml", "conservative"));

        mapOfMedia.put("liberal", liberal);
        mapOfMedia.put("conservative", conservative);

    }

    // getter of MapOfMedia
    public Map<String, List<NewsSource>> getMapOfMedia(){
        return mapOfMedia; 
    }
    
}


