package com.web.controllers;

import java.io.IOException;
import java.lang.Void;

import board.Board;
import board.Shot;
import board.StateGame;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.services.GameStatusService;
import dataConfig.Position;
import org.junit.Test;
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
import static org.mockito.Mockito.verify;
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
        //when
        ResponseEntity<String> response = serviceTests.executeLogin(restTemplate, port);
        //then
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

    @DirtiesContext
    @Test
    void shouldAddSecondPlyaer() {
        //given
        long userIdPly1 = 1;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/welcomeView", "meta",
                "content", restTemplate, port);
        long userIdPly2 = 2;

        //when
        HttpEntity<Void> requestWithToken = new HttpEntity<>(headers);
        ResponseEntity<Long> response = restTemplate.exchange(
                serviceTests.buildUrl("/json/addSecondPlayer/"+userIdPly2+"/"+gameId, port)
                , HttpMethod.POST
                , requestWithToken
                , Long.class);

        //then
        assertThat(response.getBody()).isEqualTo(gameId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }



    //TODO test zawsze przechodzi
    @DirtiesContext
    @Test
    void shouldThrowExceptionShipLimitExceedFor1MastedShipsWhenAddedToManyShips() {
        //given
        long userIdPly1 = 1;
        long userIdPly2 = 2;
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/" + gameId, "meta"
                , "content", restTemplate, port);
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);
        //when
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip1 = new HttpEntity<>(ship1, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip1
                        , String.class);
        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip2 = new HttpEntity<>(ship2, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip2
                        , String.class);
        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip3 = new HttpEntity<>(ship3, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip3
                        , String.class);
        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip4 = new HttpEntity<>(ship4, headers);
                restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip4
                        , String.class);

        Ship ship5 = new Ship(1, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip5 = new HttpEntity<>(ship5, headers);
        ResponseEntity<String> responseException = restTemplate
                    .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                            , requestShip5
                            , String.class);

       //then
        assertThat(responseException.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseException.getBody()).isEqualTo("exceptions.ShipLimitExceedException: 1 masted ships limit has been reached. Press enter and re-enter the data");
    }

    @DirtiesContext
    @Test
    void shouldAddShipToDataBase() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta",
                                                                            "content", restTemplate, port);
        //when
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> response = restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userId+"/"+gameId, port)
                        , requestWithToken
                        , String.class);


        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @DirtiesContext
    @Test
    void shouldAddShipsToListsPlayers() {
        //given
        long userIdPly1 = 1;
        long userIdPly2 = 2;
        long gameId = 1;

        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/" + gameId, "meta"
                , "content", restTemplate, port);
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);

        //when
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip1 = new HttpEntity<>(ship1, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly1 + "/" + gameId, port)
                        , requestShip1
                        , String.class);
        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip2 = new HttpEntity<>(ship2, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly1 + "/" + gameId, port)
                        , requestShip2
                        , String.class);
        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip3 = new HttpEntity<>(ship3, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly1 + "/" + gameId, port)
                        , requestShip3
                        , String.class);
        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip4 = new HttpEntity<>(ship4, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly1 + "/" + gameId, port)
                        , requestShip4
                        , String.class);
        Ship ship5 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip5 = new HttpEntity<>(ship5, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly1 + "/" + gameId, port)
                        , requestShip5
                        , String.class);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip6 = new HttpEntity<>(ship6, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly2 + "/" + gameId, port)
                        , requestShip6
                        , String.class);
        Ship ship7 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip7 = new HttpEntity<>(ship7, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly2 + "/" + gameId, port)
                        , requestShip7
                        , String.class);
        Ship ship8 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip8 = new HttpEntity<>(ship8, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly2 + "/" + gameId, port)
                        , requestShip8
                        , String.class);
        Ship ship9 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip9 = new HttpEntity<>(ship9, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly2 + "/" + gameId, port)
                        , requestShip9
                        , String.class);
        Ship ship10 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip10 = new HttpEntity<>(ship10, headers);
        ResponseEntity<String> responseWithFullShipLists = restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/" + userIdPly2 + "/" + gameId, port)
                        , requestShip10
                        , String.class);

        //then
        assertThat(responseWithFullShipLists.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseWithFullShipLists.getBody().equals("true")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldReturnSetupBoard() {
        //when
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
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
    void gameWithOneUserShouldReturneWaitingPhaseGame() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta", "content", restTemplate, port);
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId,restTemplate, port);

        //when
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        ResponseEntity<StateGame> response = restTemplate.exchange(
                serviceTests.buildUrl("/json/status-game/"+gameId, port)
                , HttpMethod.GET
                , request
                , StateGame.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(StateGame.WAITING);
    }

    @DirtiesContext
    @Test
    void gameWithTwoUserShouldReturneApprovedPhaseGame() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta", "content", restTemplate, port);
        long userIdPly1 = 1;
        long userIdPly2 = 2;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1,restTemplate, port);
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);

        //when
        HttpEntity<Long> request = new HttpEntity<>(userIdPly1, headers);
        ResponseEntity<StateGame> response = restTemplate.exchange(
                serviceTests.buildUrl("/json/status-game/"+gameId, port)
                , HttpMethod.GET
                , request
                , StateGame.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(StateGame.WAITING);
    }

    @DirtiesContext
    @Test
    void shouldApprovedSecondPlayer() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta", "content", restTemplate, port);
        long userIdPly1 = 1;
        long userIdPly2 = 2;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1,restTemplate, port);
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);
        String state = "APPROVED";

        //when
        HttpEntity<Long> request = new HttpEntity<>(userIdPly1, headers);
        ResponseEntity<Long> response = restTemplate.exchange(
                serviceTests.buildUrl("/json/approve/"+userIdPly1+"/"+state, port)
                , HttpMethod.GET
                , request
                , Long.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(gameId);
    }

    @DirtiesContext
    @Test
    void shouldThrowExceptionTryingReturnUserPhaseGame() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
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
    void whenSecondPlayerRequestPhaseGameShouldUpdateToRequesting() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
                                                                                , "content", restTemplate, port);
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);

        //when
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Long> response = restTemplate.exchange(serviceTests.buildUrl("/json/request/"+gameId, port)
                , HttpMethod.GET
                , request
                , Long.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(gameId);
    }

    @DirtiesContext
    @Test
    void shouldUpdatePhaseGame() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
                , "content", restTemplate, port);
        long userIdPly1 = 1;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        long userIdPly2 = 2;
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);
        //actual phase game is WAITING

        //when
        String state = "FINISHED";
        HttpEntity<String> request = new HttpEntity<>(state, headers);
        restTemplate.exchange(serviceTests.buildUrl("/json/update-state/"+userIdPly1, port)
                , HttpMethod.POST
                , request
                , Void.class);
        HttpEntity<Void> requestGetState = new HttpEntity<>(headers);
        ResponseEntity<StateGame> response = restTemplate.exchange(serviceTests.buildUrl("/json/status-game/"+gameId, port)
                , HttpMethod.GET
                , requestGetState
                , StateGame.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(StateGame.FINISHED);
    }

    @DirtiesContext
    @Test
    void shouldReturnStatusGame() throws IOException {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
                                                                                    , "content", restTemplate, port);
        long userId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);

        //when
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/json/listBoard/"+gameId, port)
                , HttpMethod.GET
                , request
                , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(serviceTests.deserialiJsonToList(response.getBody())
                .equals(List.of(new Board(), new Board()))).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldDeleteShipFromStatusGame() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
                                                                                , "content", restTemplate, port);
        long userIdPly1 = 1;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        long userIdPly2 = 2;
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);

        //when
        Ship ship = new Ship(3, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestWithToken = new HttpEntity<>(ship, headers);
        ResponseEntity<String> response1 = restTemplate.postForEntity(serviceTests
                        .buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                , requestWithToken
                , String.class);
        Map<String, Long> map = new HashMap<>();
        map.put("userId", userIdPly1);
        map.put("gameId", gameId);
        HttpEntity<Map<String, Long>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests
                        .buildUrl("/json/deleteShip/"+userIdPly1+"/"+gameId, port)
                , HttpMethod.DELETE
                , request
                , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()
                .equals("{\"ships\":[],\"opponentShots\":[],\"hittedShip\":[]}")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldThrowExceptionWhenDeleteShipFromEmptyStatusGame() {
        //given
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
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
        long gameId = 1;
        long userIdPly1 = 1;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        long userIdPly2 = 2;
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/added_Ship/"+gameId, "meta"
                                                                                    , "content", restTemplate, port);
        //when
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(serviceTests.buildUrl("/view/game/"+gameId, port)
                , HttpMethod.GET
                , request
                , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @DirtiesContext
    @Test
    void shouldThrowExceptionEmptyShipList() {
        //given
        long userIdPly1 = 1;
        long gameId = 1;
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        long userIdPly2 = 2;
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/game/"+gameId, "meta"
                                                                                , "content", restTemplate, port);
        //then
        HttpEntity<Void> request = new HttpEntity<>(headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(serviceTests
                            .buildUrl("/json/game/status-isFinished/"+userIdPly1+"/"+gameId, port)
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
        long userIdPly1 = 1;
        long userIdPly2 = 2;
        long gameId = 1;
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/getParamGame/"+gameId, "meta"
                                                                                , "content", restTemplate, port);
        serviceTests.addNewGameWithStatusGameForUser(userIdPly1, restTemplate, port);
        serviceTests.addSecondPlayerToGame(userIdPly2, gameId, restTemplate, port);
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip1 = new HttpEntity<>(ship1, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip1
                        , String.class);
        Ship ship2 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip2 = new HttpEntity<>(ship2, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip2
                        , String.class);
        Ship ship3 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip3 = new HttpEntity<>(ship3, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip3
                        , String.class);
        Ship ship4 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip4 = new HttpEntity<>(ship4, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip4
                        , String.class);
        Ship ship5 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip5 = new HttpEntity<>(ship5, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly1+"/"+gameId, port)
                        , requestShip5
                        , String.class);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        HttpEntity<Ship> requestShip6 = new HttpEntity<>(ship6, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly2+"/"+gameId, port)
                        , requestShip6
                        , String.class);
        Ship ship7 = new Ship(1, 3, 4, Position.VERTICAL);
        HttpEntity<Ship> requestShip7 = new HttpEntity<>(ship7, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly2+"/"+gameId, port)
                        , requestShip7
                        , String.class);
        Ship ship8 = new Ship(1, 7, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip8 = new HttpEntity<>(ship8, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly2+"/"+gameId, port)
                        , requestShip8
                        , String.class);
        Ship ship9 = new Ship(1, 0, 7, Position.VERTICAL);
        HttpEntity<Ship> requestShip9 = new HttpEntity<>(ship9, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly2+"/"+gameId,port)
                        , requestShip9
                        , String.class);
        Ship ship10 = new Ship(2, 8, 0, Position.VERTICAL);
        HttpEntity<Ship> requestShip10 = new HttpEntity<>(ship10, headers);
        restTemplate
                .postForEntity(serviceTests.buildUrl("/json/addShip/"+userIdPly2+"/"+gameId,port)
                        , requestShip10
                        , String.class);
        HttpHeaders headersGame = serviceTests.setupHeadersRequestToGameController("/view/game/"+gameId, "meta"
                                                                        , "content", restTemplate, port);
        //when
        HttpEntity<Void> request = new HttpEntity<>(headersGame);
            ResponseEntity<String> response = restTemplate.exchange(serviceTests
                            .buildUrl("/json/game/status-isFinished/"+userIdPly1+"/"+gameId, port)
                    , HttpMethod.GET
                    , request
                    , String.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().equals("false")).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldAddShotToOneBoard() throws JsonProcessingException {
        //given
        long userId = 1;
        long gameId = 1;
        Board board1 = new Board();
        Board board2 = new Board();
        List<Board> boardList = new ArrayList<>();
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/game/"+gameId, "meta"
                                                                                    , "content", restTemplate, port);
        //when
        Shot shot = new Shot(2, 4);
        board2.getOpponentShots().add(shot);
        boardList.add(board1);
        boardList.add(board2);
        HttpEntity<Shot> request = new HttpEntity<>(shot, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(serviceTests.buildUrl("/json/game/boards/"+gameId, port)
                , request
                , String.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(serviceTests.deserialiJsonToList(response.getBody()).equals(boardList)).isTrue();
    }

    @DirtiesContext
    @Test
    void shouldAddShotToTwoBoard() throws JsonProcessingException {
        //given
        long userId = 1;
        long gameId = 1;
        Board board1 = new Board();
        Board board2 = new Board();
        List<Board> boardListOneShot = new ArrayList<>();
        List<Board> boardListTwoShots = new ArrayList<>();
        serviceTests.addNewGameWithStatusGameForUser(userId, restTemplate, port);
        HttpHeaders headers = serviceTests.setupHeadersRequestToGameController("/view/game/"+gameId, "meta"
                                                                                    , "content", restTemplate, port);
        //when
        Shot shot = new Shot(2, 4);
        board2.getOpponentShots().add(shot);
        boardListOneShot.add(new Board());
        boardListOneShot.add(board2);
        Shot shot1 = new Shot(2, 4);
        board1.getOpponentShots().add(shot1);
        boardListTwoShots.add(board1);
        boardListTwoShots.add(board2);

        HttpEntity<Shot> requestFirstShot = new HttpEntity<>(shot, headers);
        ResponseEntity<String> responseFirstShot = restTemplate.postForEntity(serviceTests.buildUrl("/json/game/boards/"+gameId, port)
                , requestFirstShot
                , String.class);
        HttpEntity<Shot> requestSecondShot = new HttpEntity<>(shot1, headers);
        ResponseEntity<String> responseSecondShot = restTemplate.postForEntity(serviceTests.buildUrl("/json/game/boards/"+gameId, port)
                , requestSecondShot
                , String.class);
        //then
        assertThat(responseFirstShot.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(serviceTests.deserialiJsonToList(responseFirstShot.getBody()).equals(boardListOneShot)).isTrue();
        assertThat(responseSecondShot.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(serviceTests.deserialiJsonToList(responseSecondShot.getBody()).equals(boardListTwoShots)).isTrue();
    }


}
