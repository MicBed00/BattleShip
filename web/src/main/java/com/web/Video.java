package com.web;

import javax.persistence.*;


@Entity(name = "Table_test")
@Table
public class Video {

    public Video() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String kkk11;


    private String nowaKolumnaTest;


    private String email;


    private String password;

    private String phoneNumber;

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
