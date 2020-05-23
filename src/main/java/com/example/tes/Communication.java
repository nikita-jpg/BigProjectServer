package com.example.tes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@RestController
@RequestMapping(value = "/",method = RequestMethod.POST)
public class Communication {
    private final CommunicationWork work;

    @Autowired
    Communication(CommunicationWork work){
        this.work = work;
    }


    @PostMapping("/regPerson")
    public String regPerson(@RequestParam String login,@RequestParam String password) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return work.regPerson(login,password);
    }

    @PostMapping("/autPerson")
    public String autPerson(@RequestParam String login,@RequestParam String password) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException, SQLException {
        return work.autPerson(login,password);
    }

    @RequestMapping(value="/uploadFile", method=RequestMethod.POST)
    public @ResponseBody int handleFileUpload(@RequestParam("foo") String name,
                                              @RequestParam("zam") MultipartFile zam,
                                              @RequestParam("fileName") String fileName){
         return work.uploadFile(name,zam,fileName);
    }

    @PostMapping("/getFileNameArr")
    public String[] getFileNameArr(String auth){
        return work.getFileNameArr(auth);
    }

    @PostMapping("/downloadFile")
    public File downloadFile(String auth,String name){
        return work.downloadFile(auth,name);
    }
}
