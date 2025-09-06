// request/house/LocationRequest.java
package backend.futurefinder.dto.request.house;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(
        @NotBlank String province,
        @NotBlank String city
) {}
