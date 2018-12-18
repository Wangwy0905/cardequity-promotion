package com.youyu.cardequity.promotion.biz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2018年12月6日 下午10:00:00
 * @work Swagger配置类
 */
@Configuration
@EnableSwagger2
//@Profile({"dev", "test"})
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("权益系统")
                        .description("权益系统相关功能")
                        .contact(new Contact("徐长焕", "", "xuchanghuan@youyufund.com"))
                        .version("1.0")
                        .build()
                ).select()
                .apis(basePackage("com.youyu.cardequity.promotion.biz"))
                .paths(any())
                .build();
    }

}
