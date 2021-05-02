package com.vladyslav.encryptedchat.Views.Fragments;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vladyslav.encryptedchat.Managers.KeyManager;
import com.vladyslav.encryptedchat.Models.ChatModel;
import com.vladyslav.encryptedchat.Models.ChatModel.Message;
import com.vladyslav.encryptedchat.Presenters.ChatPresenter;
import com.vladyslav.encryptedchat.R;
import com.vladyslav.encryptedchat.ViewsInterfaces.ChatView;

import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class ChatFragment extends Fragment implements ChatView, View.OnClickListener {
    private View currentFragment;
    private ChatPresenter chatPresenter;
    private ListView msgList;
    private FloatingActionButton sendBtn;
    private EditText textField;
    private String chatId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatId = getArguments().getString("chatId");
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentFragment = inflater.inflate(R.layout.fragment_chat, container, false);
        msgList = currentFragment.findViewById(R.id.msgList);
        sendBtn = currentFragment.findViewById(R.id.btnSend);
        textField = currentFragment.findViewById(R.id.msgField);
        chatPresenter = new ChatPresenter(this, chatId);
        sendBtn.setOnClickListener(this);
        return currentFragment;
    }

    @Override
    public void setMessages(FirebaseListAdapter<Message> adapter) {
        msgList.setAdapter(adapter);
    }

    @Override
    public void updateMessage(View v, String username, String time, String text, boolean isSender) {
        ((TextView) v.findViewById(R.id.msg_user)).setText(username);
        ((TextView) v.findViewById(R.id.msg_time)).setText(time);
        BubbleTextView msgTextView = v.findViewById(R.id.msg_text);
        msgTextView.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (textField.getText().toString().equals(""))
            return;
        chatPresenter.sendMessage(textField.getText().toString());
        textField.setText("");
    }

    @Override
    public void onDestroy() {
        ChatModel.delete();
        KeyManager.delete();
        super.onDestroy();
    }
}