package com.youyu.cardequity.promotion.biz;

import org.springframework.cloud.client.SpringCloudApplication;
import tk.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringCloudApplication
@EnableFeignClients(basePackages={"com.youyu.cardequity.promotion.api"})
@MapperScan("com.youyu.cardequity.promotion.biz.dal.dao")
public class CardequityPromotionApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardequityPromotionApplication.class, args);
	}
}
