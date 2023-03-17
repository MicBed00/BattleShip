package com.web.repositories;

import com.web.enity.user.User;
import com.web.enity.user.UserRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:/init_fixtures.sql")
class UserRepoTest {
    @Autowired
    UserRepo userRepo;

    @DirtiesContext
    @Test
    void shouldFindUserByEmail() {
        //when
        User result = userRepo.findByEmail("adam@gmail.com").get();
        //then
        assertEquals("adam@gmail.com", result.getEmail());
        assertEquals("adam", result.getFirstName());
    }
    @DirtiesContext
    @Transactional
    @Test
    void shouldSaveUser() {
        //given
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("{bcrypt}$2a$10$qqO5BdX6q/bjVFjWV8YzFe.b9y/oe.yXN9lHB9Su5bKYMtJb9dLqe");
        user.setFirstName("Test");
        user.setLastName("Test");
        UserRole userRole = new UserRole();
        userRole.setName("USER");
        user.getRoles().add(userRole);

        //when
        User savedUser = userRepo.save(user);

        //then
        assertNotNull(savedUser);
    }
}