package com.youyu.cardequity.promotion.biz;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages={"com.youyu.cardequity.promotion.api"})
@MapperScan("com.youyu.cardequity.promotion.biz.dal.dao")
public class CardequityPromotionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardequityPromotionApplication.class, args);
	}
}
