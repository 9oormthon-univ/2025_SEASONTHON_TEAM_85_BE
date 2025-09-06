package backend.futurefinder.dto.request.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AwardUpsertRequest(
        @NotBlank(message = "수상명은 필수입니다")
        String awardName,

        @NotBlank(message = "수여기관은 필수입니다")
        String organization,

        @NotNull(message = "수상일자는 필수입니다")
        LocalDate awardedOn,

        String description // 선택사항
) {}