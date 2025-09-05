package backend.futurefinder.model.recruit;

import backend.futurefinder.model.token.RefreshToken;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class RecruitmentItem {
    private final String institute;   // 공시기관
    private final String title;       // 공고(제목)
    private final String recruitType; // 채용구분
    private final String hireType;     // 고용형태

    public static RecruitmentItem of(String institute, String title, String recruitType, String hireType) {
        return new RecruitmentItem(institute, title, recruitType, hireType);
    }



}
