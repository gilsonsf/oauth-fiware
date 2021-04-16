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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/client")
public class ClientController {

    private Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    private ClientTemplate loggedClient;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ModelAndView authorize(String authorizationName) {

        this.loggedClient = TemplateMemoryRepository.copyValues(TemplateMemoryRepository.findByAS(authorizationName));

        String authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(this.loggedClient);

        return new ModelAndView("redirect:" + authorizationEndpoint);


//        if(authorizationName.equalsIgnoreCase("fiwarelab")) {
//            client = TemplateMemoryRepository.findByAS("vulnerable-https");
//
//            ClientTemplate clientAttacker = new ClientTemplate();
//
//            clientAttacker.setClientId(client.getClientId());
//            clientAttacker.setState("fiwarelab_state");
//
//            TemplateUrls urls = new TemplateUrls();
//            urls.setUrlAuthorize(client.getUrls().getUrlAuthorize());
//            urls.setRedirectUri(client.getUrls().getRedirectUri());
//            clientAttacker.setUrls(urls);
//
//            clientAttacker.setResponseType(client.getResponseType());
//
//            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(clientAttacker);
//
//        } else {

    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public ResponseEntity callback(String code, String state) {

        OAuth2Token token = authorizationCodeTokenService.getToken(code, this.loggedClient);
        this.loggedClient.setToken(token);
        LOGGER.info("Client with Access Token: " + this.loggedClient);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
