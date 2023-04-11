package com.web.controllers;

import com.web.configuration.GameSetupsDto;
import com.web.enity.user.User;
import com.web.services.GameService;
import com.web.services.SavedGameService;
import com.web.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("view")
public class GameControllerView {
    private final SavedGameService savedGameService;
    private final GameService gameService;
    private final UserService userService;
    private final GameSetupsDto gameSetupsDto;

    @Autowired
    public GameControllerView(SavedGameService savedGameService,
                              GameService gameService,
                              UserService userService,
                              GameSetupsDto gameSetupsDto)
    {
        this.savedGameService = savedGameService;
        this.gameService = gameService;
        this.userService = userService;
        this.gameSetupsDto = gameSetupsDto;
    }

    @GetMapping(value= "/welcomeView")
    public String welcome(Model model) {
        User user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("userId", user.getId());
        model.addAttribute("waitingGames", gameService.getIdGamesForView());
        model.addAttribute("usersGames", savedGameService.getUnfinishedUserGames());
        return "welcomeView";
    }

    @GetMapping(value = "/getParamGame/{gameId}/{sizeBoard}")
    public String getParametersGame(@PathVariable long gameId,
                                    @PathVariable int sizeBoard,
                                    Model model) {
        model.addAttribute("shipSize", gameSetupsDto.getShipSize());
        model.addAttribute("orientList", gameSetupsDto.getOrientations());
        model.addAttribute("shipLimit", gameSetupsDto.getShipLimit());
        model.addAttribute("gameId", gameId);
        model.addAttribute("sizeBoard", sizeBoard);
        model.addAttribute("userId", userService
                .getUser(SecurityContextHolder.getContext().getAuthentication().getName())
                .getId());
        return "add_ship";
    }

    @GetMapping("/added_Ship/{gameId}")
    public String addedShip(@PathVariable Long gameId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUser(username);
        savedGameService.checkIfTwoPlayersArePreparedNextChangeState("PREPARED", user.getId());
        model.addAttribute("gameId", gameId);
        return "addShip_success";
    }

    @GetMapping(value = "/game/{gameId}")
    public String getBoards(@PathVariable Long gameId, Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUser(username);
        model.addAttribute("userId", user.getId());
        model.addAttribute("gameId", gameId);
        return "game";
    }

    @GetMapping(value="/statistics/{gameId}")
    public String printStatistics(@PathVariable long gameId, Model model) {
        model.addAttribute("player1numberShots", savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(0))[0]);
        model.addAttribute("player1HittedShots", savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(0))[1]);
        model.addAttribute("player2numberShots", savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(1))[0]);
        model.addAttribute("player2HittedShots", savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(1))[1]);
        double accuracyPly1 = savedGameService.getAccuracyShots(savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(0))[0],
                savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(0))[1]);
        double accuracyPly2 = savedGameService.getAccuracyShots(savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(1))[0],
                savedGameService.statisticsGame(savedGameService.getBoardsList(gameId).get(1))[1]);
        model.addAttribute("accuracyPly1", accuracyPly1);
        model.addAttribute("accuracyPly2", accuracyPly2);
        return "gameOver";
    }


}
