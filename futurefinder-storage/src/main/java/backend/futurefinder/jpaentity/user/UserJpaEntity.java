package backend.futurefinder.jpaentity.user;


import backend.futurefinder.jpaentity.common.BaseEntity;
import backend.futurefinder.model.media.FileCategory;
import backend.futurefinder.model.media.Media;
import backend.futurefinder.model.media.MediaType;
import backend.futurefinder.model.user.AccessStatus;
import backend.futurefinder.model.user.UserId;
import backend.futurefinder.model.user.UserInfo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@DynamicInsert
@Table(
        name = "user",
        schema = "futurefinder",
        indexes = {
                @Index(name = "user_idx_userid", columnList = "userId")
        }

)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserJpaEntity extends BaseEntity {

    @Id
    private String userId = UUID.randomUUID().toString();

    @Column(name = "user_name")
    private String userName;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth")
    private String birth;


    @Column(name = "account_id")
    private String accountId;

    @Column(name = "password")
    private String password;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "picture_type")
    private MediaType pictureType;

    @Enumerated(EnumType.STRING)
    private AccessStatus status;




    @Builder
    public UserJpaEntity(
            String userName,
            String nickName,
            String accountId,
            String password,
            String pictureUrl,
            MediaType pictureType,
            AccessStatus status
    ) {
        this.userName = userName;
        this.nickName = nickName;
        this.accountId = accountId;
        this.password = password;
        this.pictureUrl = pictureUrl;
        this.pictureType = pictureType;
        this.status = status;
    }


    public static UserJpaEntity generate(String userName, String nickName, String accountId, AccessStatus access) {
        return UserJpaEntity.builder()
                .userName(userName)
                .nickName(nickName)
                .accountId(accountId)
                .password("")
                .pictureUrl("")
                .pictureType(MediaType.IMAGE_BASIC)
                .status(access)
                .build();
    }

    public UserInfo toUser() {
        return UserInfo.of(
                UserId.of(this.userId),
                this.userName,
                this.nickName,
                this.accountId,
                this.password,
                Media.of(FileCategory.PROFILE, this.pictureUrl, 0, this.pictureType),
                this.status,
                this.email,
                this.phoneNumber,
                this.birth
        );
    }


    public void updatePassword(String password) {
        this.password = password;
        this.status = AccessStatus.ACCESS;
    }


    public void updateUserPictureUrl(Media media) {
        this.pictureUrl = media.getUrl();
        this.pictureType = media.getType();
    }

    public void updateUserProfile(String userName, String email, String phoneNumber, String birth) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birth = birth;
    }


    public void updateAccessStatus(AccessStatus accessStatus) {
        this.status = accessStatus;
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }
    public UserId toUserId() {
        return UserId.of(this.userId);
    }



}
