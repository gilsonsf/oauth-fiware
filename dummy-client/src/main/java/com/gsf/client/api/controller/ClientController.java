package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.entity.TemplateUrls;
import com.gsf.client.api.repository.TemplateMemoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ModelAndView authorize(String authorizationName) {


        ClientTemplate client = null;
        String authorizationEndpoint = null;

        if(authorizationName.equalsIgnoreCase("fiwarelab")) {
            client = TemplateMemoryRepository.findByAS("vulnerable");

            ClientTemplate clientAttacker = new ClientTemplate();

            clientAttacker.setClientId(client.getClientId());
            clientAttacker.setState("fiwarelab_state");

            TemplateUrls urls = new TemplateUrls();
            urls.setUrlAuthorize(client.getUrls().getUrlAuthorize());
            urls.setRedirectUri(client.getUrls().getRedirectUri());
            clientAttacker.setUrls(urls);

            clientAttacker.setResponseType(client.getResponseType());

            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(clientAttacker);

        } else {
            client = TemplateMemoryRepository.findByAS(authorizationName);
            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(client);
        }

        return new ModelAndView("redirect:" + authorizationEndpoint);
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public ResponseEntity callback(String code, String state) {

        ClientTemplate client = TemplateMemoryRepository.findByState(state);

        OAuth2Token token = authorizationCodeTokenService.getToken(code, client);

        if(nonNull(token)) {
            System.out.println(token.getAccessToken());
            System.out.println(token);

            client.setToken(token);
            TemplateMemoryRepository.update(client);

            for (int i = 0; i < 3 ; i++) {
                authorizationCodeTokenService.getEntity(client);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
