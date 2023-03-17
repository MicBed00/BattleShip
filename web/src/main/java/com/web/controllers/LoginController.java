package com.web.controllers;


import com.web.services.GameStatusService;
import com.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private final GameStatusService gameStatusService;
    private final UserService userService;
    @Autowired
    LoginController(GameStatusService gameStatusService,
                    UserService userService)
    {
        this.gameStatusService = gameStatusService;
        this.userService = userService;
    }
    @GetMapping(value="/login")
    String loginPage() {
        return "login_form";
    }

}
