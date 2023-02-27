package com.web.enity.game;

import com.web.enity.user.User;
import jakarta.persistence.*;


import java.sql.Timestamp;
import java.util.HashSet;
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
    private Set<User> users = new HashSet<>();

    public Game() {}

    public Game(Timestamp date) {
        this.date = date;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
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
