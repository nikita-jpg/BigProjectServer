package com.example.tes;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public int uploadFile(String auth, MultipartFile file, String fileName, byte[] img){
        String arr = null;
        if(img!= null) {
            arr = new String(img);
        }
        return LocalBase.uploadFile(auth,file,fileName,arr);
    }

    @Override
    public String[] downloadText(String auth, String name) {
        return LocalBase.downloadFile(auth,name);
    }

    @Override
    public String downloadImage(String auth, String name) {
        return LocalBase.downloadImage(auth,name);
    }


    public String[] getFileNameArr(String auth){
        return LocalBase.getFileNameArr(auth);
    }


}
