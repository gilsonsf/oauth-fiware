package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import com.gsf.client.api.repository.TemplateMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @GetMapping("/")
    public String home() {
        return "asvulnerable";
    }

    @GetMapping("/authorize")
    public String asVulnerable(String redirect_uri, String state, String client_id) {
        TemplateMemoryRepository.setClientLogged(client_id);
        return "asvulnerable";
    }

}
