package com.gsf.client.api.config;

import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
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

    public String getAuthorizationEndpoint() {

        BasicAuthentication clientAuthentication = new BasicAuthentication("cliente-app", "123456");

        // recuperando os dados necessários
        // para o endpoint de autorização

        String endpointDeAutorizacao = "http://localhost:8080/oauth/authorize";

        // montando a URI
        Map<String, String> parametros = new HashMap<>();
        parametros.put("client_id", getEncodedUrl(clientAuthentication.getLogin()));
        parametros.put("response_type", "code");
        parametros.put("redirect_uri", getEncodedUrl("http://localhost:9000/integracao/callback"));
        parametros.put("scope", getEncodedUrl("read write"));

        return construirUrl(endpointDeAutorizacao, parametros);

    }

    public String getAuthorizationEndpoint(ClientTemplate client) {

        Map<String, String> parametros = new HashMap<>();
        parametros.put("client_id", getEncodedUrl(client.getClientId()));
        parametros.put("redirect_uri", getEncodedUrl(client.getUrls().getRedirectUri()));
        parametros.put("response_type", client.getResponseType());
        parametros.put("scope", getEncodedUrl("read write"));
        parametros.put("state", client.getState());

        return construirUrl(client.getUrls().getUrlAuthorize(), parametros);

    }

    private String construirUrl(String authorizeEndpoint, Map<String, String> parametros) {
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

    public String extractCode(String url) {
       return url.split("\\?")[1]
               .split("&")[0]
               .split("=")[1];
    }

    public OAuth2Token getToken(String authorizationCode, ClientTemplate client) {
        RestTemplate restTemplate = new RestTemplate();
        BasicAuthentication clientAuthentication = new BasicAuthentication(client.getClientId(), client.getSecret());

        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                getBodyKeyrock(authorizationCode, client.getUrls().getRedirectUri()),
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

        // isso deve ser tratado de forma melhor (apenas para exemplo)
        throw new RuntimeException("error trying to retrieve access token");
    }

    public void getEntity(ClientTemplate client) {
        RestTemplate restTemplate = new RestTemplate();

        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                getHeader(client.getToken().getAccessToken()),
                HttpMethod.GET,
                URI.create("http://localhost:1027/v2/entities/")
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

    private MultiValueMap<String, String> getBody(String authorizationCode) {
        MultiValueMap<String, String> dadosFormulario = new LinkedMultiValueMap<>();

        dadosFormulario.add("grant_type", "authorization_code");
        dadosFormulario.add("code", authorizationCode);
        dadosFormulario.add("scope", "read write");
        dadosFormulario.add("redirect_uri",
                "http://localhost:9000/integracao/callback");

        return dadosFormulario;
    }

    private MultiValueMap<String, String> getBodyKeyrock(String authorizationCode, String redirectUri) {
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
