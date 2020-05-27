package com.example.tes;

import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public interface CommunicationWork {

    String regPerson(String login, String password);
    String autPerson(String login,String password);
    String[] getFileNameArr(String auth);
    int uploadFile(String name, MultipartFile zam, String fileName, byte[] img);
    String[] downloadFile(String auth,String name);
}
