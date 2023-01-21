package com.web.controller;


import com.web.enity.user.UserRegistrationDto;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RegisterController {

    private final UserService userService;

    @Autowired
    public RegisterController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    String register(Model model) {
        UserRegistrationDto user = new UserRegistrationDto();
        model.addAttribute("userDto", user);
        return "registration";

    }
//
//    @PostMapping("/register")
//    String userRegister(UserRegistrationDto user) {
//        userService.saveUser(user);
//        return "redirect:/success";
//    }

    @GetMapping("/success")
    String successRegister() {
        return "confirmation";
    }
}
