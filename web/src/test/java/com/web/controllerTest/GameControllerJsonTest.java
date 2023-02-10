package com.web.controllerTest;


import dataConfig.Position;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;
import ship.Ship;

import javax.xml.parsers.DocumentBuilderFactory;

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

    @Test
    void testAddShipToDataBaseWithCsrfToken() {
        //pobranie csrf token za pomocą metody GET
        HttpHeaders headersCSRF = new HttpHeaders();
        HttpEntity<Void> request = new HttpEntity<>(headersCSRF);
        ResponseEntity<String> response = restTemplate.withBasicAuth("admin@gmail.com", "123")
                .getForEntity("http://localhost:"+port+"/view/getParamGame", String.class);
        String html = response.getBody();

        Document document = Jsoup.parse(html);
        Element csrfTokenElement = document.select("meta[name=_csrf]").first();
        String csrfToken = csrfTokenElement.attr("content");
        System.out.println("Nagłóweki:  " + response.getHeaders());

        System.out.println("CSRF token>>>>>>>>>>>>>>> " + csrfToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-CSRF-TOKEN", csrfToken);


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
