package com.web.springTest;

import board.StatePreperationGame;
import com.web.RepoStartGame;
import com.web.StartGame;
import com.web.service.GameService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;

//@ExtendWith(SpringExtension.class)
//@DataJpaTest
@Testcontainers
@SpringBootTest(classes = {RepoStartGame.class})
class RespoStartGameTest {


    public static PostgreSQLContainer container = new PostgreSQLContainer(DockerImageName.parse("postgres:15-alpine"))
            .withUsername("userTest")
            .withPassword("1234")
            .withDatabaseName("dbtest");

    // > spring 2.2.6
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Autowired
    private GameService gameService;
    @Autowired
    RepoStartGame repository;

    @Test
    public void nonFind() {
//        repository.save(new StartGame(1, Timestamp.valueOf(LocalDateTime.now()),
//                            new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS)));
        System.out.println("Test");
    }



}