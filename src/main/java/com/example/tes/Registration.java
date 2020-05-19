package com.example.tes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

@RestController
@RequestMapping("reg")
public class Registration {
    private final RegistrationWork work;

    @Autowired
    Registration(RegistrationWork work){
        this.work = work;
    }

    @PostMapping("/getPublicKeyForReg")
    public String getPublicKeyForReg(){
        return work.getPublicKeyForReg();
    }

    @PostMapping("/regPerson")
    public String[] regPerson(@RequestBody String[] arr) throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        return work.makePerson(arr);
    }

}
