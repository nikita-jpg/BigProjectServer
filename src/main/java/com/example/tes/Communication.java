package com.example.tes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
