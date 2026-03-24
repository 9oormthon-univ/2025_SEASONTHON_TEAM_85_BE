package backend.futurefinder.external;

import backend.futurefinder.dto.job.RecommendedActivityResponse;
import backend.futurefinder.dto.job.RecommendedJobResponse;
import backend.futurefinder.model.house.ChatMessage;
import backend.futurefinder.service.house.OpenAIService;
import backend.futurefinder.service.job.JobService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAIRecommendationServiceImpl implements JobAIRecommendationService {

    private final JobService jobService;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    @Override
    public List<RecommendedActivityResponse> generateRecommendedActivities(String userId) {
        return recommend(userId, ACTIVITY_SYSTEM_MSG, this::buildActivityPrompt,
                new TypeReference<>() {}, this::defaultActivities);
    }

    @Override
    public List<RecommendedJobResponse> generateRecommendedJobs(String userId) {
        return recommend(userId, JOB_SYSTEM_MSG, this::buildJobPrompt,
                new TypeReference<>() {}, this::defaultJobs);
    }

    /** 공통 추천 흐름: 프로필 구성 → 프롬프트 생성 → AI 호출 → JSON 파싱 (실패 시 기본값) */
    private <T> List<T> recommend(String userId, String systemMsg,
                                   java.util.function.Function<String, String> promptBuilder,
                                   TypeReference<List<T>> typeRef, Supplier<List<T>> fallback) {
        try {
            String profile = buildUserProfile(userId);
            String aiResponse = openAIService.getChatCompletion(List.of(
                    ChatMessage.system(systemMsg),
                    ChatMessage.user(promptBuilder.apply(profile))
            ));
            String json = aiResponse.substring(aiResponse.indexOf('['), aiResponse.lastIndexOf(']') + 1);
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            log.error("AI 추천 생성 실패: ", e);
            return fallback.get();
        }
    }

    private String buildUserProfile(String userId) {
        var educations = jobService.findEducationsByUserId(userId);
        var activities = jobService.findActivitiesByUserId(userId);
        var awards = jobService.findAwardsByUserId(userId);

        var sb = new StringBuilder();
        if (!educations.isEmpty())
            sb.append("학업 정보: ").append(educations.stream()
                    .map(e -> "%s %s %s".formatted(e.schoolName(), e.major(), e.status()))
                    .collect(Collectors.joining(", "))).append("\n");
        if (!activities.isEmpty())
            sb.append("대외활동 경험: ").append(activities.stream()
                    .map(a -> "%s - %s".formatted(a.type(), a.title()))
                    .collect(Collectors.joining(", "))).append("\n");
        if (!awards.isEmpty())
            sb.append("수상 내역: ").append(awards.stream()
                    .map(a -> "%s (%s)".formatted(a.awardName(), a.organization()))
                    .collect(Collectors.joining(", "))).append("\n");

        return sb.isEmpty() ? "신입 학생" : sb.toString();
    }

    // ---- 프롬프트 ----

    private static final String ACTIVITY_SYSTEM_MSG = "당신은 한국의 대학생 취업 및 대외활동 전문가입니다. JSON 형식으로만 응답해주세요.";
    private static final String JOB_SYSTEM_MSG = "당신은 한국의 취업 전문가입니다. JSON 형식으로만 응답해주세요.";

    private String buildActivityPrompt(String profile) {
        return """
                다음 사용자 정보를 바탕으로 마감일이 임박한 추천 대외활동 3개를 생성해주세요:

                사용자 정보:
                %s

                마감일이 가장 임박한 순서대로 정렬해서 추천해주세요.

                다음 JSON 형식으로만 응답해주세요 (다른 텍스트 없이):
                [{"title":"활동명","type":"CLUB","description":"활동 설명","period":"활동 시기","benefits":"참여 시 이익"}]
                """.formatted(profile);
    }

    private String buildJobPrompt(String profile) {
        return """
                다음 사용자 정보를 바탕으로 맞춤형 취업 공고 3개를 생성해주세요:

                사용자 정보:
                %s

                다음 JSON 형식으로만 응답해주세요 (다른 텍스트 없이):
                [{"companyName":"회사명","position":"포지션명","description":"직무 설명","requirements":"지원 자격 요건","deadline":"D-30","matchReason":"추천 이유"}]
                """.formatted(profile);
    }

    // ---- 기본값 ----

    private List<RecommendedActivityResponse> defaultActivities() {
        return List.of(
                new RecommendedActivityResponse("대학생 IT 동아리", "CLUB", "프로그래밍 및 프로젝트 경험을 쌓을 수 있는 동아리", "학기 중", "실무 경험 및 네트워킹"),
                new RecommendedActivityResponse("창업 경진대회", "COMPETITION", "혁신적인 아이디어로 사업계획서를 작성하는 대회", "방학 중", "창업 역량 및 상금"),
                new RecommendedActivityResponse("인턴십 프로그램", "INTERNSHIP", "실무 경험을 쌓을 수 있는 단기 인턴", "여름/겨울방학", "실무 경험 및 취업 연계")
        );
    }

    private List<RecommendedJobResponse> defaultJobs() {
        return List.of(
                new RecommendedJobResponse("네이버", "신입 개발자", "웹/앱 서비스 개발 및 운영", "컴퓨터공학 전공, 프로그래밍 경험", "D-30", "IT 분야 대표 기업으로 성장 기회가 많음"),
                new RecommendedJobResponse("카카오", "데이터 분석가", "서비스 데이터 분석 및 인사이트 도출", "통계학/경영학 전공, 데이터 분석 경험", "D-45", "데이터 기반 의사결정 경험을 쌓을 수 있음"),
                new RecommendedJobResponse("삼성전자", "마케팅 전문가", "제품 마케팅 전략 수립 및 실행", "마케팅/경영학 전공, 관련 경험", "D-60", "글로벌 기업에서 마케팅 전문성을 기를 수 있음")
        );
    }
}
