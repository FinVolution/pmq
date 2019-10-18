package com.ppdai.infrastructure.rest.mq.boot;

import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
//引入swagger会导致中文乱码，非必须不加入
//@Configuration //必须存在
//@EnableSwagger2 //必须存在
//@EnableWebMvc //必须存在
//@ComponentScan(basePackageClasses=PubServController.class) //必须存在 
public class SwaggerConfig{

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .version("1.1.0")
                .build();
    }
}

