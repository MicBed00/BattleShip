package com.web.enity.statusGame;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;


import org.hibernate.annotations.Type;

import serialization.GameStatus;

import java.sql.Timestamp;


@Entity
@Table(name="games")
public class StartGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Timestamp date;

    public StartGame() {}

    public StartGame(Timestamp date) {
        this.date = date;
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


}
