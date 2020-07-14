package com.git.notesr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ActivityTools extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static ClipboardManager clipboard;

    public static Context getAppContext() {
        return context;
    }

    public static Intent getIntent(Context context, Class<?> _class) {
        Intent intent = new Intent(context, _class);
        return intent;
    }

    public static void showTextMessage(String text, int duration, Context context) {
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public static void requirePermission(Activity activity, String permission) {
        ActivityCompat.requestPermissions(activity, new String[]{ permission }, 23);
    }

    public static String keyToHex(String key) {
        StringBuilder result = new StringBuilder();
        byte[] keyBytes = key.getBytes();
        int buff = 4;

        for(int i=0; i<keyBytes.length; i++) {
            result.append(Integer.toHexString(keyBytes[i]));

            if(i != keyBytes.length - 1){
                result.append(" ");
            }

            buff--;

            if(buff == 0){
                result.append("\n");
                buff = 4;
            }
        }

        return result.toString();
    }

    public static String hexToKey(String hex) {
        String[] hexArr = hex.replace("\n", "").split(" ");
        byte[] keyBytes = new byte[hexArr.length];

        for (int i=0; i<hexArr.length; i++) {
            keyBytes[i] = (byte)Integer.parseInt(hexArr[i], 16);
        }

        return new String(keyBytes);
    }

    public static void saveKey(Context context) throws Exception {
        if (Storage.isExternalStorageAvailable() && !Storage.isExternalStorageReadOnly()) {
            String aesPassword = md5(Config.pinCode);

            Storage.writeFile(context,Config.keyBinFileName, AES.Encrypt(Config.aesKey, //getAppContext()
                    AES.GenKey(aesPassword, md5(aesPassword))));
        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public static boolean getKeys(String pin, Context context) {

        if (Storage.isExternalStorageAvailable() && !Storage.isExternalStorageReadOnly()) {
            String encryptedKey = Storage.readFile(context,Config.keyBinFileName);

            try{
                String aesPassword = md5(pin);
                String aesKey = AES.Decrypt(encryptedKey, AES.GenKey(aesPassword,
                        md5(aesPassword)));

                Config.aesKey = aesKey;
                Config.pinCode = pin;

                return true;

            } catch (Exception e) {
                e.printStackTrace();

                return false;
            }

        } else {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);

            return false;
        }
    }

    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");

            digest.update(s.getBytes());

            byte[] messageDigest = digest.digest();

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String randomString(int len) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();

        int randomLength = generator.nextInt(len);
        char tempChar;

        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }

        return randomStringBuilder.toString();
    }
}
