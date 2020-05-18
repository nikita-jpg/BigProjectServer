package com.example.tes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;

@RestController
@RequestMapping("reg")
public class Registration {
    private final RegistrationWork work;

    @Autowired
    Registration(RegistrationWork work){
        this.work = work;
    }

    @GetMapping("/getPublicKeyForReg")
    public String getPublicKeyForReg(){
        return work.getPublicKeyForReg();
    }
    @GetMapping("/regPerson")
    public String regPerson(@RequestParam(value = "login") String login,@RequestParam(value = "password") String password,
                            @RequestParam(value = "login") String pKkey){
        return work.makePerson(login,password,pKkey);
    }

}
