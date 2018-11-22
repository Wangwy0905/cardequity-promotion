package com.youyu.cardequity.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages={"com.youyu.middleground.communication.api","com.youyu.middleground.storage.api"})
public class CardequityPromotionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardequityPromotionApplication.class, args);
	}
}
