package com.koreanews.politics;

import java.util.*;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.json.JSONArray;

import java.lang.StringBuilder;

/**
 * 내용을 다룬다. 
 * 프롬프트 만들기,지시문 내용을 만들고, 응답을 해석해준다.
 */
@Service 
public class AiTopicMatcher {
    private GeminiAPIComm geminiAPIComm;

    public AiTopicMatcher(GeminiAPIComm geminiAPIComm){
        this.geminiAPIComm = geminiAPIComm;   
    }
    
    public List<TopicGrouper> aiTopicMatch(List<Article> liberalArticles, List<Article> conservativeArticles) throws Exception{
        StringBuilder sb = new StringBuilder();
        int index = 1;

        // 진보 기사 제목에 번호를 부친다:
        for (Article news : liberalArticles){
            sb.append(index).append(". ").append(news.getTitle()).append(" (liberal)\n");
            index ++;
        }

        // 보수 기사 제목에 번호를 부친다: 
        for (Article news : conservativeArticles){
            sb.append(index).append(". ").append(news.getTitle()).append(" (conservative)\n");
            index ++;
        }
        // Gemini 에게 넣는 프롬프트 - 지시문: 
        sb.append("\n위 기사 제목들을 같은 주제끼리 묶어서 분류해줘. ");
        sb.append("각 그룹은 topic, liberal_indices, conservative_indices를 모두 포함해야 해. 해당 카테고리 기사가 없으면 빈 배열 []로 표시해줘.");
        sb.append("설명없이 순수 JSON 배열로 응답. 예시: [{\"topic\": \"관세 정책\", \"liberal_indices\": [1,5], \"conservative_indices\": [2,7]}]");
        String prompt = sb.toString();

        // Gemini 호출
        String aiResponse = geminiAPIComm.callAi(prompt);
  
        // Gemini 의 Candidate - content -parts 형식을 풀어나가기: 
        JSONObject responseJson = new JSONObject(aiResponse);
        JSONArray candidates = responseJson.getJSONArray("candidates");
        JSONObject candidate = candidates.getJSONObject(0);
        JSONObject contentObj = candidate.getJSONObject("content");
        JSONArray partsArray = contentObj.getJSONArray("parts");
        JSONObject firstPart = partsArray.getJSONObject(0);
        String groupText = firstPart.getString("text");

        // 진짜 내용을  JSON 으로 파싱하기: 
        JSONArray topicGroups = new JSONArray(groupText); 

        // Gemini 가 준 번호를 실제 기사로 바꾸고 TopicGroup 에다 담기: 
        List<TopicGrouper> result = new ArrayList<>();
        for (int i = 0; i < topicGroups.length(); i++){
            JSONObject object = topicGroups.getJSONObject(i); 
            String topic = object.getString("topic");
            JSONArray liberalIndices = object.getJSONArray("liberal_indices");
            JSONArray conservativeIndicies = object.getJSONArray("conservative_indices");

            // TopicGrouper 에 주제생성하기:
            TopicGrouper topicGrouper = new TopicGrouper(topic);

            // 진보 기사 돌면서 찾기
            for (int j = 0; j < liberalIndices.length(); j++){
                int number = liberalIndices.getInt(j) - 1;
                // 번호가 음수이거나 정해져있는 바깥 수를 주면 건너뛰기:
                if (number < 0 || number >= liberalArticles.size()) continue;
                Article yep = liberalArticles.get(number);
                topicGrouper.addLiberalArticle(yep); 

            }
            // 보수 기사 돌면서 찾기
            for (int j = 0; j < conservativeIndicies.length(); j++){
                int number = conservativeIndicies.getInt(j) - liberalArticles.size() - 1;
                // 번호가 음수이거나 정해져있는 바깥수를 주면 건너뛰기:
                if (number < 0 || number >= conservativeArticles.size()) continue;
                Article yep = conservativeArticles.get(number);
                topicGrouper.addConservArticle(yep);
            }
            //주제 하나를 처리한다: 
            result.add(topicGrouper);
        }
        // 모든 주제를 받아서 돌려준 - TopicService 가 받아서 정렬. 
        return result; 
    }

    
}
