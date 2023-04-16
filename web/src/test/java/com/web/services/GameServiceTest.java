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
class GameServiceTest {

    @Mock
    private GameRepo gameRepo;

    @Mock
    private UserService userService;

    @Mock
    SavedGameRepo repoStatusGame;

    @Mock
    SavedGameService savedGameService;
    @InjectMocks
    private GameService gameService;

    @Test
    void shouldReturnFalseForUserWithoutGames() {
        //when
        boolean result = gameService.checksUnfinishedGames();

        //then
        assertFalse(result);
        verify(savedGameService, times(1)).getUnfinishedUserGames();
    }
}