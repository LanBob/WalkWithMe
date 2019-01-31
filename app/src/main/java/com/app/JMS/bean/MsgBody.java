package com.app.JMS.bean;



public  class   MsgBody implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
     private MsgType localMsgType;


    public MsgType getLocalMsgType() {
        return localMsgType;
    }

    public void setLocalMsgType(MsgType localMsgType) {
        this.localMsgType = localMsgType;
    }
}
