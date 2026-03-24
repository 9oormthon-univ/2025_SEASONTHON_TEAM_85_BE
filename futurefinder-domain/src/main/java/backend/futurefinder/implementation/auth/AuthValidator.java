package backend.futurefinder.implementation.auth;

import backend.futurefinder.error.AuthorizationException;
import backend.futurefinder.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final PasswordEncoder passwordEncoder;

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new AuthorizationException(ErrorCode.WRONG_PASSWORD);
        }
    }
}
