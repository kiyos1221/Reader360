package com.a360ground.epubreader360.EpubManipulation.Encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Key;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Kiyos Solomon on 10/17/2016.
 */
public class Encryption {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String PrefName = "DeviceInfo";
    Context context;
    public Encryption(Context context){
        this.context = context;
    }
    private String keyGen(){
        String key;
                if(readFromPreferences(context,PrefName,null)==null){
                    String uuid =UUID.randomUUID().toString();
                    key = uuid.replaceAll("-","").substring(0,16);
                    saveToPreferences(context,PrefName,key);
                }
                else{
                    key = readFromPreferences(context,PrefName,null);
                }
        Log.d("KEY ",key);
        return key;
    }
    public void encrypt(File file) throws Exception{
        Key secretKey = new SecretKeySpec(keyGen().getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        FileInputStream inputStream = new FileInputStream(file);
        byte[] inputBytes = new byte[(int) file.length()];
        inputStream.read(inputBytes);
        byte[] outputBytes = cipher.doFinal(inputBytes);
        FileOutputStream outputStream = new FileOutputStream(file.getParent()+File.separator+file.getName().substring(0,file.getName().length()-5)+".lomi");
        outputStream.write(outputBytes);
        inputStream.close();
        outputStream.close();
        Log.d("Encrypted",file.getAbsolutePath()+" is successfully encrypted");
    }
    public InputStream decrypt(File file) throws Exception{
        Key secretKey = new SecretKeySpec(keyGen().getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        FileInputStream fileinputStream = new FileInputStream(file);
        byte[] inputBytes = new byte[(int) file.length()];
        fileinputStream.read(inputBytes);
        byte[] outputBytes = cipher.doFinal(inputBytes);
        Log.d("Decrypted",file.getAbsolutePath()+" is successfully decrypted");
        return new ByteArrayInputStream(outputBytes);
    }

    public void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public String readFromPreferences(Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}