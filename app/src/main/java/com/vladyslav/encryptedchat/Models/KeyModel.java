package com.vladyslav.encryptedchat.Models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.vladyslav.encryptedchat.lib.ExCallable;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class KeyModel {
    private static final String KEY_STORAGE = "key"; // Путь к расположению ключа в базе
    private static KeyModel instance;
    private DatabaseReference keyRef; // ССылка на ключа в базе
    private ValueEventListener keyEventListener;

    /**
     * Класс ключа
     * generatedKey - сгенерированный ключ
     * uses - количество использований данного ключа
     */
    public static class Key {
        public List<Integer> generatedKey;
        public List<Integer> ivspec;
        public boolean inUse;

        public Key() {
        }

        public Key(SecretKey generatedKey, IvParameterSpec ivspec) {
            byte[] keyBytes = generatedKey.getEncoded();
            this.generatedKey = new ArrayList<>();
            for (byte keyByte : keyBytes)
                this.generatedKey.add((int) keyByte);

            byte[] ivSpecBytes = ivspec.getIV();
            this.ivspec = new ArrayList<>();
            for (byte ivspecByte : ivSpecBytes)
                this.ivspec.add((int) ivspecByte);

            this.inUse = false;
        }
    }

    public static KeyModel getInstance(String chatId) {
        if (instance == null) {
            instance = new KeyModel();
            instance.init(chatId);
        }
        return instance;
    }

    public static void delete(){
        instance = null;
    }

    /**
     * Инциализация
     */
    private void init(String chatId) {
        keyRef = FirebaseDatabase.getInstance().getReference(KEY_STORAGE + "/" + chatId);
    }

    /**
     * Получение ключа с сервера
     *
     * @param exCallable - колбек
     */
    public void getKey(ExCallable<Key> exCallable) {
        keyRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        exCallable.call(task.getResult().getValue(Key.class));
                    } else {
                        Log.e(DEBUG_TAG, "Error getting data", task.getException());
                    }
                }
        );
    }

    /**
     * Удаление ключа в базе
     */
    public void removeKey() {
        keyRef.removeValue();
    }

    /**
     * Добавление прослушивания обновлений ключа
     *
     * @param listener - листенер
     */
    public void addKeyUpdateListener(ValueEventListener listener) {
        keyEventListener = listener;
        keyRef.addValueEventListener(listener);
    }

    /**
     * Удаление прослушивания обновлений ключа
     */
    public void removeKeyUpdateListener() {
        if (keyEventListener != null) {
            keyRef.removeEventListener(keyEventListener);
            keyEventListener = null;
        }
    }

    /**
     * Обновление ключа в базе
     *
     * @param key        - ключ
     * @param exCallable - колбек
     */
    public void updateKey(Key key, ExCallable<Void> exCallable) {
        keyRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Key serverKey = currentData.getValue(Key.class);

                if (serverKey == null) {
                    serverKey = key;
                } else {
                    serverKey.inUse = true;
                }

                currentData.setValue(serverKey);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                exCallable.call(null);
            }
        });
    }
}
