package com.cyber.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix="jwt")
public class AuthingTokenInterceptorPathPattern {

    List<String> pathPatterns;

    List<String> excludePathPatterns;

    Map<String,String> resourceHandlers;

    public List<String> getPathPatterns() {
        return pathPatterns;
    }

    public void setPathPatterns(List<String> pathPatterns) {
        this.pathPatterns = pathPatterns;
    }

    public List<String> getExcludePathPatterns() {
        return excludePathPatterns;
    }

    public void setExcludePathPatterns(List<String> excludePathPatterns) {
        this.excludePathPatterns = excludePathPatterns;
    }

    public Map<String, String> getResourceHandlers() {
        return resourceHandlers;
    }

    public void setResourceHandlers(Map<String, String> resourceHandlers) {
        this.resourceHandlers = resourceHandlers;
    }
}
