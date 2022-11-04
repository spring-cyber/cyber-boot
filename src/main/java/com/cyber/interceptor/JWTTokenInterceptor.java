package com.cyber.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cyber.constant.JWTTokenKey;
import com.cyber.entity.JWTToken;
import com.cyber.utils.ThreadLocals;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.concurrent.TimeUnit;

public class JWTTokenInterceptor implements HandlerInterceptor  {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTTokenInterceptor.class);
    private static final String BASIC_JWT_TOKEN_PREFIX = "Basic ";

    Cache<String, JWTToken> tokenCache = CacheBuilder.newBuilder().maximumSize(2).expireAfterWrite(5, TimeUnit.MINUTES).build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        JWTToken<JSONObject> jwtToken = checkJWTToken(request);
        if(null != jwtToken) {
            LOGGER.debug("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest ... ");
            ThreadLocals.put(JWTTokenKey.X_CLIENT_TOKEN_USER, jwtToken);
            return true;
        }


        jwtToken = checkToken(request,response);
        if(null != jwtToken) {
            LOGGER.debug("Get [X_CLIENT_TOKEN] From HttpServletRequest ... ");
            ThreadLocals.put(JWTTokenKey.X_CLIENT_TOKEN_USER, jwtToken);
            return true;
        }
        LOGGER.debug("Get [X_CLIENT_TOKEN] From HttpServletRequest,But Empty ...");
        return false;
    }



    public JWTToken<JSONObject> checkJWTToken(HttpServletRequest request) {
        String jwtTokenString = null;
        Cookie tokenCookie = WebUtils.getCookie(request, JWTTokenKey.X_CLIENT_JWT_TOKEN);
        if (tokenCookie != null) {
            LOGGER.debug("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest Cookie ... ");
            jwtTokenString = tokenCookie.getValue();
        }

        if(StringUtils.isEmpty(jwtTokenString)) {
            LOGGER.debug("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest Header ... ");
            jwtTokenString = request.getHeader(JWTTokenKey.X_CLIENT_JWT_TOKEN);
        }

        if(StringUtils.isEmpty(jwtTokenString)) {
            LOGGER.info("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest,But Empty ... ");
            return null;
        }

        if (jwtTokenString.startsWith(BASIC_JWT_TOKEN_PREFIX)) {
            jwtTokenString = jwtTokenString.substring(BASIC_JWT_TOKEN_PREFIX.length());
        }

        return claim2Token(jwtTokenString);
    }

    @Value("${acl.jwt.secret:ABCDEFGHIJKLMNOPQRSTUVMXYZABCDEFGHIJKLMNOPQRSTUVMXYZABCDEFGHIJKLMNOPQRSTUVMXYZABCDEFGHIJKLMNOPQRSTUVMXYZ}")
    private String jwtSecret;
    private JWTToken<JSONObject> claim2Token(String jwtToken) {
        Key signingKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwtToken).getBody();

        JWTToken<JSONObject> token = new JWTToken<JSONObject>();
        token.setJwtToken(jwtToken);
        try {
            String sessionId = claims.get("session_id", String.class);
            if (StringUtils.isEmpty(sessionId)) {
                LOGGER.debug("Get [session_id] Token Id From Claims, But Empty... ");
                return null;
            }
            token.setSessionId(sessionId);

            String sessionName = claims.get("session_name", String.class);
            if (StringUtils.isEmpty(sessionName)) {
                LOGGER.debug("Get [sessionName] From Claims, But Empty... ");
                return null;
            }
            token.setSessionName(sessionName);

            String deviceId = claims.get("device_id", String.class);
            if (StringUtils.isEmpty(deviceId)) {
                LOGGER.debug("Get [deviceId] From Claims, But Empty... ");
            }
            token.setDeviceId(deviceId);

            String tokenId = claims.get("token", String.class);
            if (StringUtils.isEmpty(tokenId)) {
                LOGGER.debug("Get [token] From Claims, But Empty... ");
            }
            token.setToken(tokenId);

            JSONObject user = claims.get("user", JSONObject.class);
            if (user == null) {
                LOGGER.debug("Get [user] From Claims, But Empty... ");
            }
            token.setUser(user);
        } catch (Exception exception) {
            LOGGER.error("Get Token From Claims, But Exception... ");
        }
        return token;
    }

    public JWTToken<JSONObject> checkToken(HttpServletRequest request, HttpServletResponse response) {
        String tokenKey = null;
        Cookie tokenCookie = WebUtils.getCookie(request, JWTTokenKey.X_CLIENT_TOKEN);
        if (tokenCookie != null) {
            tokenKey = tokenCookie.getValue();
        }
        if (StringUtils.isEmpty(tokenKey)) {
            tokenKey = request.getHeader(JWTTokenKey.X_CLIENT_TOKEN);
        }
        if (StringUtils.isEmpty(tokenKey)) {
            LOGGER.debug("Get [X_CLIENT_TOKEN] From HttpServletRequest Cookie & Header,But Empty... ");


            return null;
        }

        JWTToken<JSONObject> token = tokenCache.getIfPresent(tokenKey);
        if (token == null) {

            //redis
        } else {
            LOGGER.info("Check Token {} In Local Cache... ", tokenKey);
        }

        return token;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocals.reset();
    }
}
