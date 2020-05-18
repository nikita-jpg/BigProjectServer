package com.example.tes;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class Authorization {
    @GetMapping("/testAuth")
    public String meth(@RequestParam(value = "key") String key){
        return key;
    }
}
