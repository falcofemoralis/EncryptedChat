package com.vladyslav.encryptedchat.EncryptProcessor;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class KeyProcessor {
    private final int keySize;
    private SecretKey secretKey;
    private IvParameterSpec ivspec; //init vector

    public KeyProcessor() {
        this.keySize = 128;

        //The block size required depends on the AES encryption block size.
        //For the default block size of 128 bits, we need an initialization vector of 16 bytes.
        byte[] iv = new byte[keySize / 8];
        SecureRandom sr = new SecureRandom();
        sr.nextBytes(iv);
        ivspec = new IvParameterSpec(iv);
    }

    public SecretKey generateRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        secretKey = keyGen.generateKey();
        return secretKey;
    }

    public IvParameterSpec getIvspec() {
        return ivspec;
    }
}