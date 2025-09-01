package backend.futurefinder.dto.request.auth;

import backend.futurefinder.model.notification.PushInfo;

public record LogoutRequest(
        String deviceId,
        String provider
) {
    public PushInfo.Device toDevice() {
        return PushInfo.Device.of(
                deviceId,
                PushInfo.Provider.valueOf(provider.toUpperCase())
        );
    }
}

