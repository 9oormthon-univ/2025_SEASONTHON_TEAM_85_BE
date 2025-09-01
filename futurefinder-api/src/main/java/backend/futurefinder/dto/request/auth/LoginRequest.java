package backend.futurefinder.dto.request.auth;

import backend.futurefinder.model.notification.PushInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String accountId;
    private String password;
    private String deviceId;
    private String provider;
    private String appToken;

    public String toAccountId() { return accountId; }


    public String toPassword() {
        return password;
    }

    public PushInfo.Device toDevice() {
        return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.toUpperCase()));
    }
    public LoginRequest(String accountId, String password,
                        String deviceId, String provider, String appToken) {
        this.accountId = accountId;
        this.password = password;
        this.deviceId = deviceId;
        this.provider = provider;
        this.appToken = appToken;
    }


    public String toAppToken() {
        return appToken;
    }
}
