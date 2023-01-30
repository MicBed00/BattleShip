package com.web.controller;

import com.web.enity.user.User;
import com.web.service.GameRepoService;
import com.web.service.GameStatusRepoService;
import com.web.service.GameStatusService;
import com.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("view")
public class GameControllerView {
    private final GameStatusService gameStatusService;
    private final GameStatusRepoService gameStatusRepoService;
    private final GameRepoService gameRepoService;
    private final UserService userService;

    @Autowired
    public GameControllerView(GameStatusService gameStatusService,
                              GameStatusRepoService gameStatusRepoService,
                              GameRepoService gameRepoService,
                              UserService userService)
    {
        this.gameStatusService = gameStatusService;
        this.gameStatusRepoService = gameStatusRepoService;
        this.gameRepoService = gameRepoService;
        this.userService = userService;
    }

    @GetMapping(value= "/welcomeView")
    public String welcome() {
        return "welcomeView";
    }

    @GetMapping(value = "/startGame")
    public String startGame() {
        return "redirect:/view/getParamGame";
    }

    @GetMapping(value = "/getParamGame")
    public String getParametersGame(Model model) {
        model.addAttribute("shipSize", gameStatusService.getShipSize());
        model.addAttribute("orientList", gameStatusService.getOrientation());
        model.addAttribute("shipLimit", gameStatusService.getShipLimits());
        model.addAttribute("userId", userService
                .getUser(SecurityContextHolder.getContext().getAuthentication().getName())
                .getId());
        return "add_ship";
    }

    @GetMapping("/added_Ship")
    public String addedShip() {
        return "addShip_success";
    }

    @GetMapping(value = "/game")
    public String getBoards(Model model) {
        //TODO update endpoint getBoards /game
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUser(username);
        gameStatusRepoService.updateStatePreperationGame(user.getId(), "PREPARED");
        model.addAttribute("userId", user.getId());
        return "game";
    }

    @GetMapping(value="/statistics")
    public String printStatistics(Model model) {
        model.addAttribute("player1numberShots", gameStatusService.statisticsGame(gameStatusService.getBoardList().get(0))[0]);
        model.addAttribute("player1HittedShots", gameStatusService.statisticsGame(gameStatusService.getBoardList().get(0))[1]);
        model.addAttribute("player2numberShots", gameStatusService.statisticsGame(gameStatusService.getBoardList().get(1))[0]);
        model.addAttribute("player2HittedShots", gameStatusService.statisticsGame(gameStatusService.getBoardList().get(1))[1]);
        double accuracyPly1 = gameStatusService.getAccuracyShot(gameStatusService.statisticsGame(gameStatusService.getBoardList().get(0))[0],
                gameStatusService.statisticsGame(gameStatusService.getBoardList().get(0))[1]);
        double accuracyPly2 = gameStatusService.getAccuracyShot(gameStatusService.statisticsGame(gameStatusService.getBoardList().get(1))[0],
                gameStatusService.statisticsGame(gameStatusService.getBoardList().get(1))[1]);
        model.addAttribute("accuracyPly1", accuracyPly1);
        model.addAttribute("accuracyPly2", accuracyPly2);
        return "gameOver";
    }


}
