package com.app.entity;

public class Message_chat_dao {
    //0是收,1是发
    public static  final int TYPE_RECEIVED = 0;
    public static  final int TYPE_SEND = 1;
    private String content;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path;



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

}
