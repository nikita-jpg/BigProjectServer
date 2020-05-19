package com.example.tes;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

@Service
public class RegistrationWorkImp implements RegistrationWork {
    private PrivateKey privateKey;
    public PublicKey publicKey;


    RegistrationWorkImp(){
        KeyPair keyPair = null;
        keyPair = generateKeys();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }
    public KeyPair generateKeys(){
        KeyPair keyPair = null;
        try {
            keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return keyPair;
    }

    @Override
    public String[] makePerson(String arr[]) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {

        String request = "";

        String login = decode(arr[0]);
        String password = decode(arr[1]);

        String pkFromUser = "";
        for(int i = 2;i<10;i++){
            pkFromUser+=decode(arr[i]);
        }

        String[] byteValues = pkFromUser.substring(1, pkFromUser.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0; i<bytes.length; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pkKey = null;
        try {
            pkKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytes));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        try {
            request = LocalBase.makePerson(login,password,pkFromUser);
            for(int i =0;i<9;i++)
                arr[i] = encode(request.substring(i*(request.length()/10),(i+1)*(request.length()/10)),pkKey);
            arr[9] = encode(request.substring(9*request.length()/10),pkKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }

      return arr;

    }

    public String getPublicKeyForReg() {

        String req = Arrays.toString(publicKey.getEncoded());
        /*
        String[] byteValues = req.substring(1, req.length() - 1).split(",");
        byte[] bytes = new byte[byteValues.length];

        for (int i=0, len=bytes.length; i<len; i++) {
            bytes[i] = Byte.parseByte(byteValues[i].trim());
        }
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytes));


        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String test = "123456789012312";
        try {
            String enc = encode(test);
            test = decode(enc);
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
        String arr = test;
         */

        return "'"+req+"'";
    }

    @Override
    public String decode(String str) {

        String string ="";
        try {
            String[] byteValues = str.substring(1, str.length() - 1).split(",");
           byte[] bytes = new byte[byteValues.length];

           for (int i=0, len=bytes.length; i<len; i++)
            {
                bytes[i] = Byte.parseByte(byteValues[i].trim());
            }
            Cipher cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher2.init(Cipher.DECRYPT_MODE,privateKey);
            byte[] req = cipher2.doFinal(bytes);
            string = new String(req, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string;
    }

    @Override
    public String encode(String str,PublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] ret = cipher.doFinal(str.getBytes());
        String string = Arrays.toString(ret);
        return string;
    }

    @Override
    public String test() {
        try {
            return LocalBase.getPublKey("log");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

}
