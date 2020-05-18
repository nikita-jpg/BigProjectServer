package com.example.tes;

import java.security.KeyPair;
import java.security.PublicKey;

public interface RegistrationWork {

    String makePerson(String login,String password,String pKkey);
    String getPublicKeyForReg();
    KeyPair generateKeys();
    String decode(String str);
    String test();
}
