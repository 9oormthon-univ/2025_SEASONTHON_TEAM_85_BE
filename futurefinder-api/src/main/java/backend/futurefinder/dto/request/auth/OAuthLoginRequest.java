package backend.futurefinder.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OAuthLoginRequest {
    public static record OAuthLogin(
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("userNickname") String userNickname
    ) {
        public String toEmail() { return userEmail; }
        public String toNickname() { return userNickname; }
    }
}
