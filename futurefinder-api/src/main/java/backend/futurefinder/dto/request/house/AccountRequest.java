// request/house/AccountRequest.java
package backend.futurefinder.dto.request.house;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
        @NotBlank String bankName,
        @NotBlank String accountNumber
) {}
