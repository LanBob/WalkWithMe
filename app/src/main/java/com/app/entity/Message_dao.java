package com.app.entity;

public class Message_dao {

    public String getMessage_name() {
        return message_name;
    }

    public void setMessage_name(String message_name) {
        this.message_name = message_name;
    }

    public Integer getMessage_image() {
        return message_image;
    }

    public void setMessage_image(Integer message_image) {
        this.message_image = message_image;
    }

    String message_name;

    @Override
    public String toString() {
        return "Message_dao{" +
                "message_name='" + message_name + '\'' +
                ", message_image=" + message_image +
                '}';
    }

    Integer message_image;
}