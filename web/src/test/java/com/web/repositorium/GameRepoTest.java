//package com.web.repositorium;
//
//import com.web.enity.game.Game;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.sql.Timestamp;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
////@DataJpaTest
//@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
////@TestPropertySource(locations = "classpath:application.properties") //TODO chciałem zrobić test z H2ale nie działa
//class GameRepoTest {
//
//    @Autowired
//    private GameRepo gameRepo;
//
//    @Test
//    void shouldfindMaxIdGame() {
//        //given
//        Game save = gameRepo.save(new Game(Timestamp.valueOf(LocalDateTime.now())));
//        gameRepo.save(new Game(Timestamp.valueOf(LocalDateTime.now())));
//        //when
//        Long maxId = gameRepo.findMaxId().get();
//        //then
//        assertEquals(2,maxId);
//        assertNotNull(save);
//    }
//
//
//}