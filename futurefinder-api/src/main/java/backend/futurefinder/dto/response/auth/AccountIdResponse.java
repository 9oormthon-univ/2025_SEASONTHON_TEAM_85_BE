package backend.futurefinder.dto.response.auth;

public record AccountIdResponse(String accountId) {
    public static AccountIdResponse of(String accountId) {
        return new AccountIdResponse(accountId);
    }
}
