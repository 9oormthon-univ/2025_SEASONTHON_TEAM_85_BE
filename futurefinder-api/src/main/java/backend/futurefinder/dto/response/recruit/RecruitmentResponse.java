package backend.futurefinder.dto.response.recruit;

import backend.futurefinder.model.news.NewsItem;
import backend.futurefinder.model.recruit.RecruitmentItem;

public record RecruitmentResponse (
        String institute,   // 공시기관
        String title,       // 공고(제목)
        String recruitType, // 채용구분
        String hireType     // 고용형태
){
    public static RecruitmentResponse of(RecruitmentItem item) {
        return new RecruitmentResponse(item.getInstitute(), item.getTitle(), item.getRecruitType(), item.getHireType());
    }

}
