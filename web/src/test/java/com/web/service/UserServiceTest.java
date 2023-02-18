package com.web.service;

import com.web.enity.user.User;
import com.web.enity.user.UserRegistrationDto;
import com.web.enity.user.UserRole;
import com.web.repositorium.UserRepo;
import com.web.repositorium.UserRoleRepo;
import org.hibernate.mapping.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Long id = userService.getUserId();

        //then
        assertEquals(1L, id);
    }

//    @Test
//    void shouldSaveRegistrationUser() {
//        //given
//        UserRegistrationDto userDto = new UserRegistrationDto();
//        userDto.setEmail("jan@gmail.com");
//        userDto.setFirstName("jan");
//        userDto.setLastName("kowal");
//        userDto.setPassword("123");
//        UserRole userRole = new UserRole();
//        userRole.setName("USER");
//        userRole.setId(1L);
//        //password = 123
//        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("$2y$10$7iLEze9izvoT8MpuwxAtouJbMLCrwF5zN7o456n2lStGmIt6Q9bLS");
//        when(userRoleRepo.findByName("USER")).thenReturn(Optional.of(userRole));
////        User user = new User();
////        user.setEmail(userDto.getEmail());
////        user.setFirstName(userDto.getFirstName());
////        user.setLastName(userDto.getLastName());
////        Set<UserRole> objects = new HashSet<>();
////        user.setRoles(objects.add);
//
//        //
////        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
////
////        verify(userRepo.save(userCaptor.capture()));
////
////        User userCaptorValue = userCaptor.getValue();
////
////        assertThat(userCaptorValue).isEqualTo(userCaptorValu)
//
//        verify(userRoleRepo).findByName("USER");
////        verify(userRoleRepo).save()
//
//    }

    @Test
    void getLogInUser() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getLastUserGames() {
    }
}