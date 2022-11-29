package com.cyber.application.controller;

import com.alibaba.fastjson.JSONObject;
import com.cyber.domain.constant.AuthingTokenKey;
import com.cyber.domain.entity.AuthingToken;
import com.cyber.infrastructure.toolkit.ThreadLocals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthingTokenController {

    protected Logger LOGGING = LoggerFactory.getLogger(getClass());

    protected AuthingToken<JSONObject> getThreadLocalToken() {
        if(ThreadLocals.get(AuthingTokenKey.X_CLIENT_TOKEN_USER) != null) {
            AuthingToken<JSONObject> token = (AuthingToken<JSONObject>) ThreadLocals.get(AuthingTokenKey.X_CLIENT_TOKEN_USER);
            return token;
        }
        LOGGING.debug("Get Token Form ThreadLocal,But Is Empty ...");
        return null;
    }
    protected String getTenantCode() {
        AuthingToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getTenantCode() : "";
    }

    protected String getSessionId() {
        AuthingToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getSessionId(): "";
    }

    protected String getSessionName() {
        AuthingToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getSessionName()  : "";
    }

    protected JSONObject getUser() {
        AuthingToken<JSONObject> token = getThreadLocalToken();
        return token != null ? token.getUser()  : null;
    }

}
