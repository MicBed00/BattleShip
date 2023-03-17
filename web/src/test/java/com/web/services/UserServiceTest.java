package com.web.service;

import com.web.enity.game.Game;
import com.web.enity.user.User;
import com.web.enity.user.UserRegistrationDto;
import com.web.enity.user.UserRole;
import com.web.repositorium.UserRepo;
import com.web.repositorium.UserRoleRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.BDDMockito.given;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;
    @Mock
    private UserRoleRepo userRoleRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityContext securityContext;

    private AutoCloseable autoCloseable;
    private UserService userService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepo, userRoleRepo, passwordEncoder);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldReturnUserId() {
        //given
        User user = new User();
        user.setId(1L);
        user.setEmail("jan@gmail.com");
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Authentication auth = new UsernamePasswordAuthenticationToken("jan@gmail.com","123");
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        //when
        Long id = userService.getLoginUserId();

        //then
        assertEquals(1L, id);
    }

    @Test
    void shouldSaveRegistrationUser() {
        //given
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setEmail("jan@gmail.com");
        userDto.setFirstName("jan");
        userDto.setLastName("kowal");
        userDto.setPassword("123");
        String hash = "$2y$10$7iLEze9izvoT8MpuwxAtouJbMLCrwF5zN7o456n2lStGmIt6Q9bLS";
        //password = 123
        given(passwordEncoder.encode(userDto.getPassword())).willReturn(hash);
        UserRole userRole = new UserRole();
        userRole.setName("USER");
        userRole.setId(1L);
        given(userRoleRepo.findByName("USER")).willReturn(Optional.of(userRole));
        //when
        userService.saveRegistrationUser(userDto);

        //then
        verify(userRepo, times(1)).save(any(User.class));
        verify(userRoleRepo).findByName("USER");
        verify(passwordEncoder, times(1)).encode(userDto.getPassword());
    }

    @Test
    void shouldReturnLogInUser() {
        //given
        long userId = 1L;
        User user = new User();
        user.setId(userId);
        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        //when
        User logInUser = userService.getLogInUser(userId);

        //then
        assertEquals(user, logInUser);
        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    void shouldThrowExceptionsForNoLogInUser() {
        //given
        long userId = 1L;
        given(userRepo.findById(userId)).willThrow(new NoSuchElementException("No user with id "+ userId));

        //when
        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            userService.getLogInUser(userId);
        });
        assertEquals("No user with id "+ userId, e.getMessage());
        verify(userRepo, times(1)).findById(userId);
    }



    @Test
    void shouldRetrunUserForUserId() {
        //given
        String email = "test@test.com";
        User user = new User();
        user.setEmail("test@test.com");
        given(userRepo.findByEmail(email)).willReturn(Optional.of(user));

        //when
        userService.getUser(email);

        //then
        verify(userRepo, times(1)).findByEmail(email);
        assertEquals(email, user.getEmail());
    }

    @Test
    void shouldThrowExceptionIfUserIdDoesNotExist() {
        //given
        String email = "test@test.com";
        given(userRepo.findByEmail(email)).willThrow(new NoSuchElementException("No user with named "+ email));

        //when
        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            userService.getUser(email);
        });
        assertEquals("No user with named "+ email, e.getMessage());
        verify(userRepo, times(1)).findByEmail(email);
    }


    @Test
    void shouldReturnLastUserGame() {
        //given
        long owener = 1;
        Game game1 = new Game(Timestamp.valueOf(LocalDateTime.now()), owener);
        game1.setId(1);
        Game game2 = new Game(Timestamp.valueOf(LocalDateTime.now()), owener);
        game2.setId(2);
        List<Game> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        long userId = 1L;
        User user = new User();
        user.setId(1L);
        user.setGames(games);

        given(userRepo.findById(userId)).willReturn(Optional.of(user));

        //then
        Game result = userService.getLastUserGames(userId);

        //then
        assertEquals(game2, result);
        assertEquals(games.size(), 2);
    }

    @Test
    void shouldThrowExceptionIfUserDoesNotHaveGame() {
        //given
        long userId = 1L;
        User user = new User();
        user.setId(1L);

        //when
        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            userService.getLastUserGames(userId);
        });
        assertEquals("No user with id "+ userId, e.getMessage());
    }
}