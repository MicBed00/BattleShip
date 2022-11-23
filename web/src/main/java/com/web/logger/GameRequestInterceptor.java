package com.web.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class GameRequestInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameRequestInterceptor.class);
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOGGER.debug("preHandler invoked.. {}: " + request.getRequestURL().toString(), request.getMethod());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LOGGER.info("postHandle invoked...{},{}: "+request.getRequestURI(),request.getMethod(),response.getStatus());

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

       if(ex!=null) {
           LOGGER.error("exception inside afterCompletion"+ex.getMessage());
       }
        LOGGER.info("preHandle invoked...{}:{}"+request.getRequestURI(),request.getMethod());
    }
}
