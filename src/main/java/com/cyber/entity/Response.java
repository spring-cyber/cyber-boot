package com.cyber.entity;

import com.cyber.constant.Constant;

public class Response extends Entity {

    private int code;

    private String message;

    public Response() { }

    public Response(int code) {
        this.code = code;
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return "0".equals(code);
    }

    public static Response success() {
        return new Response(0);
    }

    public static Response fail(int code,String message) {
        return new Response(code,message);
    }


    public static Response fail(Constant.ResultCode resultCode) {
        return new Response(resultCode.getStatusCode(), resultCode.getStatusMessage());
    }

}
