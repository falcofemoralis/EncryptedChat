package com.vladyslav.encryptedchat;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vladyslav.encryptedchat.Models.Message;

public class MainActivity extends AppCompatActivity {

    private static final int SIGN_IN_CODE = 1;
    private RelativeLayout mainLayout;
    private FirebaseListAdapter<Message> adapter;
    private FloatingActionButton sendBtn;

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
        sendBtn.setOnClickListener(v -> {
            EditText textField = findViewById(R.id.msgField);

            if(textField.getText().toString().equals(""))
                return;

            FirebaseDatabase.getInstance().getReference().push().setValue(new Message(
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            textField.getText().toString()
                    )
            );
            textField.setText("");
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        }
        else{
            showAlert("Вы авторизированы");
            checkKey();
            displayAllMessages();
        }
    }

    private void showAlert(String text) {
        Snackbar.make(mainLayout, text, Snackbar.LENGTH_LONG).show();
    }

    private void displayAllMessages() {
        ListView listOfMessages = findViewById(R.id.msgList);

        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(FirebaseDatabase.getInstance().getReference(), Message.class)
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

    private void checkKey(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Keys")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("CHAT_TESTING", document.getId() + " => " + document.getData());
                        }
                    } else {
                        Log.w("CHAT_TESTING", "Error getting documents.", task.getException());
                    }
                });
    }

    private void generateKey(){



        FirebaseFirestore db = FirebaseFirestore.getInstance();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference keys = database.getReference("Keys");
        keys.setValue("Hello, World!");
    }
}