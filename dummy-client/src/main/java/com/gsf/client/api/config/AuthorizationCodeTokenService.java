package com.gsf.client.api.config;

import com.gsf.client.api.entity.ClientTemplate;
import com.gsf.client.api.entity.OAuth2Token;
import com.gsf.client.api.repository.TemplateMemoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.UTF_8;

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

    public void execute(Consumer<String> consumer) {

        String authorizationCode = "uCHdRpiiCqDyZIfjjC5PnORZMJwhJR";
        ClientTemplate clientTemplate = TemplateMemoryRepository.findById(3);

        BasicAuthentication clientAuthentication = new BasicAuthentication("043260b7b56d43d08b87a8d7d78fa69c", "052c725f4d73493bb475556f5ff863bc");

        WebClient webClient = WebClient.builder()
                .baseUrl("https://account.lab.fiware.org")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();


        Mono<String> result = webClient.post()
                .uri("/oauth2/token")
                .header("Authorization", "Basic " + Base64Utils
                        .encodeToString(("043260b7b56d43d08b87a8d7d78fa69c" + ":" + "052c725f4d73493bb475556f5ff863bc").getBytes(UTF_8)))
                .body(BodyInserters.fromMultipartData(getBody(authorizationCode, clientTemplate.getUrls().getRedirectUri())))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse ->
                        Mono.error(new Exception())
                )
                .onStatus(HttpStatus::is5xxServerError, clientResponse ->
                        Mono.error(new Exception())
                )
                .bodyToMono(String.class);

        result.subscribe(consumer);

    }

    private static <T> void process(T t) {
        System.out.println("aqui ó >>" + t);
    }

    public static void main(String[] args) {
        new AuthorizationCodeTokenService().execute(str -> process(str));
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
