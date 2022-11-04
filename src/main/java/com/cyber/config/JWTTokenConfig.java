package com.cyber.config;

import com.cyber.interceptor.JWTTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
public class JWTTokenConfig implements WebMvcConfigurer {

    @Autowired
    JWTTokenInterceptor tokenInterceptor;

    @Value("token-path-pattern")
    List<String> pathPatterns;

    @Value("exclude-token-path-pattern")
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
}
