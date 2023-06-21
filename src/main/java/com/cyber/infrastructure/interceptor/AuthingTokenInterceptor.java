package com.cyber.infrastructure.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cyber.domain.constant.AuthingTokenKey;
import com.cyber.domain.constant.HttpResultCode;
import com.cyber.domain.entity.AuthingToken;
import com.cyber.infrastructure.toolkit.Responses;
import com.cyber.infrastructure.toolkit.ThreadLocals;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Component
public class AuthingTokenInterceptor implements HandlerInterceptor  {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthingTokenInterceptor.class);
    private static final String BASIC_JWT_TOKEN_PREFIX = "Basic ";

    Cache<String, String> jwtTokenStringCache = CacheBuilder.newBuilder().maximumSize(2).expireAfterWrite(5, TimeUnit.MINUTES).build();
    Cache<String, AuthingToken> jwtTokenCache = CacheBuilder.newBuilder().maximumSize(2).expireAfterWrite(5, TimeUnit.MINUTES).build();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(checkAuthingToken(request,response)) {
            LOGGER.debug("JWTToken Interceptor, check JWTToken Success ... ");
            return true;
        }

        LOGGER.debug("JWTToken Interceptor, check JWTToken Fail ... ");

        return false;
    }



    public boolean checkAuthingToken(HttpServletRequest request, HttpServletResponse response) {
        String jwtTokenString = null;
        Cookie tokenCookie = WebUtils.getCookie(request, AuthingTokenKey.X_CLIENT_JWT_TOKEN);
        if (tokenCookie != null) {
            LOGGER.debug("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest Cookie ... ");
            jwtTokenString = tokenCookie.getValue();
        }

        if(StringUtils.isEmpty(jwtTokenString)) {
            LOGGER.debug("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest Header ... ");
            jwtTokenString = request.getHeader(AuthingTokenKey.X_CLIENT_JWT_TOKEN);
        }

        if(StringUtils.isEmpty(jwtTokenString)) {
            LOGGER.info("Get [X_CLIENT_JWT_TOKEN] From HttpServletRequest,But Empty ... ");
            Responses.response(response, HttpResultCode.BAD_AUTH);
            return false;
        }

        if (jwtTokenString.startsWith(BASIC_JWT_TOKEN_PREFIX)) {
            jwtTokenString = jwtTokenString.substring(BASIC_JWT_TOKEN_PREFIX.length());
        }

        String jwtTokenLocal = jwtTokenStringCache.getIfPresent(AuthingTokenKey.X_CLIENT_JWT_TOKEN);
        AuthingToken<JSONObject> jwtTokenUser = null;
        if(StringUtils.isNoneEmpty(jwtTokenLocal)) {
            if(jwtTokenLocal.equals(jwtTokenString)) {
                jwtTokenUser = jwtTokenCache.getIfPresent(AuthingTokenKey.X_CLIENT_TOKEN_USER);
                if(jwtTokenUser != null) {
                    LOGGER.info("Get [X_CLIENT_TOKEN_USER] From Local Cache ... ");
                    ThreadLocals.put(AuthingTokenKey.X_CLIENT_TOKEN_USER, jwtTokenUser);
                    return true;
                }
                LOGGER.info("Get [X_CLIENT_TOKEN_USER] From Local Cache, But Empty ... ");
            }
        }
        jwtTokenUser = claim2Token(jwtTokenString);
        if(jwtTokenUser == null) {
            LOGGER.info("Get [X_CLIENT_TOKEN_USER] From jwtToken String ... ");
            Responses.response(response, HttpResultCode.SERVER_ERROR);
            return false;
        }


        jwtTokenStringCache.put(AuthingTokenKey.X_CLIENT_JWT_TOKEN,jwtTokenString);
        jwtTokenCache.put(AuthingTokenKey.X_CLIENT_TOKEN_USER, jwtTokenUser);

        ThreadLocals.put(AuthingTokenKey.X_CLIENT_TOKEN_USER, jwtTokenUser);
        return true;
    }

    @Value("${acl.jwt.secret:ABCDEFGHIJKLMNOPQRSTUVMXYZABCDEFGHIJKLMNOPQRSTUVMXYZABCDEFGHIJKLMNOPQRSTUVMXYZABCDEFGHIJKLMNOPQRSTUVMXYZ}")
    private String jwtSecret;
    private AuthingToken<JSONObject> claim2Token(String jwtToken) {
        Key signingKey = new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
        Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwtToken).getBody();

        AuthingToken<JSONObject> token = new AuthingToken<JSONObject>();
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

            HashSet<String> roles = claims.get("roles", HashSet.class);
            if (CollectionUtils.isEmpty(roles)) {
                LOGGER.debug("Get [roles] From Claims, But Empty... ");
            }
            token.setRoles(roles);

            String userString = claims.get("user", String.class);
            if (StringUtils.isEmpty(userString)) {
                LOGGER.debug("Get [user] From Claims, But Empty... ");
            }
            JSONObject user = JSONObject.parseObject(userString);
            token.setUser(user);
        } catch (Exception exception) {
            LOGGER.error("Get Token From Claims, But Exception... ");
        }
        return token;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocals.reset();
    }
}
