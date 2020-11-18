package com.example.note_taking_application.security;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decryption {


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String decrypt(String salt, String data, String iv, String password) throws Exception {

        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;

        int count = 1000;




        Base64.Decoder decoder=Base64.getDecoder();

        byte[] salt1=decoder.decode(salt);
        byte[] text=decoder.decode(data);
        byte[] iv1=decoder.decode(iv);


        pbeKeySpec = new PBEKeySpec(password.toCharArray(),salt1,count,256);
        keyFac=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] key=keyFac.generateSecret(pbeKeySpec).getEncoded();
        SecretKeySpec keySpec=new SecretKeySpec(key,"AES");




        IvParameterSpec ivParameterSpec=new IvParameterSpec(iv1);


        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");


        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        byte[] decryptes=cipher.doFinal(text);
        return new String(decryptes);
    }



}
