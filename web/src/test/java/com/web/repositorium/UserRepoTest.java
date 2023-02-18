package com.web.repositorium;

import com.web.enity.user.User;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:/init_fixtures.sql")
class UserRepoTest {
    @Autowired
    UserRepo userRepo;

    @Test
    void shouldFindUserByEmail() {
        //when
        User user = userRepo.findByEmail("adam@gmail.com").get();
        //then
        assertEquals("adam@gmail.com", user.getEmail());
    }
}