package com.web.enity.game;


import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;
import serialization.GameStatus;

@Entity
@Table(name = "game_statuses")
public class StatusGame {

    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY )
    private Long id;
    @Column(name="status_game")
    @Type(JsonType.class)
    private GameStatus gameStatus;
    @ManyToOne
    @JoinColumn(name="game_id")
    private StartGame game;

    public StatusGame() {
    }

    public StatusGame(GameStatus gameStatus, StartGame game) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.game = game;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public StartGame getGame() {
        return game;
    }

    public void setGame(StartGame game) {
        this.game = game;
    }
}



