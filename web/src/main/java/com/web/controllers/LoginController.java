package com.web.controllers;


import com.web.services.SavedGameService;
import com.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    private final SavedGameService savedGameService;
    private final UserService userService;
    @Autowired
    LoginController(SavedGameService savedGameService,
                    UserService userService)
    {
        this.savedGameService = savedGameService;
        this.userService = userService;
    }
    @GetMapping(value="/login")
    String loginPage() {
        return "login_form";
    }

}
