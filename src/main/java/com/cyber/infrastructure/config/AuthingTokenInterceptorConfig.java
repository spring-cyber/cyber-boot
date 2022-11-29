package com.cyber.infrastructure.config;

import com.cyber.infrastructure.interceptor.RestClientInterceptor;
import com.cyber.infrastructure.interceptor.AuthingTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;


@Configuration
public class AuthingTokenInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    AuthingTokenInterceptor tokenInterceptor;

    @Autowired
    RestClientInterceptor httpRequestInterceptor;

    @Autowired
    AuthingTokenInterceptorPathPattern pathPattern;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor).addPathPatterns(pathPattern.pathPatterns).excludePathPatterns(pathPattern.excludePathPatterns);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if(!CollectionUtils.isEmpty(pathPattern.resourceHandlers)) {
            for(String key : pathPattern.resourceHandlers.keySet()) {
                registry.addResourceHandler(key).addResourceLocations(pathPattern.resourceHandlers.get(key));
            }
        }
    }

    @Bean(name = "jwtTokenRestTemplate")
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(httpRequestInterceptor));
        return restTemplate;
    }
}
