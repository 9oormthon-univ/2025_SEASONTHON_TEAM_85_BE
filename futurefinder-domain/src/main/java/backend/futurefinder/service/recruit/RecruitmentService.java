package backend.futurefinder.service.recruit;


import backend.futurefinder.implementation.recruit.RecruitmentReader;
import backend.futurefinder.model.recruit.RecruitmentItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentReader recruitmentReader;

    public List<RecruitmentItem> getRecruitmentList(int page) {
        return recruitmentReader.reads(page);
    }
}
