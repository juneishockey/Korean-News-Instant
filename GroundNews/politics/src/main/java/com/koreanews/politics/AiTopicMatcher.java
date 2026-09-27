package com.koreanews.politics;

import java.util.*;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.json.JSONArray;

import java.lang.StringBuilder;


@Service 
public class AiTopicMatcher {
    private GeminiAPIComm geminiAPIComm;

    public AiTopicMatcher(GeminiAPIComm geminiAPIComm){
        this.geminiAPIComm = geminiAPIComm;   
    }
    
    public List<TopicGrouper> aiTopicMatch(List<Article> liberalArticles, List<Article> conservativeArticles) throws Exception{
        StringBuilder sb = new StringBuilder();
        int index = 1;

        for (Article news : liberalArticles){
            sb.append(index).append(". ").append(news.getTitle()).append(" (liberal)\n");
            index ++;
        }

        for (Article news : conservativeArticles){
            sb.append(index).append(". ").append(news.getTitle()).append(" (conservative)\n");
            index ++;
        }

        sb.append("\n위 기사 제목들을 같은 주제끼리 묶어서 분류해줘. ");
        sb.append("각 그룹은 topic, liberal_indices, conservative_indices를 모두 포함해야 해. 해당 카테고리 기사가 없으면 빈 배열 []로 표시해줘.");
        sb.append("설명없이 순수 JSON 배열로 응답. 예시: [{\"topic\": \"관세 정책\", \"liberal_indices\": [1,5], \"conservative_indices\": [2,7]}]");
        
        String prompt = sb.toString();
        String aiResponse = geminiAPIComm.callAi(prompt);
        System.out.println(aiResponse);  // 임시 디버깅용

        JSONObject responseJson = new JSONObject(aiResponse);
        JSONArray candidates = responseJson.getJSONArray("candidates");
        JSONObject candidate = candidates.getJSONObject(0);
        JSONObject contentObj = candidate.getJSONObject("content");
        JSONArray partsArray = contentObj.getJSONArray("parts");
        JSONObject firstPart = partsArray.getJSONObject(0);
        String groupText = firstPart.getString("text");

    

        JSONArray topicGroups = new JSONArray(groupText); 

        List<TopicGrouper> result = new ArrayList<>();
        for (int i = 0; i < topicGroups.length(); i++){
            JSONObject object = topicGroups.getJSONObject(i); 
            String topic = object.getString("topic");
            JSONArray liberalIndices = object.getJSONArray("liberal_indices");
            JSONArray conservativeIndicies = object.getJSONArray("conservative_indices");

            TopicGrouper topicGrouper = new TopicGrouper(topic);

            // Finding article thru looping liberalArticles
            for (int j = 0; j < liberalIndices.length(); j++){
                int number = liberalIndices.getInt(j) - 1;
                // AI가 범위 밖 번호를 줄 수 있으니 건너뜀
                if (number < 0 || number >= liberalArticles.size()) continue;
                Article yep = liberalArticles.get(number);
                topicGrouper.addLiberalArticle(yep); 

            }
            //Finding article thru looping conservArticles
            for (int j = 0; j < conservativeIndicies.length(); j++){
                int number = conservativeIndicies.getInt(j) - liberalArticles.size() - 1;
                if (number < 0 || number >= conservativeArticles.size()) continue;
                Article yep = conservativeArticles.get(number);
                topicGrouper.addConservArticle(yep);
            }

            result.add(topicGrouper);
        }

        

        return result; 

        

    }

    
}
