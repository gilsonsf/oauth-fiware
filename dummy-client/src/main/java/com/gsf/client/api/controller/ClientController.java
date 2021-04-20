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

import java.util.Objects;

@RestController
@RequestMapping("/client")
public class ClientController {

    private Logger LOGGER = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    private ClientTemplate loggedClient;

    private String asName;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ModelAndView authorize(String authorizationName) throws CloneNotSupportedException {

        this.asName = authorizationName;
        String authorizationEndpoint = null;
        String[] as = this.asName.split("_");

        if(as.length == 2) {
            //attacker scenario

            //passo 2
//            ClientTemplate honest = TemplateMemoryRepository.copyValues(TemplateMemoryRepository.findByAS(as[0]));
//            this.loggedClient = TemplateMemoryRepository.copyValues(TemplateMemoryRepository.findByAS(as[1])); //this is attacker

            ClientTemplate honest = TemplateMemoryRepository.findByAS(as[0]).clone();
            this.loggedClient = TemplateMemoryRepository.findByAS(as[1]).clone(); //this is attacker

            honest.getUrls().setUrlAuthorize(this.loggedClient.getUrls().getUrlAuthorize());
            honest.setState(this.loggedClient.getState());

            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(honest);

        } else {
            //honest
            this.loggedClient = TemplateMemoryRepository.findByAS(as[0]).clone();
            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(this.loggedClient);

        }

        return new ModelAndView("redirect:" + authorizationEndpoint);

    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public ResponseEntity callback(String code, String state) throws CloneNotSupportedException {


        if("".equalsIgnoreCase(state)) {
            LOGGER.info("Client was attacked by CSRF  : " + code);
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        String[] as = this.asName.split("_");

        if (as.length == 2) {
            ClientTemplate honest = TemplateMemoryRepository.findByAS(as[0]).clone();
            this.loggedClient.setClientId(honest.getClientId());
            this.loggedClient.setSecret(honest.getSecret());
        }

        OAuth2Token token = null;
        if("fiwarelab".equalsIgnoreCase(loggedClient.getAuthorizationServerName())) {
            //provisorio
            token = TemplateMemoryRepository.createTokenFiwareLabTemporary(code);

        } else {
            token = authorizationCodeTokenService.getToken(code, this.loggedClient);
        }

        if (Objects.nonNull(token)) {
            this.loggedClient.setToken(token);
            LOGGER.info("Client with Access Token: " + this.loggedClient);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
