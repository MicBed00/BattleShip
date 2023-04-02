package com.web.services;

import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class GameRepoServiceTest {

    @Mock
    private GameRepo gameRepo;
    @Mock
    private GameStatusRepoService gameStatusRepoService;
    @Mock
    private UserService userService;

    @Mock
    SavedGameRepo repoStatusGame;

    @Mock
    SavedGameService savedGameService;
    @InjectMocks
    private GameRepoService gameRepoService;


//    @Test
//    void shouldSaveNewGame() {
//        //given
//        long userId = 1;
//        int sizeBaord = 11;
//        List<Board> boardList = new ArrayList<>();
//        StateGame state = StateGame.IN_PROCCESS;
//        long owner = 1;
//        long gameId = 1;
//        User user = new User();
//        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
//        Game startGame = new Game(timestamp, owner);
//        startGame.setId(1);
//        when(userService.getLogInUser(userId)).thenReturn(user);
//        doReturn(startGame).when(gameRepo).save(Mockito.any(Game.class));
//
//        //when
//        gameStatusService.saveNewGame(userId, sizeBaord);
//
//        //then
//        verify(userService, times(1)).getLogInUser(userId);
////        verify(gameRepo,times(1)).save(new Game(timestamp, owner));
//    }

    @Test
    void shouldReturnFalseForUserWithoutGames() {
        //when
        boolean result = gameRepoService.checksUnfinishedGames();

        //then
        assertFalse(result);
        verify(savedGameService, times(1)).getUnfinishedUserGames();
    }
}