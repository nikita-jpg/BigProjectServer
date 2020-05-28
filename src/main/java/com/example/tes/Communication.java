package com.example.tes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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

    @RequestMapping(value="/uploadText", method=RequestMethod.POST)
    public @ResponseBody int handleFileUpload(@RequestParam("foo") String auth,
                                              @RequestParam("zam") MultipartFile zam,
                                              @RequestParam("fileName") String fileName){
         return work.uploadFile(auth,zam,fileName,null);
    }

    @PostMapping("/getFileNameArr")
    public String[] getFileNameArr(String auth){
        return work.getFileNameArr(auth);
    }

    @PostMapping("/downloadFile")
    public String[] downloadText(String auth, String name){
        return work.downloadText(auth,name);
    }

    @PostMapping("/downloadImage")
    public String downloadImage(String auth,String name){
        return work.downloadImage(auth,name);
    }

    @PostMapping("/uploadImage")
    public int uploadImage(@RequestParam("auth") String auth,
                           @RequestParam("fileName") String fileName,
                           @RequestBody byte[] imgArrByte){
        return work.uploadFile(auth,null,fileName,imgArrByte);
    }
}
