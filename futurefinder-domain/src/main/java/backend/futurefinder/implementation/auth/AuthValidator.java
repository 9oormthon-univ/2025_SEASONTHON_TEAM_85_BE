package backend.futurefinder.implementation.auth;

import backend.futurefinder.error.AuthorizationException;
import backend.futurefinder.error.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AuthValidator {


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }
    }



}
