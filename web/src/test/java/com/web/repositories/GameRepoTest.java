package com.web.repositories;

import com.web.enity.game.Game;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameRepoTest {

    @Autowired
    private GameRepo gameRepo;

    @Test
    void shouldfindMaxIdGame() {
        //given
        long owner = 1;
        Game save = gameRepo.save(new Game(Timestamp.valueOf(LocalDateTime.now()), owner));
        gameRepo.save(new Game(Timestamp.valueOf(LocalDateTime.now()), owner));
        //when
        Long maxId = gameRepo.findMaxId().get();
        //then
        assertEquals(2,maxId);
        assertNotNull(save);
    }


}