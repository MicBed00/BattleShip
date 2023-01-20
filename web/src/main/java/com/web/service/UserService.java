package com.web.service;

import com.web.enity.user.*;
import com.web.repositorium.UserRepo;
import com.web.repositorium.UserRoleRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

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

    public Optional<UserCredentialsDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserCredentialsDtoMapper::map);
    }

    @Transactional
    public void saveUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setEmail(userRegistrationDto.getEmail());
        String hashPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(hashPassword);
        Optional<UserRole> userRole = userRoleRepo.findByName(USER_ROLE);
        userRole.ifPresentOrElse(
                role -> user.getRoles().add(role),
                () -> {
                    //TODO stworzyć własny wyjątek
                    throw new NoSuchElementException();
                }
        );
        userRepository.save(user);
    }
}
