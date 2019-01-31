package com.app.JMS;


import java.io.Serializable;

/**
 * Created by ownlove on 2019/1/28.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String fromUserId;
    private String toUserId;
    byte[] message;

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
