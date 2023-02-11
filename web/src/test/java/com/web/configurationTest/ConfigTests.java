package com.web.configurationTest;

import com.web.controllerTest.GameControllerJsonTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

//@Configuration
public class ConfigTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    public TestRestTemplate getRestTemplate() {
        return restTemplate;
    }

    String buildUrl(String endpoint) {
        return "http://localhost:"+port+endpoint;
    }

    CsrfToken extractCSRFToken(String url, String tag, String attr, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        if(sessionCookie != null){
            headers.set("Cookie", sessionCookie);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(buildUrl(url),HttpMethod.GET ,entity, String.class);
        String html = response.getBody();
        Document document = Jsoup.parse(html);
        Element csrfTokenElement = document.select(tag+"[name=_csrf]").first();
        String csrfToken = csrfTokenElement.attr(attr);

        try {
            sessionCookie = extractSessionCookie(response);
        } catch (NullPointerException e) {
        }
        return new CsrfToken(csrfToken, sessionCookie);
    }

    public ResponseEntity<String> executeLogin() {
        HttpHeaders headers = new HttpHeaders();
        CsrfToken token = extractCSRFToken("/login", "input", "value", null);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie", token.session);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", "adam@gmail.com");
        map.add("password", "123");
        map.add("_csrf", token.token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(buildUrl("/login"), request , String.class );
        return response;
    }

    private String login() {
        ResponseEntity<String> response = executeLogin();
        return extractSessionCookie(response);
    }

    private static String extractSessionCookie(ResponseEntity<String> response) {
        return response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
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
