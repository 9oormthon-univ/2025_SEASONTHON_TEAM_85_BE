package backend.futurefinder.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OAuthLoginRequest(
        String email,
        String nickname
) {


    public String toGetEmail() {
        return email;
    }
    public String toGetNickname() {return nickname;}
}
