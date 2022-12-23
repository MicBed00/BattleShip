package com.web;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.beans.factory.annotation.Configurable;
import serialization.GameStatus;

import java.sql.Timestamp;


@Entity
@Configurable
@Table(name="start_game")
public class StartGame {
    public StartGame() {}

    public StartGame(Integer id, Timestamp date, GameStatus gameStatus) {
        this.id = id;
        this.date = date;
        this.gameStatus = gameStatus;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Timestamp date;

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    private transient GameStatus gameStatus;
    public GameStatus getGameStatus() {
        return gameStatus;
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
}
