package com.web.configurationTest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
public class ConfigTests {

//    @Value("${spring.security.user.name}")
    private String userName = "kowalski@gmail.com";
//    @Value("${spring.security.user.password}")
    private String password = "123";

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate(userName, password);
    }

}
