package com.web.controllerTest;

import java.lang.Void;
import board.StatePreperationGame;
import com.web.service.GameStatusService;
import dataConfig.Position;
import exceptions.ShipLimitExceedException;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ship.Ship;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.as;
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
        return "http://localhost:" + port + endpoint;
    }

    CsrfToken extractCSRFToken(String url, String tag, String attr, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        if (sessionCookie != null) {
            headers.set("Cookie", sessionCookie);
        }
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate
                .exchange(buildUrl(url), HttpMethod.GET, entity, String.class);
        String html = response.getBody();
        Document document = Jsoup.parse(html);
        Element csrfTokenElement = document.select(tag + "[name=_csrf]").first();
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
        return restTemplate.postForEntity(buildUrl("/login"), request, String.class);
    }

    private String login() {
        ResponseEntity<String> response = executeLogin();
        return extractSessionCookie(response);
    }

    private static String extractSessionCookie(ResponseEntity<String> response) {
        return response.getHeaders().get("Set-Cookie").get(0).split(";")[0];
    }

    private ResponseEntity<String> addNewGameWithStatusGameForUser(long userId) {
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        //TODO test jednostkowy na pobranie z bazy uzytkownika i zwrócenie id z tabeli
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate
                .postForEntity(buildUrl("/json/game/save/" + userId),
                        request,
                        String.class);

    }

    private HttpHeaders setupHeadersRequestToGameControllerJson() {
        String sessionCookie = login();
        CsrfToken token = extractCSRFToken("/view/getParamGame", "meta", "content", sessionCookie);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//   Inny sposób dodawania nagłówków, gdy nie ma odpowiedniej metody .set..
//   headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set("Cookie", sessionCookie);
        headers.set("X-CSRF-TOKEN", token.token);

        return headers;
    }

    @DirtiesContext
    @Test
    void loginTest() {
        ResponseEntity<String> response = executeLogin();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Set-Cookie").get(0).startsWith("JSESSIONID")).isTrue();
        assertThat(response.getHeaders().get("Location").get(0).contains("/view/welcomeView")).isTrue();
    }

    @DirtiesContext
    @Test
    void logout() {
        ResponseEntity<String> response = restTemplate.getForEntity(buildUrl("/logout"),
                String.class);
        assertThat(response.getHeaders().get("Location").get(0).contains("/login")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @DirtiesContext
    @Test
    void shouldAddNewGameForUser() {
        //when
        long userId = 1;
        ResponseEntity<String> response = addNewGameWithStatusGameForUser(userId);

        //then
        assertThat(Objects.equals(response.getBody(), "true")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    //TODO test nie działa poprawnie!
    @DirtiesContext
    @Test
    void shouldThrowExceptionShipLimitExceedFor1MastedShipsWhenAddedToManyShips() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        addNewGameWithStatusGameForUser(userId);
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
        Ship ship5 = new Ship(1, 8, 0, Position.VERTICAL);

        Map<String, Ship> map = new HashMap();
        map.put("ship1", ship1);
        map.put("ship2", ship2);
        map.put("ship3", ship3);
        map.put("ship4", ship4);
        map.put("ship5", ship5);

        HttpEntity<Map<String, Ship>> requestWithToken = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String> response = restTemplate
                    .postForEntity(buildUrl("/json/addShip")
                            , requestWithToken
                            , String.class);
        }catch(ShipLimitExceedException e) {
            assertThat(e)
                    .isInstanceOf(ShipLimitExceedException.class)
                    .hasMessageContaining("1 masted ships limit has been reached. Press enter and re-enter the data");
        }

    }

    @DirtiesContext
    @Test
    void shouldAddShipToDataBase() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        addNewGameWithStatusGameForUser(userId);
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
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

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
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //given
        long userId = 1;
        addNewGameWithStatusGameForUser(userId);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/lastGame/" + userId)
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
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //given
        long userId = 1;
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/lastGame/" + userId)
                , HttpMethod.GET
                , request
                , String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("false");
    }

    @DirtiesContext
    @Test
    void shouldReturnUserPhaseGame() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        addNewGameWithStatusGameForUser(userId);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<StatePreperationGame> response = restTemplate.exchange(
                buildUrl("/json/game/boards/phaseGame/" + userId)
                , HttpMethod.GET
                , request
                , StatePreperationGame.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(StatePreperationGame.IN_PROCCESS);
    }
    @DirtiesContext
    @Test
    void shouldThrowExceptionTryingReturnUserPhaseGame() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    buildUrl("/json/game/boards/phaseGame/1")
                    , HttpMethod.GET
                    , request
                    , String.class);
        } catch(NoSuchElementException e) {
            assertThat(e)
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("User has not yet added the ship");
        }
    }

    @DirtiesContext
    @Test
    void shouldUpdatePhaseGame() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        addNewGameWithStatusGameForUser(userId);
        String status = "REJECTED";
        HttpEntity<String> request = new HttpEntity<>(status, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/rejected/1")
                , HttpMethod.POST
                , request
                , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    //TODO pytanie: Co w sytuacji gdy logika programu nie powinna
    // dopuścić do wywołania tej metody w przypadku gdy nie ma statusu gry w tabeli -> wtedy jest przekierowanie na inny endpoint.
    //Czy testuję się takie metody pod kątem, gdy nie ma statusu gry w tabeli?
    @DirtiesContext
    @Test
    void shouldReturnStatusGame() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        //tutaj dodaję game w tabeli games i statusGame w tabeli gameStatuses
        addNewGameWithStatusGameForUser(1);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/listBoard/1")
                , HttpMethod.GET
                , request
                , String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()
                .equals("[{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}" +
                        ",{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}]")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldRestoreConfigurationGameOnServer() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();
        //when
        long userId = 1;
        addNewGameWithStatusGameForUser(userId);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/setupBoard/1")
                                    , HttpMethod.POST
                                    , request
                                    , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().equals("true")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldDeleteShipFromStatusGame() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        long indexBoard = 0;
        addNewGameWithStatusGameForUser(userId);
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> response1 = restTemplate.postForEntity(buildUrl("/json/addShip")
                , requestWithToken
                , String.class);
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("indexBoard", indexBoard);
        HttpEntity<Map<String, Long>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/deleteShip/1/0")
                                , HttpMethod.DELETE
                                , request
                                , String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()
                .equals("[{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}" +
                        ",{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}]")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldThrowExceptionWhenDeleteShipFromEmptyStatusGame() {
        //given
        HttpHeaders headers = setupHeadersRequestToGameControllerJson();

        //when
        long userId = 1;
        long indexBoard = 0;
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("indexBoard", indexBoard);
        try{
            HttpEntity<Map<String, Long>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(buildUrl("/json/deleteShip/1/0")
                    , HttpMethod.DELETE
                    , request
                    , String.class);

        } catch(NoSuchElementException e) {
            assertThat(e)
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("User has not yet added the ship");
        }
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
