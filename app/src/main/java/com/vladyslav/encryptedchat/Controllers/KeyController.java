package com.vladyslav.encryptedchat.Controllers;

import android.util.Log;

import com.vladyslav.encryptedchat.Constants.KeyUpdateType;
import com.vladyslav.encryptedchat.Models.KeyModel;
import com.vladyslav.encryptedchat.Models.KeyModel.Key;
import com.vladyslav.encryptedchat.lib.ExCallable;

public class KeyController {
    private KeyModel keyModel;
    private Key key;

    private static KeyController instance;

    public static KeyController getInstance() {
        if (instance == null) {
            instance = new KeyController();
            instance.init();
        }
        return instance;
    }

    private void init() {
        keyModel = KeyModel.getInstance();
    }

    /**
     * Инциализация ключа чата
     */
    public void initKey() {
        // Получение ключа из базы
        keyModel.getKey(serverKey -> {
            attachKey(serverKey, aVoid -> {
                // Листенер на прослушивание обновления ключа
/*                keyModel.addKeyUpdateListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("CHAT_DEBUG", "key will be updated: ");

                        attachKey(snapshot.getValue(Key.class), aVoid -> {
                            Log.d("CHAT_DEBUG", "key was updated: ");
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Toast.makeText(getApplicationContext(), "Failed to get key.", Toast.LENGTH_SHORT).show();
                    }
                });*/
            });
        });
    }

    /**
     * Предоставление ключа
     *
     * @param serverKey - ключ, который пришел с сервера
     */
    private void attachKey(Key serverKey, ExCallable<Void> exCallable) {
        if (serverKey == null) {
            // Generate key
            key = new Key("123312", 1);
            keyModel.updateKey(key, KeyUpdateType.NEW, exCallable);
            Log.d("CHAT_DEBUG", "new key uses: " + key.uses);
        } else {
            if (serverKey.uses < 2) {
                key = serverKey;
                keyModel.updateKey(key, KeyUpdateType.INC, exCallable);
                // TODO unblock input
                Log.d("CHAT_DEBUG", "key uses: " + key.uses);
            }
        }
    }

    /**
     * Ключ убирается
     */
    public void detachKey() {
        if (key != null) {
            keyModel.removeKeyUpdateListener();
            if (key.uses == 1) {
                keyModel.removeKey();
            } else {
                keyModel.updateKey(key, KeyUpdateType.DEC, aVoid -> {
                });
            }
        }
    }

    /**
     * Получение сгенерированного ключа
     *
     * @return - сненерированный ключ для шифрования данных
     */
    public String getCryptographyKey() {
        return key.generatedKey;
    }
}
