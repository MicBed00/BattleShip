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

    String extractCSRFToken(String url, String tag, String attr, String sessionCookie) {
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



        return csrfToken;
    }

    private ResponseEntity<String> executeLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("username", "adam@gmail.com");
        map.add("password", "123");
        map.add("_csrf", extractCSRFToken("/login", "input", "value", null));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:"+port+"/login" , request , String.class );
        return response;
    }

    private String login() {
        ResponseEntity<String> response = executeLogin();
        return response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
    }
    @Test
    void loginTest(){
        ResponseEntity<String> response = executeLogin();
        //TODO dodać assercję na header Location
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Set-Cookie").get(0).startsWith("JSESSIONID")).isTrue();
    }



    @Test
    void testAddShipToDataBaseWithCsrfToken() {
        String sessionCookie = login();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie));

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
        ResponseEntity<Boolean> responseWithCsrfToken = restTemplate
                .postForEntity("http://localhost:"+port+"/json/addShip",
                requestWithToken,
                Boolean.class
        );
        //then
        assertThat(responseWithCsrfToken.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
