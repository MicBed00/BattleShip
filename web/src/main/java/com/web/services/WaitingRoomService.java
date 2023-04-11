package com.web.services;

import board.Board;
import board.StateGame;
import com.web.configuration.GameSetups;
import com.web.configuration.GameSetupsDto;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.util.*;

@Service
public class WaitingRoomService {
    private final UserService userService;
    private final GameRepo gameRepo;
    private final GameService gameService;
    private final SavedGameRepo savedGameRepo;
    private final SavedGameService savedGameService;

    @Autowired
    public WaitingRoomService(UserService userService,
                              GameRepo gameRepo,
                              GameService gameService,
                              SavedGameRepo savedGameRepo,
                              SavedGameService savedGameService) {
        this.userService = userService;
        this.gameRepo = gameRepo;
        this.gameService = gameService;
        this.savedGameRepo = savedGameRepo;
        this.savedGameService = savedGameService;
    }

    @Transactional
    public Integer saveNewGame(long userId, int sizeBoard, GameSetupsDto gsDto) {
        GameSetups gameSetups = gameService.createGameSetups(gsDto.getShipSize(), gsDto.getOrientations(), gsDto.getShipLimit());
        Game game = gameService.createGame(userId, gameSetups);
        Game savedGame = gameService.saveGame(game);
        User user = userService.getLogInUser(userId);
        user.getGames().add(game);
        game.getUsers().add(user);

        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(sizeBoard));
        boardList.add(new Board(sizeBoard));
        saveNewStatusGame(new GameStatus(boardList, StateGame.NEW), game);
        gameService.addGame(game);    //dodaję do list id nowej gry, po to by wyświetliła się w widoku

        return savedGame.getId();
    }

    @Transactional
    private void saveNewStatusGame(GameStatus gameStatus, Game game) {
        SavedGame savedGame = new SavedGame(gameStatus, game);
        savedGameRepo.save(savedGame);
    }

    public List<Long> checkIfSavedGameStatusHasChanged() {
        List<Long> result = new ArrayList<>();
        long loginUserId = userService.getUserId();
        User logInUser = userService.getLogInUser(loginUserId);
        List<Game> games = logInUser.getGames();
        //na podstawie wszystkiech gier użytkownika, wyznaczam te, które mają status REQUESTING
        List<SavedGame> savedGames = getUnFinishedSavedGames(games);

        if (savedGames.isEmpty()) {
            return new ArrayList<>();
        } else {
            //Zwracam ostatnią grę z listy i wyciągam id tej gry, zwracam w endpointcie
            Game game = savedGames.stream().
                    skip(savedGames.size() - 1)
                    .findFirst().get().getGame();

            result.add(getIdOpponent(game, loginUserId));      //id przeciwnika
            result.add(Long.valueOf(game.getId()));

            return result;
        }
    }

    //zwraca listę gier, które aktualnie są wyświetlane w widoku i mają status Requesting
    private List<SavedGame> getUnFinishedSavedGames(List<Game> games) {
        return games.stream()
                .sorted(Comparator.comparing(Game::getDate))
                .filter(game -> gameService.getIdGamesForView().contains(game.getId()))
                .map(game -> savedGameRepo.findMaxIdByGameId(game.getId()))
                .map(id -> savedGameRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Game doesn't exist")))
                .filter(gs -> gs.getGameStatus().getState().equals(StateGame.REQUESTING)
                        && gs.getGame().getUsers().size() > 1)
                .toList();
    }

    private Long getIdOpponent(Game game, Long loginUserId) {
        return game.getUsers().stream()
                .filter(u -> !u.getId().equals(loginUserId))
                .map(User::getId)
                .findAny().orElseThrow(() -> new NoSuchElementException("Game doesn't exist"));
    }

    public List<String> checkIfOpponentAppears(long idGame) {
        List<String> answer = new ArrayList<>();
        Game game = gameRepo.findById(idGame).orElseThrow(() -> new NoSuchElementException("Game doesn't exist"));
        SavedGame savedStatus = savedGameService.getSavedGame(game.getId());

        if (savedStatus.getGameStatus().getState().equals(StateGame.REQUESTING)) {
            answer.add("true");
            answer.add(String.valueOf(game.getId()));
        } else {
            answer.add("false");
        }
        return answer;
    }

    @Transactional
    public void addSecondPlayerToGame(long userId, long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(() -> new NoSuchElementException("Game doesn't exist"));
        User user = userService.getLogInUser(userId);
        user.getGames().add(game);
        game.getUsers().add(user);
        gameRepo.save(game);
    }

    @Transactional
    public void deleteGame(Long userId, Long gameId) {
        Game game = gameService.getGame(gameId);
        User user = userService.findUserById(userId);
        SavedGame savedGame = savedGameService.getSavedGame(gameId);
        StateGame state = savedGame.getGameStatus().getState();
        List<Board> boards = savedGame.getGameStatus().getBoardsStatus();

        List<Game> games = user.getGames();
        if (games.contains(game)) {
            games.remove(game);
            int currentPlayer = savedGameService.getCurrentPlayer(gameId);
            GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
            savedGameRepo.save(new SavedGame(gameStatus, game));
        } else {
            throw new NoSuchElementException("No game");
        }
    }

    public Integer getLowestGameId(long userId) {
        User user = userService.getLogInUser(userId);
        Game game = user.getGames().stream()
                .sorted(Comparator.comparing(Game::getId))
                .findFirst().orElseThrow(
                        () -> new NoSuchElementException("No game")
                );
        return game.getId();
    }
}


