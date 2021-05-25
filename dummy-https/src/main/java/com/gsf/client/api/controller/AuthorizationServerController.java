package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.repository.TemplateMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.UUID;

@Controller
@RequestMapping("/authorizationServer")
public class AuthorizationServerController {

    private Logger LOGGER = LoggerFactory.getLogger(AuthorizationServerController.class);

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    private ClientTemplate loggedClientAS;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ModelAndView authorize(String email, String password,
                                  String redirect_uri, String state, String client_id, HttpServletRequest request) {

        System.out.println(email + " " + password);
        System.out.println("state >>" + state);

        if (state == null) {
            state = "";
        }

        String authorizationCode = "ASDummy_" + UUID.randomUUID().toString();
        this.loggedClientAS = TemplateMemoryRepository.getLoggedClient();
        String redirectClientEndpoint = authorizationCodeTokenService.getClientCallBackEndpoint(loggedClientAS.getUrls().getRedirectUri(), authorizationCode, state);

        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:" + redirectClientEndpoint);
    }


    @PostMapping(value = "/oauth2/token")
    public ResponseEntity<OAuth2Token> token(@RequestHeader("Authorization") String authorization,
                                             @RequestBody String body) {

        String authorizationCode = body
                    .split("&")[1]
                    .split("=")[1];

        String credentials = new String(
                Base64.getDecoder()
                        .decode(authorization.split(" ")[1].getBytes()));

        ClientTemplate client = TemplateMemoryRepository.findByClientId(credentials.split(":")[0]);
        OAuth2Token token = TemplateMemoryRepository.createToken(authorizationCode);
        client.setToken(token);

        LOGGER.info("AS Dummy responding to: " + client);

        return new ResponseEntity<>(token, HttpStatus.OK);

    }

    @PostMapping(value = "/mixup/oauth2/token")
    public ResponseEntity<OAuth2Token> mixupToken(@RequestHeader("Authorization") String authorization,
                                                  @RequestBody String body) {

        String authorizationCode = body
                .split("&")[1]
                .split("=")[1];

        String credentials = new String(
                Base64.getDecoder()
                        .decode(authorization.split(" ")[1].getBytes()));

        OAuth2Token token = new OAuth2Token();
        ClientTemplate client = TemplateMemoryRepository.findByClientId(credentials.split(":")[0]);

        token.setTokenType("Client was attacked by Mix Up : " + client);
        LOGGER.info("Client was attacked by Mix Up : " + client);

        return new ResponseEntity<>(token, HttpStatus.OK);

    }

}
