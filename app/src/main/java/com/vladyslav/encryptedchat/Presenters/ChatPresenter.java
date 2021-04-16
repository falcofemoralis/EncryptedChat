package com.vladyslav.encryptedchat.Presenters;

import android.text.format.DateFormat;
import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.vladyslav.encryptedchat.Managers.KeyManager;
import com.vladyslav.encryptedchat.Models.ChatModel;
import com.vladyslav.encryptedchat.Models.ChatModel.Message;
import com.vladyslav.encryptedchat.ViewsInterfaces.ChatView;

public class ChatPresenter {
    private ChatView chatView;
    private ChatModel chatModel;
    private FirebaseListAdapter<Message> messagesAdapter;
    private KeyManager keyManager;

    public ChatPresenter(ChatView chatView, String chatId) {
        this.chatView = chatView;
        this.chatModel = ChatModel.getInstance(chatId);

        // Инициализция ключа
        keyManager = KeyManager.getInstance(chatId);
        keyManager.initKey(key -> {
            // Ключ получен, загружаем сообщения
            init();
        });
    }

    private void init() {
        messagesAdapter = new FirebaseListAdapter<Message>(chatModel.getMessagesAdapterOptions()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                String text = keyManager.getDecryptedMsg(model.msgText);
                if (text == null) {
                    text = "Failed to decrypt";
                }
                chatView.updateMessage(v, model.userName, (String) DateFormat.format("dd-mm-yyyy HH:mm:ss", model.msgTime), text);
            }
        };

        messagesAdapter.startListening();
        chatView.setMessages(messagesAdapter);
    }

    public void sendMessage(String text) {
        byte[] encryptedMsg = keyManager.getEncryptedMsg(text);
        if (encryptedMsg != null) {
            chatModel.sendMessage(encryptedMsg);
        }
    }
}
