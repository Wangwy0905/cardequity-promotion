package com.youyu.cardequity.promotion.biz;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class CardequityPromotionApplicationTests {


	@Test
	public void contextLoads() {
	}

	public static void main(String[] args) {


		System.out.println("OrderNo".matches("([A-Z][a-z]*)\\\\1"));

		System.out.println("OrderNo".replaceAll("^([A-Z][a-z]*)([A-Z][a-z]*)\2", "$1_$2"));

		// System.out.println("神探狄仁&*%$杰之四大天王@bdfbdbdfdgds23532".replaceAll("[^\\u4e00-\\u9fa5]", ""));
	}
}
