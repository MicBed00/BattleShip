package com.web.service;

import com.web.enity.user.UserCredentialsDto;
import com.web.enity.user.UserCredentialsDtoMapper;
import com.web.repositorium.UserRepo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    //Wstrzykuje userService po to by pośrednio pobierać użytkowników z bazy danych
    // CustomUserDetailsService->userService->UserRepo->baza danych
    private final UserRepo userRepo;


    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /*wykorzystuje metodę findCredentialsByEmail(username) zwracającą UserCredentialDto.
        Wykorzystuję obiekt pośredni Dto żeby nie operować na encji w innych klasach.
        UserCredentialDto odpowiada obiektowi UserDetialsManager zwracanym przez Springa
        Klasa impementująca interfejs UserDetailsService jest to źródło użytkowników, musi nadpisać metodę loadUserByUsername
        i ta metoda musi zwracać UserDetails. Ten obiekt z klasy impelentującej USerDetailsSerive jest
        podawany do klasy DaoAuthenticationProvider gdzie następuje uwierzytelnienie - tzn porównanie danych zawartych
        w obiekcie UserDetails(wyciągniętych z bazy) z obiektem Authentication(obiekt stworzony na podstawi danych wysłanych z formularza logowania)
        */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByEmail(username)
                .map(UserCredentialsDtoMapper::map)
                .map(this::createUserDetails)
                .orElseThrow();
    }
    //na podstawie obiektu UserCredentialDto tworzony jest obiekt UserDetails
    private UserDetails createUserDetails(UserCredentialsDto userCredentialsDto) {
        return User.builder()
                .username(userCredentialsDto.getEmail())
                .password(userCredentialsDto.getPassword())
                .roles(userCredentialsDto.getRoles().toArray(String[]::new))
                .build();
    }
}
