package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import com.gsf.client.api.entity.ClientTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import static com.gsf.client.api.repository.TemplateMemoryRepository.copyValues;
import static com.gsf.client.api.repository.TemplateMemoryRepository.getLoggedClient;
import static com.gsf.client.api.repository.TemplateMemoryRepository.setClientLogged;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/csrf")
    public String csrf() {
        return "csrf";
    }

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @GetMapping("/as")
    public String asvulnerable() {
        return "asvulnerable";
    }

    @GetMapping("/authorize")
    public String authorize(String redirect_uri, String state, String client_id) {
        setClientLogged(client_id);
        return "asvulnerable";
    }

    @GetMapping("/mixup/authorize")
    public ModelAndView mixUpAuthorize(String redirect_uri, String state, String client_id) {

        //passo 3

        setClientLogged(client_id);

        ClientTemplate loggedClient = copyValues(getLoggedClient());
        loggedClient.setState(state);
        loggedClient.getUrls().setRedirectUri(redirect_uri);

        String authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(loggedClient);

        return new ModelAndView("redirect:" + authorizationEndpoint);
    }

}
