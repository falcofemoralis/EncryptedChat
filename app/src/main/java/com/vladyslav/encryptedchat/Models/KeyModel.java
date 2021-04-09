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
import com.vladyslav.encryptedchat.Constants.KeyUpdateType;
import com.vladyslav.encryptedchat.lib.ExCallable;

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
        public String generatedKey;
        public int uses;

        public Key() {
        }

        public Key(String generatedKey, int uses) {
            this.generatedKey = generatedKey;
            this.uses = uses;
        }
    }

    public static KeyModel getInstance() {
        if (instance == null) {
            instance = new KeyModel();
            instance.init();
        }
        return instance;
    }

    /**
     * Инциализация
     */
    private void init() {
        keyRef = FirebaseDatabase.getInstance().getReference(KEY_STORAGE);
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
     * @param updateType - тип обновления
     * @param exCallable - колбек
     */
    public void updateKey(Key key, KeyUpdateType updateType, ExCallable<Void> exCallable) {
        keyRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Key serverKey = currentData.getValue(Key.class);

                if (serverKey == null) {
                    serverKey = key;
                    currentData.setValue(serverKey);
                    return Transaction.success(currentData);
                }

                if (updateType == KeyUpdateType.INC)
                    serverKey.uses += 1;
                else if (updateType == KeyUpdateType.DEC)
                    serverKey.uses -= 1;

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
