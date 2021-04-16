package com.vladyslav.encryptedchat.Managers;

import com.vladyslav.encryptedchat.EncryptProcessor.AESProcessor;
import com.vladyslav.encryptedchat.EncryptProcessor.KeyProcessor;
import com.vladyslav.encryptedchat.Models.KeyModel;
import com.vladyslav.encryptedchat.Models.KeyModel.Key;
import com.vladyslav.encryptedchat.lib.ExCallable;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {
    private KeyModel keyModel;
    private Key key;
    private AESProcessor aesProcessor;

    private static KeyManager instance;

    public static KeyManager getInstance(String chatId) {
        if (instance == null) {
            instance = new KeyManager();
            if (chatId != null) {
                instance.init(chatId);
            }
        }
        return instance;
    }

    private void init(String chatId) {
        keyModel = KeyModel.getInstance(chatId);
        aesProcessor = new AESProcessor();
    }

    /**
     * Инциализация ключа чата
     */
    public void initKey(ExCallable<Key> exCallable) {
        // Получение ключа из базы
        keyModel.getKey(serverKey -> {
            attachKey(serverKey, aVoid -> {
                exCallable.call(key);
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
            KeyProcessor initialKeyProcessor = new KeyProcessor();

            try {
                key = new Key(initialKeyProcessor.generateRandomKey(), initialKeyProcessor.getIvspec());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            key = serverKey;
            // TODO unblock input
        }

        keyModel.updateKey(key, exCallable);
    }

    /**
     * Ключ убирается
     */
    public void detachKey() {
        /*if (key != null) {
            keyModel.removeKeyUpdateListener();
            if (key.uses == 1) {
                keyModel.removeKey();
            } else {
                keyModel.updateKey(key, KeyUpdateType.DEC, aVoid -> {
                });
            }
        }*/
    }

    private IvParameterSpec getIVspec(List<Integer> ivspec) {
        byte[] bytes = new byte[ivspec.size()];
        for (int i = 0; i < ivspec.size(); ++i) {
            bytes[i] = ivspec.get(i).byteValue();
        }
        return new IvParameterSpec(bytes);
    }

    private SecretKey getSecretKey(List<Integer> key) {
        byte[] bytes = new byte[key.size()];
        for (int i = 0; i < key.size(); ++i) {
            bytes[i] = key.get(i).byteValue();
        }

        return new SecretKeySpec(bytes, "AES");
    }

    public byte[] getEncryptedMsg(String msg) {
        try {
            return aesProcessor.encrypt(msg, getSecretKey(key.generatedKey), getIVspec(key.ivspec));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getDecryptedMsg(List<Integer> msg) {
        try {
            byte[] bytes = new byte[msg.size()];
            for (int i = 0; i < msg.size(); ++i) {
                bytes[i] = msg.get(i).byteValue();
            }

            return aesProcessor.decrypt(bytes, getSecretKey(key.generatedKey), getIVspec(key.ivspec));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
