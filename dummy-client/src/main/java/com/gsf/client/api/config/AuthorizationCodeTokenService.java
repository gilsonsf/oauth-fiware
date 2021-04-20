package com.gsf.client.api.config;

import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.repository.TemplateMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorizationCodeTokenService {

    private Logger logger = LoggerFactory.getLogger(AuthorizationCodeTokenService.class);

    public String getAuthorizationEndpoint(ClientTemplate client) {

        Map<String, String> parametros = new HashMap<>();
        parametros.put("client_id", getEncodedUrl(client.getClientId()));
        parametros.put("redirect_uri", getEncodedUrl(client.getUrls().getRedirectUri()));
        parametros.put("response_type", client.getResponseType());
        parametros.put("scope", getEncodedUrl("read write"));
        parametros.put("state", client.getState());

        return buildUrl(client.getUrls().getUrlAuthorize(), parametros);

    }
    public String getClientCallBackEndpoint(String redirectUri, String authorizationCode, String state) {

        Map<String, String> parametros = new HashMap<>();
        parametros.put("code", authorizationCode);
        parametros.put("state", state);

        return buildUrl(redirectUri, parametros);

    }

    private String buildUrl(String authorizeEndpoint, Map<String, String> parametros) {
        List<String> authorizationParams = new ArrayList<>(parametros.size());

        parametros.forEach((param, valor) -> {
            authorizationParams.add(param + "=" + valor);
        });

        return authorizeEndpoint + "?" + authorizationParams.stream()
                .reduce((a,b) -> a + "&" + b).get();
    }

    private String getEncodedUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public OAuth2Token getToken(String authorizationCode, ClientTemplate client) {
        RestTemplate restTemplate = new RestTemplate();
        BasicAuthentication clientAuthentication = new BasicAuthentication(client.getClientId(), client.getSecret());

        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                getBody(authorizationCode, client.getUrls().getRedirectUri()),
                getHeader(clientAuthentication),
                HttpMethod.POST,
                URI.create(client.getUrls().getUrlToken())
        );

        ResponseEntity<OAuth2Token> responseEntity = restTemplate.exchange(
                requestEntity,
                OAuth2Token.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }

        throw new RuntimeException("error trying to retrieve access token");
    }

    public void getEntity(ClientTemplate client) {
        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                getHeader(client.getToken().getAccessToken()),
                HttpMethod.GET,
                URI.create(client.getUrls().getEntities())
        );

        try {
            HttpEntity<String> response = restTemplate.exchange(
                    requestEntity,
                    String.class);
            logger.info(response.getBody());
        } catch (HttpStatusCodeException e) {
            logger.info(e.getStatusCode() + " " + e.getResponseBodyAsString());
        }
    }

    private MultiValueMap<String, String> getBody(String authorizationCode, String redirectUri) {
        MultiValueMap<String, String> dadosFormulario = new LinkedMultiValueMap<>();

        dadosFormulario.add("grant_type", "authorization_code");
        dadosFormulario.add("code", authorizationCode);
        dadosFormulario.add("redirect_uri", redirectUri);

        return dadosFormulario;
    }

    private HttpHeaders getHeader(BasicAuthentication clientAuthentication) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Authorization", "Basic " + clientAuthentication.getCredenciaisBase64());

        return httpHeaders;
    }

    private HttpHeaders getHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("X-Auth-Token", token);

        return httpHeaders;
    }

}
