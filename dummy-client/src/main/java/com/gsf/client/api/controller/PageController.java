package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class PageController {

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @GetMapping("/")
    public String home() {
        return "home";
    }


}
