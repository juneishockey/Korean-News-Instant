package com.koreanews.politics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PoliticsApplication {
// 도커나 모든 실행의 시작. 
// 메인 메서드. 
	public static void main(String[] args) {
		SpringApplication.run(PoliticsApplication.class, args);
	}

}
