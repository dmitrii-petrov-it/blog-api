package org.example.blogapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/public/hello")
    public String publicHello(){
        return "Hello all";
    }

    @GetMapping("/api/private/hello")
    public String privateHello(Authentication auth){
        return "Hello " + auth.getName();
    }

    @GetMapping("/api/admin/hello")
    public String adminHello(Authentication auth){
        return "Hello admin " + auth.getName();
    }


}
