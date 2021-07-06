package com.xjh.common.utils;

public class Result<T> {
    int code;
    boolean success;
    String msg;
    T data;

    public static <T> Result<T> success(T data) {
        Result<T> rs = new Result<>();
        rs.setSuccess(true);
        rs.setCode(0);
        rs.setMsg("成功");
        rs.setData(data);
        return rs;
    }

    public static <T> Result<T> fail(String msg) {
        return fail(1, msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        Result<T> rs = new Result<>();
        rs.setSuccess(false);
        rs.setCode(code);
        rs.setMsg(msg);
        return rs;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
