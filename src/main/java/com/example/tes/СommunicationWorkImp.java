package com.example.tes;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

@Service
public class СommunicationWorkImp implements CommunicationWork {

    @Override
    public String regPerson(String login, String password) {
        return LocalBase.makePerson(login,password);
    }

    @Override
    public String autPerson(String login, String password){
        return LocalBase.autPerson(login,password);
    }


}
