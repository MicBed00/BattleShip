package com.web.configuration;

import com.web.logger.GameRequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Autowired
    GameRequestInterceptor gameRequestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(gameRequestInterceptor);
//        registry.addInterceptor(gameRequestInterceptor)
//                .addPathPatterns("/view/**");
    }

}
