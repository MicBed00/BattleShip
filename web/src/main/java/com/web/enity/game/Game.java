package com.web.enity.game;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.web.enity.user.User;
import jakarta.persistence.*;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name="games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Timestamp date;
    @ManyToMany(mappedBy = "games")
    @JsonBackReference
    private List<User> users = new ArrayList<>();

    public Game() {}

    public Game(Timestamp date) {
        this.date = date;
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


}