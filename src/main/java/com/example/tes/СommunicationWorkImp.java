package com.example.tes;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.*;
import java.io.File;
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

    @Override
    public int uploadFile(String auth, MultipartFile file,String fileName){
        return LocalBase.uploadFile(auth,file,fileName);
    }

    @Override
    public File downloadFile(String auth, String name) {
        return LocalBase.downloadFile(auth,name);
    }

    public String[] getFileNameArr(String auth){
        return LocalBase.getFileNameArr(auth);
    }

}
