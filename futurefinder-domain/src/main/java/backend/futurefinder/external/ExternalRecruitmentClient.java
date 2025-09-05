package backend.futurefinder.external;

import backend.futurefinder.model.recruit.RecruitmentItem;

import java.util.List;

public interface ExternalRecruitmentClient {
    List<RecruitmentItem> fetch(int page);
}