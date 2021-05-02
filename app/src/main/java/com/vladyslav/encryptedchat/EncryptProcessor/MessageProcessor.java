package com.vladyslav.encryptedchat.EncryptProcessor;

import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class MessageProcessor {
    private AESProcessor aesProcessor;

    public MessageProcessor() {
        aesProcessor = new AESProcessor();
    }

    public byte[] getEncryptedMsg(String msg, SecretKey key, IvParameterSpec ivSpec) {
        try {
            return aesProcessor.encrypt(msg, key, ivSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getDecryptedMsg(List<Integer> msg, SecretKey key, IvParameterSpec ivSpec) {
        try {
            byte[] bytes = new byte[msg.size()];
            for (int i = 0; i < msg.size(); ++i) {
                bytes[i] = msg.get(i).byteValue();
            }

            return aesProcessor.decrypt(bytes, key, ivSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
