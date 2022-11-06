package com.cyber.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cyber.constant.JWTTokenKey;
import com.cyber.entity.JWTToken;
import com.cyber.utils.ThreadLocals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;

import java.io.IOException;

public class RestClientInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            JWTToken<JSONObject> token = null;
            if(ThreadLocals.get(JWTTokenKey.X_CLIENT_TOKEN_USER) != null) {
                HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);

                token =  (JWTToken<JSONObject>) ThreadLocals.get(JWTTokenKey.X_CLIENT_TOKEN_USER);
                requestWrapper.getHeaders().set(JWTTokenKey.X_CLIENT_JWT_TOKEN,token.getJwtToken());

                return execution.execute(requestWrapper, body);
            }
            return execution.execute(request,body);
        } catch (Throwable throwable) {
            throw throwable;
        }
    }
}
