package com.koreanews.politics;

import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.*;
import java.io.InputStream;
import java.lang.Exception;

import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndEntry;

/**
 * Rome 라이브러리로 RSS(XML)뉴스 정보를 Article 형식으로 바꾼다. 
 * 데이터가 처음으로 들어오는구역. 
 * RssParser
 */

public class RssParser {
    public List<Article> parseFeed(NewsSource source){
        List<Article> articles = new ArrayList<>();
        
        // NewsSource 가 들어오면 List<Article> 형식으로 바꿔준다: 
        try{
            //요청 준비:
            URL url = URI.create(source.getUrl()).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //리디렉션을 따라가라 지시: 
            HttpURLConnection.setFollowRedirects(true);
            connection.setInstanceFollowRedirects(true);

            //요청 헤더: 유저는 누구인지 밝히기 - 크롬인척 하기:
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            
            // 연결맺기 5초, 연결포기 5초: 기다리다 에러나지 않도록:
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            // 실제 요청 전송: 
            int responseCode = connection.getResponseCode();
            int redirectCount = 0;

            // 리디렉트 카운트가 5미만, 3xx (이동) 코드뜨면
            while ((responseCode == 301 || responseCode == 302 || responseCode == 303 || 
                responseCode == 307 || responseCode == 308) && redirectCount < 5){
                    
                    //현재 주소 기준 완전한 주소로 만든다, 리디렉션 속성은 같음: 
                    String newUrl = connection.getHeaderField("Location");
                    URL resolvedUrl = new URL(connection.getURL(), newUrl);
                    connection = (HttpURLConnection) resolvedUrl.openConnection();
                    connection.setRequestProperty("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"); 
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    responseCode = connection.getResponseCode();
                    redirectCount = redirectCount + 1;
                }

            //실제 본문을 받기: 
            InputStream stream = connection.getInputStream();
            
            SyndFeedInput input = new SyndFeedInput(); // Rome XML 파싱하게.
            SyndFeed feed = input.build(new XmlReader(stream)); //한글이 안깨지게.
            
            // RSS item 하나를 Article 객체로 변환하기:
            for (SyndEntry entry : feed.getEntries()){
                String title = entry.getTitle();
                String link = entry.getLink();
                Date pubDate = entry.getPublishedDate();

                Article article = new Article(title, link, pubDate, source.getName());
                articles.add(article);
            }

        // 에러 대처
        } catch(Exception e){
            e.printStackTrace();
        }
        return articles; 
        
    }
}