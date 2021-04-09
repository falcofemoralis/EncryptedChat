package com.vladyslav.encryptedchat.Views;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.vladyslav.encryptedchat.R;
import com.vladyslav.encryptedchat.Views.Fragments.MainFragment;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {
    private static final int SIGN_IN_CODE = 1;
  //  private FirebaseListAdapter<Message> adapter;
    private FloatingActionButton sendBtn;
    private static final String CHATS_STORAGE = "chats";

    private LinearLayout mainLayout;
    private FragmentManager fragmentManager;
    private static Fragment mainFragment;
    public static final String DEBUG_TAG = "CHAT_DEBUG";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                showAlert("Вы авторизированы");
                showMainFragment();
                //displayAllMessages();
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

        mainLayout = findViewById(R.id.activity_main_ll_container);

        fragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            // Проверка авторизован ли пользователь
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                // В случае первого входа - показать окно регистрации\входа
                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
            } else {
                showAlert("Вы авторизированы");
                showMainFragment();
                //   keyManager.initKey();
                //   displayAllMessages();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Fragment fragmentSource, Fragment fragmentReceiver, OnFragmentInteractionListener.Action action, Bundle data, String backStackTag) {
        FragmentTransaction fTrans = fragmentManager.beginTransaction();
        if (fragmentReceiver != null)
            fragmentReceiver.setArguments(data);

        int animIn = R.anim.fade_in, animOut = R.anim.fade_out;

        fTrans.setCustomAnimations(animIn, animOut, animIn, animOut);

        switch (action) {
            case NEXT_FRAGMENT_NO_BACK_STACK:
                fTrans.replace(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.commit();
                break;
            case NEXT_FRAGMENT_HIDE:
                if (mainFragment.isVisible())
                    fTrans.hide(mainFragment);
                else
                    fTrans.hide(fragmentSource);

                fTrans.add(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case NEXT_FRAGMENT_REPLACE:
                fTrans.replace(R.id.activity_main_ll_container, fragmentReceiver);
                fTrans.addToBackStack(backStackTag);   // Добавление изменнений в стек
                fTrans.commit();
                break;
            case RETURN_FRAGMENT_BY_TAG:
                fragmentManager.popBackStack(backStackTag, 0);
                break;
            case POP_BACK_STACK:
                fragmentManager.popBackStack();
                break;
        }
    }

    private void showMainFragment() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Инициализация менеджера смены фрагментов
        mainFragment = new MainFragment();

        // Открытие фрагмента главного меню
        fragmentManager.beginTransaction()
                .add(R.id.activity_main_ll_container, mainFragment)
                .commit();
    }

    private void showAlert(String text) {
        Snackbar.make(mainLayout, text, Snackbar.LENGTH_LONG).show();
    }



      /*  mainLayout = findViewById(R.id.activity_main);
        sendBtn = findViewById(R.id.btnSend);

        keyManager = KeyManager.getInstance();

        // Добавление обработчика на кнопку отправить
        sendBtn.setOnClickListener(v -> {
            EditText textField = findViewById(R.id.msgField);

            // Если ничего не введенно
            if (textField.getText().toString().equals(""))
                return;

            Log.d(DEBUG_TAG, "generated key: " + keyManager.getCryptographyKey());

            // Отправляем сообщение
            FirebaseDatabase.getInstance().getReference(CHATS_STORAGE).push().setValue(new Message(
                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            textField.getText().toString()
                    )
            );

            // Очищаем текстовое поле
            textField.setText("");
        });


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
        keyManager.detachKey();
        super.onStop();
    }*/
}