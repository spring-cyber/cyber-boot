package com.cyber.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyber.constant.JWTTokenKey;
import com.cyber.entity.JWTToken;
import com.cyber.utils.ThreadLocals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JWTTokenController {

    protected Logger LOGGING = LoggerFactory.getLogger(getClass());

    protected JWTToken<JSONObject> getThreadLocalToken() {
        if(ThreadLocals.get(JWTTokenKey.X_CLIENT_TOKEN_USER) != null) {
            JWTToken<JSONObject> token = (JWTToken<JSONObject>)ThreadLocals.get(JWTTokenKey.X_CLIENT_TOKEN_USER);
            return token;
        }
        LOGGING.debug("Get Token Form ThreadLocal,But Is Empty ...");
        return null;
    }
    protected String getTenantCode() {
        JWTToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getTenantCode() : "";
    }

    protected String getSessionId() {
        JWTToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getSessionId(): "";
    }

    protected String getSessionName() {
        JWTToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getSessionName()  : "";
    }

    protected JSONObject getUser() {
        JWTToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getUser()  : null;
    }

}
