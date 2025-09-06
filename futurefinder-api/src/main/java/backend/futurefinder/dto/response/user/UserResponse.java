package backend.futurefinder.dto.response.user;

import backend.futurefinder.model.user.UserInfo;

public record UserResponse (
        String username,
        String nickname,
        String email,
        String phoneNumber,
        String birth,
        String imageUrl
){

    public static UserResponse of(UserInfo userInfo) {
        return new UserResponse(userInfo.getUserName(), userInfo.getNickName(), userInfo.getEmail(), userInfo.getPhoneNumber(), userInfo.getBirth(), userInfo.getImage().getUrl());
    }


}
