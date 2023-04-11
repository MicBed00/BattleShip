package com.web.enity.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vladmihalcea.hibernate.type.json.JsonType;
import com.web.configuration.GameSetups;
import com.web.enity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Table(name="games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name="owner_game")
    private Long ownerGame;
    @Column
    private Timestamp date;
    @Column(name="setups")
    @Type(JsonType.class)
    private GameSetups gameSetups;
    @ManyToMany(mappedBy = "games")
    @JsonBackReference
    private List<User> users = new ArrayList<>();

    public Game() {}

    public Game(Timestamp date, Long ownerGame, GameSetups gameSetups) {
        this.date = date;
        this.ownerGame = ownerGame;
        this.gameSetups = gameSetups;
    }

    public Game(Integer id) {
        this.id = id;
    }

    public GameSetups getGameSetups() {
        return gameSetups;
    }

    public void setGameSetups(GameSetups gameSetups) {
        this.gameSetups = gameSetups;
    }

    public Long getOwnerGame() {
        return ownerGame;
    }

    public void setOwnerGame(Long ownerGame) {
        this.ownerGame = ownerGame;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "StartGame{" +
                "id=" + id +
                ", date=" + date +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) && Objects.equals(ownerGame, game.ownerGame) && Objects.equals(date, game.date) && Objects.equals(gameSetups, game.gameSetups) && Objects.equals(users, game.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ownerGame, date, gameSetups, users);
    }
}
