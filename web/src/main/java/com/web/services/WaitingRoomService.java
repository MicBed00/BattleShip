package com.web.services;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class WaitingRoomService {
    private final UserService userService;
    private final GameRepo gameRepo;
    private final GameRepoService gameRepoService;

    private final SavedGameRepo savedGameRepo;

    private final SavedGameService savedGameService;

    @Autowired
    public WaitingRoomService(UserService userService,
                              GameRepo gameRepo,
                              GameRepoService gameRepoService,
                              SavedGameRepo savedGameRepo,
                              SavedGameService savedGameService) {
        this.userService = userService;
        this.gameRepo = gameRepo;
        this.gameRepoService = gameRepoService;
        this.savedGameRepo = savedGameRepo;
        this.savedGameService = savedGameService;
    }

    @Transactional
    public Integer saveNewGame(long userId, int sizeBoard) {
        User user = userService.getLogInUser(userId);
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), userId);
        user.getGames().add(game);
        game.getUsers().add(user);
        //TODO czy w tym miejscu zapisywac z wykorzystaniem repo czy lepiej przez jakiś serwis?
        Game savedGame = gameRepo.save(game);
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(sizeBoard));
        boardList.add(new Board(sizeBoard));
        saveNewStatusGame(new GameStatus(boardList, StateGame.NEW), game);
        gameRepoService.getIdGamesForView().add(savedGame.getId());    //dodaję do list id nowej gry, po to by wyświetliła się w widoku

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
                .filter(game -> gameRepoService.getIdGamesForView().contains(game.getId()))
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
        SavedGame savedStatus = savedGameService.getStatusGame(game.getId());

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
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Can't find game")
        );
        User user = userService.findUserById(userId);
        SavedGame savedGame = savedGameService.getStatusGame(gameId);
        StateGame state = savedGame.getGameStatus().getState();
        List<Board> boards = savedGame.getGameStatus().getBoardsStatus();

        List<Game> games = user.getGames();
        if(games.contains(game)) {
            games.remove(game);
            int currentPlayer = savedGameService.getCurrentPlayer(gameId);
            GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
            savedGameRepo.save(new SavedGame(gameStatus, game));
        } else {
            throw new NoSuchElementException("No game");
        }
    }


}
