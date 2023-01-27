package com.web.enity.game;

import com.web.enity.user.User;
import jakarta.persistence.*;


import java.sql.Timestamp;


@Entity
@Table(name="games")
public class StartGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Timestamp date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public StartGame() {}

    public StartGame(Timestamp date, User user) {
        this.date = date;
        this.user = user;
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
