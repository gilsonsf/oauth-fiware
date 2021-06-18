package com.gsf.authorizationserver.controller;

import com.gsf.authorizationserver.config.AuthorizationCodeTokenService;
import com.gsf.authorizationserver.entity.ClientTemplate;
import com.gsf.authorizationserver.repository.TemplateMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @GetMapping("/")
    public String home() {
        return "asvulnerable";
    }

    @GetMapping("/authorize")
    public String authorize(String redirect_uri, String state, String client_id) {
        TemplateMemoryRepository.setClientLogged(client_id);
        return "asvulnerable";
    }

    @GetMapping("/mixup/authorize")
    public ModelAndView mixUpAuthorize(String redirect_uri, String state, String client_id) {

        TemplateMemoryRepository.setClientLogged(client_id);

        ClientTemplate loggedClient = TemplateMemoryRepository.copyValues(TemplateMemoryRepository.getLoggedClient());
        loggedClient.setState(state);
        loggedClient.getUrls().setRedirectUri(redirect_uri);

        String authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(loggedClient);

        return new ModelAndView("redirect:" + authorizationEndpoint);
    }

}
