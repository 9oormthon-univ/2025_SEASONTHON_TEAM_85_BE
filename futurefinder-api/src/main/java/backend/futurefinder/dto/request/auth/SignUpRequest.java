package backend.futurefinder.dto.request.auth;

import backend.futurefinder.model.notification.PushInfo;

import java.time.LocalDate;

public class SignUpRequest {

    public record Phone(
            String accountId,
            String userName,
            String nickName,
            String deviceId,
            String provider,
            String appToken
    ){

        public String toAccountId() {
            return accountId;
        }

        public PushInfo.Device toDevice() {
            return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.toUpperCase()));
        }

        public String toAppToken() {
            return appToken;
        }

        public String toUserName() {
            return userName;
        }

        public String toNickName() {
            return nickName;
        }






    }
    public record Password(
            String password
    ) {
        // password null 처리: 기본값을 "" 로 보정
        public Password {
            if (password == null) {
                password = "";
            }
        }
    }





}
