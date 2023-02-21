package com.web.service;

import com.web.enity.user.User;
import com.web.enity.user.UserCredentialsDto;
import com.web.enity.user.UserCredentialsDtoMapper;
import com.web.enity.user.UserRole;
import com.web.repositorium.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.BDDMockito.given;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepo userRepo;

    private CustomUserDetailsService customUserDetailsService;

    AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        customUserDetailsService = new CustomUserDetailsService(userRepo);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void loadUserByUsername() {
        //given
        String userName = "test@test.com";
        UserRole userRole = new UserRole();
        userRole.setId(1L);
        userRole.setName("USER");
        User user = new User();
        user.getRoles().add(userRole);
        user.setEmail("test@test.com");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setPassword("123");
        user.setId(1L);
        given(userRepo.findByEmail(userName)).willReturn(Optional.of(user));

        //when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userName);

        //then
        assertEquals(userDetails.getUsername(), user.getEmail());
        verify(userRepo, times(1)).findByEmail(userName);




    }
}