package com.se.jsonmodel;

/**
 * Created by Jack on 2017/7/1.
 */
public class LoginReturn {
    private int userId;
    private String userName;

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
