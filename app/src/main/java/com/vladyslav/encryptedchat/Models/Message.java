package com.vladyslav.encryptedchat.Models;

import java.util.Date;

public class Message {
    private String userName;
    private String msgText;
    private long msgTime;

    public Message() {
    }

    public Message(String userName, String msgText) {
        this.userName = userName;
        this.msgText = msgText;
        this.msgTime = new Date().getTime();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }

    public long getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(long msgTime) {
        this.msgTime = msgTime;
    }
}
