package com.se.jsonmodel;

/**
 * Created by Jack on 2017/7/1.
 */
public class Message<T> {
    private int status;
    private String error;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }


    public void setError(String error) {
        this.error = error;
    }
}
