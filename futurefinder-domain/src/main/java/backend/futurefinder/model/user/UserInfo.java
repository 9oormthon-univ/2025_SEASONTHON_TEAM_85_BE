package backend.futurefinder.model.user;

import backend.futurefinder.model.media.Media;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public final class UserInfo {

    private final UserId userId;
    private final String userName;
    private final String nickName;
    private final String accountId;
    private final String password;
    private final Media image;
    private final AccessStatus status;


    private UserInfo(
            UserId userId,
            String userName,
            String nickName,
            String accountId,
            String password, Media image,
            AccessStatus status
    ) {
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.accountId = accountId;
        this.password = password;
        this.image = image;
        this.status = status;
    }

    public static UserInfo of(
            UserId userId,
            String userName,
            String nickName,
            String accountId,
            String password,
            Media image,
            AccessStatus status
    ) {
        return new UserInfo(
                userId,
                userName,
                nickName,
                accountId,
                password,
                image,
                status
        );
    }
}
