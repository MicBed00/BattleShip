package com.web.service;

import com.web.enity.game.Game;
import com.web.enity.user.*;
import com.web.repositorium.UserRepo;
import com.web.repositorium.UserRoleRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";
    private final UserRepo userRepository;
    private final UserRoleRepo userRoleRepo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepo userRepository, UserRoleRepo userRoleRepo, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepo = userRoleRepo;
        this.passwordEncoder = passwordEncoder;
    }
    /*W tym miejscy wyciągam zapisanego uzytkownika z bazy User. Następnie mapuje Usera na
    obiekt UserCredentialDto, ponieważ nie chce w aplikacji operować na encji User. Encja User
    posiada więcej danych imię, nazwisko które nie są brane pod uwagę w trakcie autoryzacji dlatego tworzy się
    okrojone Dto odpowiadające strukturze obiektowi UserDetails
    */

    //TODO pytanie czy nie lepiej byłoby zmapować Usera wciągniętego z bazy na obiekt UserDetails??
    //w metodzie loadUserByUsername(String username) obiekt UserCredentialDto mapuje na UserDetails
    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }
    public long getLoginUserId() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUser(userEmail).getId();
    }

    @Transactional
    public User saveRegistrationUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setEmail(userRegistrationDto.getEmail());
        String hashPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(hashPassword);
        Optional<UserRole> userRole = userRoleRepo.findByName(USER_ROLE);
        userRole.ifPresentOrElse(
                role -> user.getRoles().add(role),
                () -> {//TODO stworzyć własny wyjątek
                    throw new NoSuchElementException();
                }
        );
        return userRepository.save(user);
    }

    public User getLogInUser(long userId) {
         return userRepository.findById(userId).orElseThrow(
                 () -> {
                     throw new NoSuchElementException("No user with id "+ userId);
                 }
         );
    }

    public User getUser(String userEmail) {
       return userRepository.findByEmail(userEmail).orElseThrow(
               () -> {
                   throw new NoSuchElementException("No user named "+ userEmail);
               }
       );
    }
    public Game getLastUserGames(long userId) {
        User user = getLogInUser(userId);
        List<Game> ListGames = user.getGames();
        if(ListGames.size() == 0) {
            throw new NoSuchElementException("User has not game");
        } else {
            List<Game> games = new ArrayList<>(ListGames);
            games.sort((game1, game2) -> game2.getDate().compareTo(game1.getDate()));
            return games.get(0);
        }
    }

    public User findUserById(Long userId)  {
        return userRepository.findById(userId).orElseThrow(
                ()-> new NoSuchElementException("No user in database")
        );
    }
}

