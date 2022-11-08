package com.cyber.infrastructure.config;

import com.cyber.infrastructure.interceptor.RestClientInterceptor;
import com.cyber.infrastructure.interceptor.JWTTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Configuration
public class JWTTokenConfig implements WebMvcConfigurer {

    @Autowired
    JWTTokenInterceptor tokenInterceptor;

    @Autowired
    RestClientInterceptor httpRequestInterceptor;

    @Value("${jwt.pathPatterns}")
    List<String> pathPatterns;

    @Value("${jwt.excludePathPatterns}")
    List<String> excludePathPatterns;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns(pathPatterns).excludePathPatterns(excludePathPatterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/oss/**").addResourceLocations("classpath:/oss/");
    }

    @Bean(name = "jwtTokenRestTemplate")
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(httpRequestInterceptor));
        return restTemplate;
    }
}
