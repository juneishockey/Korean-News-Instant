package com.koreanews.politics;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;


/**
 * 화면용 컨트롤러 담당. HTML 로 보여준다, 사람이 쓰는 화면에 보여준다
 */
@Controller 
public class ViewController {
    private final TopicService topicService;
    private ConfigRss configrss;
    private RssParser rssParser;
    
    public ViewController(TopicService topicService){
        this.configrss = new ConfigRss(); 
        this.rssParser = new RssParser();
        this.topicService = topicService;
    }

    // 루트 주소로 들어오면 피드 화면으로 보냄
    @GetMapping("/")
    public String home() {
        return "redirect:/feed?category=liberal";
    }

    // 주제로 들어오면
    @GetMapping("/topics")
    public String showTopics(Model model) {
        try {
            model.addAttribute("topics", topicService.getTopics());
        } catch (Exception e) {
            // Gemini 오류 등으로 실패해도 페이지는 보여주고 안내 문구 표시
            model.addAttribute("topics", List.of());
            model.addAttribute("error", "주제를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.");
        }
        return "topics";
    }

    // 피드로 들어오면
    @GetMapping("/feed")
    public String showFeed(@RequestParam String category, Model model) {
        List<NewsSource> sources = configrss.getMapOfMedia().get(category);
        if (sources == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown category: " + category);
        }

        List<Article> allArticles = new ArrayList<>();

        for (NewsSource source : sources){
            List<Article> articlesFromSource = rssParser.parseFeed(source); 
            allArticles.addAll(articlesFromSource);
        }

        model.addAttribute("articles", allArticles);
        return "feed";

    }
    
}
