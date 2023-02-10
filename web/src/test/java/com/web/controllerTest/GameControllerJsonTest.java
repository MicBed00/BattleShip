package com.web.controllerTest;


import dataConfig.Position;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ship.Ship;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({@Sql("classpath:/init_fixtures.sql")})
public class GameControllerJsonTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

//    @Value("${spring.security.user.name}")
//    private String userName;
//
//    @Value("${spring.security.user.password}")
//    private String password;

    CsrfToken extractCSRFToken(String url, String tag, String attr, String sessionCookie) {
        //"http://localhost:"+port+ - napisz metodę buildUrl()
        HttpHeaders headers = new HttpHeaders();
        if(sessionCookie != null){
            headers.set("Cookie", sessionCookie);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange("http://localhost:"+port+url,HttpMethod.GET ,entity, String.class);
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

    private ResponseEntity<String> executeLogin() {
        HttpHeaders headers = new HttpHeaders();
        CsrfToken token = extractCSRFToken("/login", "input", "value", null);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie", token.session);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", "adam@gmail.com");
        map.add("password", "123");
        map.add("_csrf", token.token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:"+port+"/login" , request , String.class );
        return response;
    }

    private String login() {
        ResponseEntity<String> response = executeLogin();
        return extractSessionCookie(response);
    }

    private static String extractSessionCookie(ResponseEntity<String> response) {
        return response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
    }

    @Test
    void loginTest(){
        ResponseEntity<String> response = executeLogin();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Set-Cookie").get(0).startsWith("JSESSIONID")).isTrue();
        assertThat(response.getHeaders().get("Location").get(0).contains("/view/welcomeView")).isTrue();
    }



    @Test
    void testAddShipToDataBaseWithCsrfToken() {
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //pobranie csrf token za pomocą metody GET
//        HttpHeaders headersCSRF = new HttpHeaders();
//        HttpEntity<Void> request = new HttpEntity<>(headersCSRF);
//        ResponseEntity<String> response = restTemplate.
//                .getForEntity("http://localhost:"+port+"/view/getParamGame", String.class);
//        String html = response.getBody();

//        Document document = Jsoup.parse(html);
//        Element csrfTokenElement = document.select("meta[name=_csrf]").first();
//        String csrfToken = csrfTokenElement.attr("content");
//        System.out.println("Nagłóweki:  " + response.getHeaders());
//
//        System.out.println("CSRF token>>>>>>>>>>>>>>> " + csrfToken);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("X-CSRF-TOKEN", csrfToken);
//
//
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> responseWithCsrfToken = restTemplate
                .postForEntity("http://localhost:"+port+"/json/addShip",
                requestWithToken,
                String.class
        );
        // TODO stacktrace pokaże, dlaczego dostajemy 500 tutaj - 	at com.web.service.UserService.getLastUserGames(UserService.java:79) - trzeba najpierw zacząć grę zanim dodamy statek :)
        //then
        assertThat(responseWithCsrfToken.getStatusCode()).isEqualTo(HttpStatus.OK);
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
