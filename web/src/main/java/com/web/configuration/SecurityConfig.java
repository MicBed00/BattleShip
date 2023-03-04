package com.web.configuration;

import com.web.service.CustomUserDetailsService;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.*;

import javax.sql.DataSource;
import java.security.SecureRandom;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

//    mvcMatcher jest deprecated i zastąpiony requestMatchers
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    DataSource dataSource;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.userDetailsService(customUserDetailsService);
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/register", "/confirmation", "/success").permitAll()
                .requestMatchers("/img/**", "/styles/**").permitAll()
                .anyRequest().authenticated()
        );
        http.formLogin(login -> login
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/view/welcomeView", true));
        http.rememberMe()
                .tokenRepository(presistentTokenRepository());
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                .logoutSuccessUrl("/login").permitAll()
                );
        return http.build();
    }
//Deklarowanie użytkowników InMemoryUserDetailsManager - bez bazy danych
//    @Bean
//    public UserDetailsService userDetailsService() {
//        User.UserBuilder userBuilder = User.builder();
//        UserDetails admin = userBuilder.username("admin@gmail.com").password("123").roles("ADMIN").build();
//        UserDetails user1 = userBuilder.username("john").password("{noop}asdf1234").roles("USER").build();
//        return new InMemoryUserDetailsManager(admin, user1);
//    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository presistentTokenRepository() {
        final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
//    private String generateKey() {
//        SecureRandom random = new SecureRandom();
//        byte[] keyBytes = new byte[16];
//        random.nextBytes(keyBytes);
//        return new String(Hex.encode(keyBytes));
//    }
}
