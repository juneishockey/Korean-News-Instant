package com.koreanews.politics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import org.json.JSONObject;
import org.json.JSONArray;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class GeminiAPIComm {

    @Value("${gemini.api.key}")
    private String apiKey;

    public String callAi(String prompt) throws Exception {
        // 키가 없으면 앱은 뜨되, AI 호출 시점에 명확한 메시지로 실패
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY 환경변수가 설정되지 않았습니다.");
        }

        HttpClient client = HttpClient.newHttpClient();

        // Step 2: 요청 본문(JSON) 만들기 — Gemini는 contents/parts 구조
        JSONObject part = new JSONObject();
        part.put("text", prompt);

        JSONArray parts = new JSONArray();
        parts.put(part);

        JSONObject content = new JSONObject();
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.put(content);

        JSONObject requestBody = new JSONObject();
        requestBody.put("contents", contents);

        JSONObject genConfig = new JSONObject();
        genConfig.put("responseMimeType", "application/json");
        requestBody.put("generationConfig", genConfig);


        // Step 3: HttpRequest 만들기 — API 키는 x-goog-api-key 헤더로
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash:generateContent";


        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("content-type", "application/json")
            .header("x-goog-api-key", apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        // Step 4: 보내고 응답 받기
        // 503(과부하)/429(요청 과다)는 일시적이라 잠깐 기다렸다가 재시도 (2초, 4초, 8초)
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        long waitMs = 2000;
        for (int retry = 0; retry < 3 && (response.statusCode() == 503 || response.statusCode() == 429); retry++) {
            Thread.sleep(waitMs);
            waitMs *= 2;
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        // 200이 아니면 (키 오류, 429 할당량 초과 등) 응답 내용을 담아 바로 실패
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Gemini API 오류 (" + response.statusCode() + "): " + response.body());
        }

        return response.body();
    }
}