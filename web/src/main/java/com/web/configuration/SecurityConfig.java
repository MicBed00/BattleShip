package com.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(requests -> requests.anyRequest().authenticated());
        http.formLogin(login -> login.loginPage("/login").permitAll());
        http.csrf().disable();
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder userBuilder = User.builder();
        //superadmin/hard
        String password1 = "{bcrypt}" + new BCryptPasswordEncoder().encode("hard");
        UserDetails admin = userBuilder.username("superadmin").password(password1).roles("ADMIN").build();
        //john/dog.8
        String password2 = "{bcrypt}" + new BCryptPasswordEncoder().encode("dog.8");
        UserDetails user1 = userBuilder.username("john").password(password2).roles("USER").build();
        return new InMemoryUserDetailsManager(admin, user1);
    }
}
