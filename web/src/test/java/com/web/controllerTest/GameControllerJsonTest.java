package com.web.controllerTest;


import board.StatePreperationGame;
import com.web.service.GameStatusService;
import dataConfig.Position;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ship.Ship;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql("classpath:/init_fixtures.sql")
public class GameControllerJsonTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    GameStatusService gameStatusService;


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

    private ResponseEntity<String> executeLogin() {
        HttpHeaders headers = new HttpHeaders();
        CsrfToken token = extractCSRFToken("/login", "input", "value", null);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Cookie", token.session);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "adam@gmail.com");
        map.add("password", "123");
        map.add("_csrf", token.token);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
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

    private ResponseEntity<String> addNewGameForUser(Long userId) {
        //given
        String sesssionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sesssionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sesssionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //when
        //TODO test jednostkowy na pobranie z bazy uzytkownika i zwrócenie id z tabeli
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .postForEntity(buildUrl("/json/game/save/"+userId),
                        request,
                        String.class);

        return response;
    }

    @DirtiesContext
    @Test
    void loginTest(){
        ResponseEntity<String> response = executeLogin();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Set-Cookie").get(0).startsWith("JSESSIONID")).isTrue();
        assertThat(response.getHeaders().get("Location").get(0).contains("/view/welcomeView")).isTrue();
    }
    @DirtiesContext
    @Test
    void logout() {;
        ResponseEntity<String> response = restTemplate.getForEntity(buildUrl("/logout"),
                String.class);
        assertThat(response.getHeaders().get("Location").get(0).contains("/login")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }
    @DirtiesContext
    @Test
    void shouldAddNewGameForUser() {
        //given
        long userId = 1;
        ResponseEntity<String> response = addNewGameForUser(userId);

        //then
        assertThat(Objects.equals(response.getBody(), "true")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @DirtiesContext
    @Test
    void testAddShipToDataBaseWithCsrfToken() {
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//   Inny sposób dodawania nagłówków, gdy nie ma odpowiedniej metody .set..
//   headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        long userId = 1;
        addNewGameForUser(userId);
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> response = restTemplate
                .postForEntity(buildUrl("/json/addShip")
                    , requestWithToken
                    , String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @DirtiesContext
    @Test
    void shouldReturnSetupBoard() {
        //when
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //given
        HttpEntity<Void> requestWithToken = new HttpEntity<>(headers);
        ResponseEntity<Integer> response = restTemplate.exchange(buildUrl("/json/setup")
                , HttpMethod.GET
                , requestWithToken
                , Integer.class);

        //then
        assertThat(response.getBody()).isEqualTo(gameStatusService.getShipLimits());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DirtiesContext
    @Test
    void shouldFindGameForUser() {
        //when
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //given
        long userId = 1;
        addNewGameForUser(userId);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/lastGame/"+userId)
                , HttpMethod.GET
                , request
                , String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("true");
    }

    @DirtiesContext
    @Test
    void shouldNotFindGameForUser() {
        //when
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //given
        long userId = 1;
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/lastGame/"+userId)
                , HttpMethod.GET
                , request
                , String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("false");
    }
    @DirtiesContext
    @Test
    void shouldReturneUserPhaseGame() {
        //given
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //when
        long userId = 1;
        addNewGameForUser(userId);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<StatePreperationGame> response = restTemplate.exchange(
                buildUrl("/json/game/boards/phaseGame/"+userId)
                , HttpMethod.GET
                , request
                , StatePreperationGame.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(StatePreperationGame.IN_PROCCESS);
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
