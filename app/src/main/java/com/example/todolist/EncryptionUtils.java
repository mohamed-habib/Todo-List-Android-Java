package com.example.todolist;

import android.util.Base64;

import androidx.annotation.Nullable;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

class EncryptionUtils {
    public static String encrypt(byte[] key, byte[] data) {
        byte[] cipherText = null;
        try {
            SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
            cipherText = cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(cipherText, Base64.DEFAULT);
    }

    @Nullable
    public static byte[] generateKey(byte[] randomNumber) {
        SecretKey sKey = null;

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(randomNumber);
            keyGen.init(256, random);

            sKey = keyGen.generateKey();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (sKey != null) {
            return sKey.getEncoded();
        } else return null;
    }


    public static String decrypt(byte[] key, byte[] encryptedData) {
        byte[] clearText = null;

        SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
            clearText = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return new String(clearText);
    }


}
