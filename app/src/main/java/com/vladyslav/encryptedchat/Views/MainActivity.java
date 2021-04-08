package com.vladyslav.encryptedchat.Views;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.vladyslav.encryptedchat.Controllers.KeyController;
import com.vladyslav.encryptedchat.Models.Message;
import com.vladyslav.encryptedchat.R;

public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_CODE = 1;
    private RelativeLayout mainLayout;
    private FirebaseListAdapter<Message> adapter;
    private FloatingActionButton sendBtn;
    private static final String CHATS_STORAGE = "chats";
    private KeyController keyController;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                showAlert("Вы авторизированы");
                displayAllMessages();
            } else {
                showAlert("Вы не авторизированы");
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.activity_main);
        sendBtn = findViewById(R.id.btnSend);

        keyController = KeyController.getInstance();

        // Добавление обработчика на кнопку отправить
        sendBtn.setOnClickListener(v -> {
            EditText textField = findViewById(R.id.msgField);

            // Если ничего не введенно
            if (textField.getText().toString().equals(""))
                return;

            Log.d("CHAT_DEBUG", "generated key: " + keyController.getCryptographyKey());

            // Отправляем сообщение
            FirebaseDatabase.getInstance().getReference(CHATS_STORAGE).push().setValue(new Message(
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            textField.getText().toString()
                    )
            );

            // Очищаем текстовое поле
            textField.setText("");
        });

        // Проверка авторизован ли пользователь
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // В случае первого входа - показать окно регистрации\входа
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        } else {
            showAlert("Вы авторизированы");
            keyController.initKey();
            displayAllMessages();
        }
    }

    private void showAlert(String text) {
        Snackbar.make(mainLayout, text, Snackbar.LENGTH_LONG).show();
    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.msgList);

        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(FirebaseDatabase.getInstance().getReference(CHATS_STORAGE), Message.class)
                .setLayout(R.layout.list_item)
                .build();

        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                ((TextView) v.findViewById(R.id.msg_user)).setText(model.getUserName());
                ((TextView) v.findViewById(R.id.msg_time)).setText(DateFormat.format("dd-mm-yyyy HH:mm:ss", model.getMsgTime()));
                ((TextView) v.findViewById(R.id.msg_text)).setText(model.getMsgText());
            }
        };

        adapter.startListening();
        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        keyController.detachKey();
        super.onStop();
    }
}