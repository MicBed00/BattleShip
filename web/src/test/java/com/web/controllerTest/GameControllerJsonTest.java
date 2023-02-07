package com.web.controllerTest;


import dataConfig.Position;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import ship.Ship;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GameControllerJsonTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testAddShipToDataBaseWithCsrfToken() {
        //pobranie csrf token za pomocą metody GET
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Integer> response = restTemplate.withBasicAuth("adam@gmail.com", "123")
                .exchange("/json/csrf", HttpMethod.GET, request, Integer.class);
        System.out.println("Nagłóweki:  " + response.getHeaders());

        String csrfToken = response.getHeaders().getFirst("X-CSRF-TOKEN");
        System.out.println("CSRF token>>>>>>>>>>>>>>> " + csrfToken);

        headers = new HttpHeaders();
        headers.set("X-CSRF-TOKEN", csrfToken);


        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<Boolean> responseWithCsrfToken = restTemplate
                .withBasicAuth("adam@gmail.com", "123")
                .postForEntity(
                "/json/addShip",
                requestWithToken,
                Boolean.class
        );
        //then
        assertThat(responseWithCsrfToken.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
