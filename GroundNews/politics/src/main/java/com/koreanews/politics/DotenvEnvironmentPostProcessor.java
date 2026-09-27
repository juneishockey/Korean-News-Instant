package com.koreanews.politics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 프로젝트 루트의 .env 파일을 읽어 Spring 환경에 추가한다.
 * 실제 OS 환경변수가 우선하도록 맨 뒤(addLast)에 등록하므로,
 * 배포 환경에서는 .env 없이 환경변수만으로도 동작한다.
 */
public class DotenvEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String SOURCE_NAME = "dotenv";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envFile = Paths.get(System.getProperty("user.dir"), ".env");
        if (!Files.isRegularFile(envFile)) {
            return; // .env 가 없으면 조용히 통과 (배포 환경)
        }

        Map<String, Object> values = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(envFile, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = unquote(trimmed.substring(eq + 1).trim());
                values.put(key, value);
            }
        } catch (IOException e) {
            throw new IllegalStateException(".env 파일을 읽지 못했습니다: " + envFile, e);
        }

        if (!values.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(SOURCE_NAME, values));
        }
    }

    private String unquote(String value) {
        if (value.length() >= 2
            && ((value.startsWith("\"") && value.endsWith("\""))
                || (value.startsWith("'") && value.endsWith("'")))) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}
