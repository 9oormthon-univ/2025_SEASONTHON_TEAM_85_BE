package backend.futurefinder.implementation.auth;


import backend.futurefinder.model.token.RefreshToken;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.repository.auth.LoggedInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthAppender {

    private final LoggedInRepository loggedInRepository;


    public void appendLoggedIn(RefreshToken newRefreshToken, UserId userId) {
        loggedInRepository.append(newRefreshToken, userId);
    }


}
