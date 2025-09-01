package backend.futurefinder.implementation.auth;

import backend.futurefinder.repository.auth.LoggedInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthRemover {

    private final LoggedInRepository loggedInRepository;

    public void removeLoginInfo(String refreshToken) {
        loggedInRepository.remove(refreshToken);
    }
}
