package com.vladyslav.encryptedchat.ViewsInterfaces;

import com.vladyslav.encryptedchat.Models.ChatModel.Message;
import android.view.View;

import com.firebase.ui.database.FirebaseListAdapter;

public interface ChatView {
    void setMessages(FirebaseListAdapter<Message> adapter);

    void updateMessage(View v, String username, String time, String text);
}
