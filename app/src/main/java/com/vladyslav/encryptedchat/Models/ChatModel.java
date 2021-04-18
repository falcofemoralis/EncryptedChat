package com.vladyslav.encryptedchat.Models;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vladyslav.encryptedchat.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatModel {
    private static final String CHATS_STORAGE = "chats";
    private String currentUserEmail;
    private String chatId;

    public static class Message {
        public String email;
        public List<Integer> msgText;
        public long msgTime;

        public Message() {
        }

        public Message(String email, byte[] msgText) {
            this.email = email;
            this.msgText = new ArrayList<>();
            for (Byte aByte : msgText) {
                this.msgText.add((int) aByte);
            }
            this.msgTime = new Date().getTime();
        }
    }

    private static ChatModel instance;
    private DatabaseReference chatRef;

    public static ChatModel getInstance(String chatId) {
        if (instance == null) {
            instance = new ChatModel();
            instance.init(chatId);
        }
        return instance;
    }

    private void init(String chatId) {
        if (chatId == null) {
            chatRef = FirebaseDatabase.getInstance().getReference(CHATS_STORAGE).push();
        } else {
            chatRef = FirebaseDatabase.getInstance().getReference(CHATS_STORAGE + "/" + chatId);
        }
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public static void delete(){
        instance = null;
    }

    public FirebaseListOptions<Message> getMessagesAdapterOptions() {
        return new FirebaseListOptions.Builder<Message>()
                .setQuery(chatRef, Message.class)
                .setLayout(R.layout.msg_item)
                .build();
    }

    public String getChatId() {
        chatId = chatRef.getKey();
        return chatId;
    }

    public void sendMessage(byte[] text) {
        // Отправляем сообщение
        chatRef.push().setValue(new Message(currentUserEmail, text));
    }
}
