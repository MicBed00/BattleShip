package com.configuration;

import com.web.logger.GameRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameRequestInterceptor.class);
    @Bean
    public GameRequestInterceptor gameRequestInterceptor() {
        return new GameRequestInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LOGGER.debug("Wiadomość z configuracji");
        System.out.println("intercepridf ");
        registry.addInterceptor(gameRequestInterceptor());
        registry.addInterceptor(gameRequestInterceptor())
                .addPathPatterns("/view/**");
    }

}
