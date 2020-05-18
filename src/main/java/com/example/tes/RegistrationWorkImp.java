package com.example.tes;

import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.sql.SQLException;
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
    public String makePerson(String login, String password,String pkKey) {
        login = decode(login);
        password = decode(password);
        pkKey = decode(pkKey);
        return LocalBase.makePerson(login,password,pkKey);
    }

    public String getPublicKeyForReg() {
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(publicKey.getEncoded()),StandardCharsets.UTF_8);
    }

    @Override
    public String decode(String str) {
        String string = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE,privateKey);
            string = (cipher.doFinal(str.getBytes())).toString();
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
        }
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
