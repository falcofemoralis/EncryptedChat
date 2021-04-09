package com.vladyslav.encryptedchat.Models;

import java.util.Date;

public class MessageModel {
    class Message {
        public String userName;
        public String msgText;
        public long msgTime;

        public Message() {
        }

        public Message(String userName, String msgText) {
            this.userName = userName;
            this.msgText = msgText;
            this.msgTime = new Date().getTime();
        }
    }
}
