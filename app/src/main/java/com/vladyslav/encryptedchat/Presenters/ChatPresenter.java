package com.vladyslav.encryptedchat.Presenters;

import android.text.format.DateFormat;
import android.view.View;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.vladyslav.encryptedchat.EncryptProcessor.MessageProcessor;
import com.vladyslav.encryptedchat.Managers.KeyManager;
import com.vladyslav.encryptedchat.Models.ChatModel;
import com.vladyslav.encryptedchat.Models.ChatModel.Message;
import com.vladyslav.encryptedchat.ViewsInterfaces.ChatView;

public class ChatPresenter {
    private ChatView chatView;
    private ChatModel chatModel;
    private FirebaseListAdapter<Message> messagesAdapter;
    private KeyManager keyManager;
    private MessageProcessor messageProcessor;

    public ChatPresenter(ChatView chatView, String chatId) {
        this.chatView = chatView;
        this.chatModel = ChatModel.getInstance(chatId);
        this.messageProcessor = new MessageProcessor();
        keyManager = KeyManager.getInstance(chatId);
        keyManager.initKey(key -> {
            init();
            sendMessage("Join in!");
        });
    }

    private void init() {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        messagesAdapter = new FirebaseListAdapter<Message>(chatModel.getMessagesAdapterOptions()) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                String text = messageProcessor.getDecryptedMsg(model.msgText, keyManager.getSecretKey(), keyManager.getIVspec());
                if (text == null)
                    text = "Failed to decrypt";

                boolean isSender = false;
                if (currentUserEmail.equals(model.email))
                    isSender = true;

                chatView.updateMessage(v, model.email, (String) DateFormat.format("dd-mm-yyyy HH:mm:ss", model.msgTime), text, isSender);
            }
        };

        messagesAdapter.startListening();
        chatView.setMessages(messagesAdapter);
    }

    public void sendMessage(String text) {
        byte[] encryptedMsg = messageProcessor.getEncryptedMsg(text, keyManager.getSecretKey(), keyManager.getIVspec());
        if (encryptedMsg != null) {
            chatModel.sendMessage(encryptedMsg);
        }
    }
}
