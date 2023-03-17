package com.web.controller;


import com.web.enity.user.UserRegistrationDto;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(value = "/register")
    String register(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("userDto", user);
        return "registration";

    }

    @PostMapping(value = "/register")
    String userRegister(UserRegistrationDto user) {
        userService.saveRegistrationUser(user);
        return "redirect:/success";
    }

    @GetMapping(value = "/success")
    String successRegister() {
        return "confirmation";
    }
}
