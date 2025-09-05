package backend.futurefinder.implementation.recruit;


import backend.futurefinder.external.ExternalRecruitmentClient;
import backend.futurefinder.model.recruit.RecruitmentItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecruitmentReader {

    private final ExternalRecruitmentClient externalRecruitmentClient;

    public List<RecruitmentItem> reads(int page) {
        return externalRecruitmentClient.fetch(page);
    }

}
