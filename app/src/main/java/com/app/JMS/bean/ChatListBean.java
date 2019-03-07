package com.app.JMS.bean;

public class ChatListBean {
    private String userId;
    private int count;
    private String time;

    public String getUserId() {
        return userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
