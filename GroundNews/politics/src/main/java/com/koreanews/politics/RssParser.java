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

public class RssParser {
    public List<Article> parseFeed(NewsSource source){
        List<Article> articles = new ArrayList<>();

        try{
            URL url = URI.create(source.getUrl()).toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            HttpURLConnection.setFollowRedirects(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            int redirectCount = 0;
            while ((responseCode == 301 || responseCode == 302 || responseCode == 303 || 
                responseCode == 307 || responseCode == 308) && redirectCount < 5){
                    
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

            InputStream stream = connection.getInputStream();

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(stream)); 

            for (SyndEntry entry : feed.getEntries()){
                String title = entry.getTitle();
                String link = entry.getLink();
                Date pubDate = entry.getPublishedDate();

                Article article = new Article(title, link, pubDate, source.getName());
                articles.add(article);
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        return articles; 
        
    }
}