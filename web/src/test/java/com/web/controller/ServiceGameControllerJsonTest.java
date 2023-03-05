package com.web.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Configuration
public class ServiceGameControllerJsonTest {

    public String buildUrl(String endpoint, int port) {
        return "http://localhost:" + port + endpoint;
    }

     CsrfToken extractCSRFToken(String url, String tag, String attr, String sessionCookie, TestRestTemplate restTemplate, int port) {
        HttpHeaders headers = new HttpHeaders();
        if (sessionCookie != null) {
            headers.set("Cookie", sessionCookie);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(buildUrl(url, port), HttpMethod.GET, entity, String.class);
        String html = response.getBody();
        Document document = Jsoup.parse(html);
        Element csrfTokenElement = document.select(tag + "[name=_csrf]").first();
        String csrfToken = csrfTokenElement.attr(attr);

        try {
            sessionCookie = extractSessionCookie(response);
        } catch (NullPointerException e) {
        }
        return new  CsrfToken(csrfToken, sessionCookie);
    }

    public ResponseEntity<String> executeLogin(TestRestTemplate restTemplate, int port) {
        HttpHeaders headers = new HttpHeaders();
        CsrfToken token = extractCSRFToken("/login", "input", "value", null, restTemplate, port);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie", token.session);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "adam@gmail.com");
        map.add("password", "123");
        map.add("_csrf", token.token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForEntity(buildUrl("/login", port), request, String.class);
    }
    public ResponseEntity<Long> addSecondPlayerToGame(long userIdPly2, long gameId, TestRestTemplate restTemplate, int port) {
        HttpHeaders headers = setupHeadersRequestToGameController("/view/welcomeView", "meta",
                "content", restTemplate, port);

        HttpEntity<Void> requestWithToken = new HttpEntity<>(headers);
        return restTemplate.exchange(
                  buildUrl("/json/addSecondPlayer/"+userIdPly2+"/"+gameId, port)
                , HttpMethod.POST
                , requestWithToken
                , Long.class);
    }

    private String login(TestRestTemplate restTemplate, int port) {
        ResponseEntity<String> response = executeLogin(restTemplate, port);
        return extractSessionCookie(response);
    }

    private static String extractSessionCookie(ResponseEntity<String> response) {
        return response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
    }

    public ResponseEntity<String> addNewGameWithStatusGameForUser(long userId, TestRestTemplate restTemplate, int port) {
        String sessionCookie = login(restTemplate, port);
        CsrfToken token = extractCSRFToken("/view/welcomeView", "meta", "content", sessionCookie, restTemplate, port);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //TODO test jednostkowy na pobranie z bazy uzytkownika i zwrócenie id z tabeli
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.postForEntity(buildUrl("/json/game/save/" + userId, port),
                        request,
                        String.class);
    }

    public HttpHeaders setupHeadersRequestToGameController(String viewUrl, String tagCsrf, String attrCsrf, TestRestTemplate restTemplate, int port) {
        String sessionCookie = login(restTemplate, port);
        CsrfToken token = extractCSRFToken(viewUrl, tagCsrf, attrCsrf, sessionCookie, restTemplate, port);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//   Inny sposób dodawania nagłówków, gdy nie ma odpowiedniej metody .set..
//   headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        return headers;
    }

    class CsrfToken {
        public final String token;
        public final String session;

        public CsrfToken(String token, String session) {
            this.token = token;
            this.session = session;
        }
    }



}
