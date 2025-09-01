package backend.futurefinder.implementation.auth;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class AuthGenerator {



    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();



    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }





}
