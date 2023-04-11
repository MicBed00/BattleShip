package com.web.repositories;

import com.web.configuration.GameSetups;
import com.web.enity.game.Game;
import dataConfig.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameRepoTest {

    @Autowired
    private GameRepo gameRepo;

    @Test
    void shouldfindMaxIdGame() {
        //given
        long owner = 1;
        int shipLimit = 4;
        GameSetups gameSetups = new GameSetups(List.of(1,2,3,4), List.of(Position.VERTICAL, Position.HORIZONTAL), shipLimit);
        Game save = gameRepo.save(new Game(Timestamp.valueOf(LocalDateTime.now()), owner, gameSetups));
        gameRepo.save(new Game(Timestamp.valueOf(LocalDateTime.now()), owner, gameSetups));
        //when
        Long maxId = gameRepo.findMaxId().get();
        //then
        assertEquals(2,maxId);
        assertNotNull(save);
    }


}