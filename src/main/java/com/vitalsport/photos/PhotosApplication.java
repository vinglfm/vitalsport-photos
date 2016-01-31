package com.vitalsport.photos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@SpringBootApplication
public class PhotosApplication {
    public static void main(String[] args) {
        SpringApplication.run(PhotosApplication.class, args);
    }

    @Bean
    public Docket profileApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("photos")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Vitalsport-photos")
                .description("Photos module API")
                .version("0.9")
                .build();
    }
}
