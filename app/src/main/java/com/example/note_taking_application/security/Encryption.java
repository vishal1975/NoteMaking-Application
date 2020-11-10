package com.example.note_taking_application.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {


    public HashMap<String,Object> encrypt(String password,String data) throws Exception {

        HashMap<String,Object> info=new HashMap<>();

        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;


        byte[] salt = new byte[8];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        int count = 1000;
        // pbeParamSpec = new PBEParameterSpec(salt, count);


        pbeKeySpec = new PBEKeySpec(password.toCharArray(),salt,count,256);

        keyFac=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key=keyFac.generateSecret(pbeKeySpec).getEncoded();
        SecretKeySpec keySpec=new SecretKeySpec(key,"AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        SecureRandom ivrandom=new SecureRandom();
        byte[] iv=new byte[16];
        ivrandom.nextBytes(iv);

        IvParameterSpec ivParameterSpec=new IvParameterSpec(iv);





        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        byte[] text = data.getBytes();
        byte[] ciphertext = cipher.doFinal(text);
        info.put("salt",salt);
        info.put("iv",iv);
        info.put("ciphertext",ciphertext);
        return info;
    }



}
