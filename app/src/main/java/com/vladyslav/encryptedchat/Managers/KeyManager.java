package com.vladyslav.encryptedchat.Managers;

import android.util.Log;

import com.vladyslav.encryptedchat.Constants.KeyUpdateType;
import com.vladyslav.encryptedchat.Models.KeyModel;
import com.vladyslav.encryptedchat.Models.KeyModel.Key;
import com.vladyslav.encryptedchat.lib.ExCallable;

import static com.vladyslav.encryptedchat.Views.MainActivity.DEBUG_TAG;

public class KeyManager {
    private KeyModel keyModel;
    private Key key;

    private static KeyManager instance;

    public static KeyManager getInstance() {
        if (instance == null) {
            instance = new KeyManager();
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
                        Log.d(DEBUG_TAG, "key will be updated: ");

                        attachKey(snapshot.getValue(Key.class), aVoid -> {
                            Log.d(DEBUG_TAG, "key was updated: ");
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
            Log.d(DEBUG_TAG, "new key uses: " + key.uses);
        } else {
            if (serverKey.uses < 2) {
                key = serverKey;
                keyModel.updateKey(key, KeyUpdateType.INC, exCallable);
                // TODO unblock input
                Log.d(DEBUG_TAG, "key uses: " + key.uses);
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
