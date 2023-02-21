package com.web.controller;

import java.lang.Void;

import board.Shot;
import board.StatePreperationGame;
import com.web.service.GameStatusService;
import dataConfig.Position;
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
import ship.Ship;

import java.util.*;

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

    @Autowired
    ServiceGameControllerJsonTest serviceTests;


    @DirtiesContext
    @Test
    void loginTest() {
        ResponseEntity<String> response = serviceTests.executeLogin(restTemplate, port);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().get("Set-Cookie").get(0).startsWith("JSESSIONID")).isTrue();
        assertThat(response.getHeaders().get("Location").get(0).contains("/view/welcomeView")).isTrue();
    }

    @DirtiesContext
    @Test
    void logout() {
        ResponseEntity<String> response = restTemplate.getForEntity(serviceTests.buildUrl("/logout", port),
                String.class);
        assertThat(response.getHeaders().get("Location").get(0).contains("/login")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }

    @DirtiesContext
    @Test
    void shouldAddNewGameForUser() {
        //when
        long userId = 1;
        ResponseEntity<String> response = serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);

        //then
        assertThat(Objects.equals(response.getBody(), "true")).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    //TODO test zawsze przechodzi
//    @DirtiesContext
//    @Test
//    void shouldThrowExceptionShipLimitExceedFor1MastedShipsWhenAddedToManyShips() {
//        //given
//        HttpHeaders headers = configTests.setupHeadersRequestToGameController("/view/getParamGame", "meta", "content", restTemplate, port);
//        long userId = 1;
//        configTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);
//
//        //when
//        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
//        HttpEntity<Ship> requestShip1 = new HttpEntity<>(ship1, headers);
//        restTemplate
//                .postForEntity(configTests.buildUrl("/json/addShip", port)
//                       , requestShip1
//                        , String.class);
//        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
//        HttpEntity<Ship> requestShip2 = new HttpEntity<>(ship2, headers);
//        restTemplate
//                .postForEntity(configTests.buildUrl("/json/addShip", port)
//                        , requestShip2
//                        , String.class);
//        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
//        HttpEntity<Ship> requestShip3 = new HttpEntity<>(ship3, headers);
//        restTemplate
//                .postForEntity(configTests.buildUrl("/json/addShip", port)
//                        , requestShip3
//                        , String.class);
//        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
//        HttpEntity<Ship> requestShip4 = new HttpEntity<>(ship4, headers);
//        restTemplate
//                .postForEntity(configTests.buildUrl("/json/addShip", port)
//                        , requestShip4
//                        , String.class);
//
//        try {
//            Ship ship5 = new Ship(1, 8, 0, Position.VERTICAL);
//            HttpEntity<Ship> requestShip5 = new HttpEntity<>(ship5, headers);
//            ResponseEntity<String> responseException = restTemplate
//                    .postForEntity(configTests.buildUrl("/json/addShip", port)
//                            , requestShip5
//                            , String.class);
//        }catch(ShipLimitExceedException e) {
//            assertThat(e)
//                    .isInstanceOf(ShipLimitExceedException.class)
//                    .hasMessageContaining("1 masted ships limit has been reached. Press enter and re-enter the data");
//        }
//    }

    @DirtiesContext
    @Test
    void shouldAddShipToDataBase() {
        //given
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta",
                                                                            "content", restTemplate, port);
        //when
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> response = restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestWithToken
                        , String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @DirtiesContext
    @Test
    void shouldFillOutTwoListShip() {
        //given
        long userId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                    ,"content", restTemplate, port);
        serviceTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip1 = new HttpEntity<>(ship1, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip1
                        , String.class);
        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip2 = new HttpEntity<>(ship2, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip2
                        , String.class);
        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip3 = new HttpEntity<>(ship3, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip3
                        , String.class);
        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip4 = new HttpEntity<>(ship4, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip4
                        , String.class);
        Ship ship5 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip5 = new HttpEntity<>(ship5, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip5
                        , String.class);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip6 = new HttpEntity<>(ship6, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip6
                        , String.class);
        Ship ship7 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip7 = new HttpEntity<>(ship7, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip7
                        , String.class);
        Ship ship8 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip8 = new HttpEntity<>(ship8, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip8
                        , String.class);
        Ship ship9 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip9 = new HttpEntity<>(ship9, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip9
                        , String.class);
        Ship ship10 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip10 = new HttpEntity<>(ship10, headers);
        ResponseEntity<String> responseWithFullShipLists = restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip10
                        , String.class);

        //then, port
        assertThat(responseWithFullShipLists.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseWithFullShipLists.getBody().equals("true")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldReturnSetupBoard() {
        //when
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                , "content", restTemplate, port);

        //given
        HttpEntity<Void> requestWithToken = new HttpEntity<>(headers);
        ResponseEntity<Integer> response = restTemplate.exchange(serviceTests.buildUrl("/json/setup", port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                    , "content", restTemplate, port);

        //given
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/lastGame/" + userId, port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                    , "content", restTemplate, port);
        //given
        long userId = 1;
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/lastGame/" + userId, port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta", "content", restTemplate, port);

        //when
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<StatePreperationGame> response = restTemplate.exchange(
                serviceTests.buildUrl("/json/game/boards/phaseGame/" + userId, port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                    , "content", restTemplate, port);

        //when
        long userId = 1;
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    serviceTests.buildUrl("/json/game/boards/phaseGame/1", port)
                    , HttpMethod.GET
                    , request
                    , String.class);
        } catch (NoSuchElementException e) {
            assertThat(e)
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("User has not yet added the ship");
        }
    }

    @DirtiesContext
    @Test
    void shouldUpdatePhaseGame() {
        //given
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                , "content", restTemplate, port);

        //when
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        String status = "REJECTED";
        HttpEntity<String> request = new HttpEntity<>(status, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/rejected/1", port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                    , "content", restTemplate, port);

        //when
        long userId = 1;
        //tutaj dodaję game w tabeli games i statusGame w tabeli gameStatuses
        serviceTests.addNewGameWithStatusGameForUser(1, restTemplate, port);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/listBoard/1", port)
                , HttpMethod.GET
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
    void shouldRestoreConfigurationGameOnServer() {
        //given
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                    , "content", restTemplate, port);
        //when
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/setupBoard/1", port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                , "content", restTemplate, port);
        //when
        long userId = 1;
        long indexBoard = 0;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> response1 = restTemplate.postForEntity(serviceTests.buildUrl("/json/addShip", port)
                , requestWithToken
                , String.class);
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("indexBoard", indexBoard);
        HttpEntity<Map<String, Long>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/deleteShip/1/0", port)
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
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                , "content", restTemplate, port);
        //when
        long userId = 1;
        long indexBoard = 0;
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userId);
        map.put("indexBoard", indexBoard);
        try {
            HttpEntity<Map<String, Long>> request = new HttpEntity<>(map, headers);
            ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/deleteShip/1/0", port)
                    , HttpMethod.DELETE
                    , request
                    , String.class);

        } catch (NoSuchElementException e) {
            assertThat(e)
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("User has not yet added the ship");
        }
    }

    @DirtiesContext
    @Test
    void shouldReturnGameView() {
        //given
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/added_Ship", "meta"
                                                                                    , "content", restTemplate, port);
        //when
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/view/game", port)
                , HttpMethod.GET
                , request
                , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @DirtiesContext
    @Test
    void shouldThrowExceptionWhenTryGetGameViewWithoutGameStarted() {
        //given
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/added_Ship", "meta"
                                                                                , "content", restTemplate, port);
        //when
        try {
            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/view/game", port)
                    , HttpMethod.GET
                    , request
                    , String.class);

        } catch (NoSuchElementException e) {
            assertThat(e)
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("User has not yet added the ship");
        }
    }

    @DirtiesContext
    @Test
    void shouldThrowExceptionEmptyShipList() {
        //given
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        //when
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/game", "meta"
                                                                                , "content", restTemplate, port);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/game/boards/isFinished/" + userId, port)
                    , HttpMethod.GET
                    , request
                    , String.class);

        }catch (NoSuchElementException e){
            assertThat(e)
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Empty ship list");
        }
    }

    @DirtiesContext
    @Test
    void shouldReturnTrueGameIsNotFinished() {
        //given
        long userId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame", "meta"
                                                                                , "content", restTemplate, port);
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip1 = new HttpEntity<>(ship1, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip1
                        , String.class);
        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip2 = new HttpEntity<>(ship2, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip2
                        , String.class);
        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip3 = new HttpEntity<>(ship3, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip3
                        , String.class);
        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip4 = new HttpEntity<>(ship4, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip4
                        , String.class);
        Ship ship5 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip5 = new HttpEntity<>(ship5, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip5
                        , String.class);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip6 = new HttpEntity<>(ship6, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip6
                        , String.class);
        Ship ship7 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip7 = new HttpEntity<>(ship7, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip7
                        , String.class);
        Ship ship8 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip8 = new HttpEntity<>(ship8, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip", port)
                        , requestShip8
                        , String.class);
        Ship ship9 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip9 = new HttpEntity<>(ship9, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip",port)
                        , requestShip9
                        , String.class);
        Ship ship10 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip10 = new HttpEntity<>(ship10, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip",port)
                        , requestShip10
                        , String.class);
        //when
        HttpHeaders headersGame = serviceTests.setupHeadersRequestToGameController("/view/game", "meta"
                                                                                    , "content", restTemplate, port);
        HttpEntity<Void> request = new HttpEntity<>(headersGame);
            ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/game/boards/isFinished/" + userId, port)
                    , HttpMethod.GET
                    , request
                    , String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().equals("false")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldAddShotToOneBoard() {
        //given
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/game", "meta"
                                                                                    , "content", restTemplate, port);
        //when
        Shot shot = new Shot(2, 4);
        HttpEntity<Shot> request = new HttpEntity<>(shot, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(serviceTests.buildUrl("/json/game/boards", port)
                , request
                , String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().equals("[{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}" +
                ",{\"ships\":[],\"opponentShots\":[{\"x\":2,\"y\":4,\"state\":\"MISSED\"}],\"hittedShip\":[]}]")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldAddShotToTwoBoard() {
        //given
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/game", "meta"
                                                                                    , "content", restTemplate, port);
        //when
        Shot shot = new Shot(2, 4);
        Shot shot1 = new Shot(2, 4);

        HttpEntity<Shot> requestFirstShot = new HttpEntity<>(shot, headers);
        ResponseEntity<String> responseFirstShot = restTemplate.postForEntity(serviceTests.buildUrl("/json/game/boards", port)
                , requestFirstShot
                , String.class);
        HttpEntity<Shot> requestSecondShot = new HttpEntity<>(shot1, headers);
        ResponseEntity<String> responseSecondShot = restTemplate.postForEntity(serviceTests.buildUrl("/json/game/boards", port)
                , requestSecondShot
                , String.class);
        //then
        assertThat(responseFirstShot.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseFirstShot.getBody().equals("[{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}" +
                ",{\"ships\":[],\"opponentShots\":[{\"x\":2,\"y\":4,\"state\":\"MISSED\"}],\"hittedShip\":[]}]")).isTrue();
        assertThat(responseSecondShot.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseSecondShot.getBody().equals("[{\"ships\":[],\"opponentShots\":[{\"x\":2,\"y\":4,\"state\":\"MISSED\"}],\"hittedShip\":[]}" +
                ",{\"ships\":[],\"opponentShots\":[{\"x\":2,\"y\":4,\"state\":\"MISSED\"}],\"hittedShip\":[]}]")).isTrue();
    }


}
