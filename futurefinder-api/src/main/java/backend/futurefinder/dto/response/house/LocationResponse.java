package backend.futurefinder.dto.response.house;

import backend.futurefinder.model.house.LocationEntry;

public record LocationResponse(
        String province,
        String city,
        String type
) {
    public static LocationResponse from(LocationEntry entry, String type) {
        return new LocationResponse(entry.getProvince(), entry.getCity(), type);
    }
}
