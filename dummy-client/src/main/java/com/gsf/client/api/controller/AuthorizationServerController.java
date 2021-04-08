package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.entity.TemplateUrls;
import com.gsf.client.api.repository.ClientTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;

@RestController
@RequestMapping("/authorizationServer")
public class AuthorizationServerController {

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ModelAndView authorize(String redirect_uri, String state, String client_id) {
        ClientTemplate client = ClientTemplateRepository.findById(1);
        ClientTemplate clientAttacker = new ClientTemplate();

        clientAttacker.setClientId("tutorial-dckr-site-0000-xpresswebapp");
        clientAttacker.setState(state);

        TemplateUrls urls = new TemplateUrls();
        urls.setUrlAuthorize("http://localhost:3005/oauth2/authorize");
        urls.setRedirectUri(client.getUrls().getRedirectUri());
        clientAttacker.setUrls(urls);

        clientAttacker.setResponseType(client.getResponseType());

        String authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(clientAttacker);

        //manda pro H-AS
        return new ModelAndView("redirect:" + authorizationEndpoint);
    }


    @PostMapping(value = "/oauth2/token")
    public ResponseEntity token(@RequestHeader("Authorization") String authorization,
                                @RequestBody String body) {

        String authorizationCode = body
                    .split("&")[1]
                    .split("=")[1];

        System.out.println("Authorization: " + authorization);
        System.out.println("Access Code: " + authorizationCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
