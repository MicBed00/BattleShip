package com.web.controller;


import board.Board;
import com.web.enity.user.User;
import com.web.service.GameStatusService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

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

    @GetMapping(value ="/clean")
    String logout() {
        //po wylogowaniu czyszczę Boardy na serwerze
        gameStatusService.resetGame();
//        Board board1 = gameStatusService.getBoardList().get(0);
//        board1 = new Board();
//        Board board2 = gameStatusService.getBoardList().get(1);
//        board2 = new Board();
//        List<Board> boardList = gameStatusService.getBoardList();
        System.out.println("Board List " + gameStatusService.getBoardList());
        return "redirect:/login";
    }
}
