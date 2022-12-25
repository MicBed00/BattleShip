package com.web;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepoStartGame extends JpaRepository<StartGame,Long> {

}
