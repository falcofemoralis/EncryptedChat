package com.vladyslav.encryptedchat.EncryptProcessor;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AESProcessor {
    private Cipher cipher;

    public AESProcessor() {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String text, SecretKey key, IvParameterSpec ivSpec) throws
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            UnsupportedEncodingException,
            BadPaddingException,
            IllegalBlockSizeException {

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        byte[] inputText = text.getBytes("UTF-8");
        return cipher.doFinal(inputText);
    }

    public String decrypt(byte[] text, SecretKey key, IvParameterSpec ivSpec) throws
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] encryptedText = text.clone();
        return new String(cipher.doFinal(encryptedText));
    }
}