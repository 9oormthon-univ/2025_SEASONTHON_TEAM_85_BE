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

@Service
@RequiredArgsConstructor
@Slf4j
public class JobAIRecommendationServiceImpl implements JobAIRecommendationService {

    private final JobService jobService;
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;

    @Override
    public List<RecommendedActivityResponse> generateRecommendedActivities(String userId) {
        try {
            String userProfile = buildUserProfile(userId);
            String prompt = buildActivityPrompt(userProfile);

            List<ChatMessage> messages = List.of(
                    ChatMessage.system("당신은 한국의 대학생 취업 및 대외활동 전문가입니다. JSON 형식으로만 응답해주세요."),
                    ChatMessage.user(prompt)
            );

            String aiResponse = openAIService.getChatCompletion(messages);
            return parseActivityRecommendations(aiResponse);

        } catch (Exception e) {
            log.error("대외활동 추천 생성 실패: ", e);
            return getDefaultActivityRecommendations();
        }
    }

    @Override
    public List<RecommendedJobResponse> generateRecommendedJobs(String userId) {
        try {
            String userProfile = buildUserProfile(userId);
            String prompt = buildJobPrompt(userProfile);

            List<ChatMessage> messages = List.of(
                    ChatMessage.system("당신은 한국의 취업 전문가입니다. JSON 형식으로만 응답해주세요."),
                    ChatMessage.user(prompt)
            );

            String aiResponse = openAIService.getChatCompletion(messages);
            return parseJobRecommendations(aiResponse);

        } catch (Exception e) {
            log.error("취업 공고 추천 생성 실패: ", e);
            return getDefaultJobRecommendations();
        }
    }

    private String buildUserProfile(String userId) {
        var educations = jobService.findEducationsByUserId(userId);
        var activities = jobService.findActivitiesByUserId(userId);
        var awards = jobService.findAwardsByUserId(userId);

        StringBuilder profile = new StringBuilder();

        if (!educations.isEmpty()) {
            profile.append("학업 정보: ");
            educations.forEach(edu ->
                    profile.append(String.format("%s %s %s, ",
                            edu.schoolName(), edu.major(), edu.status()))
            );
            profile.append("\n");
        }

        if (!activities.isEmpty()) {
            profile.append("대외활동 경험: ");
            activities.forEach(act ->
                    profile.append(String.format("%s - %s, ", act.type(), act.title()))
            );
            profile.append("\n");
        }

        if (!awards.isEmpty()) {
            profile.append("수상 내역: ");
            awards.forEach(award ->
                    profile.append(String.format("%s (%s), ",
                            award.awardName(), award.organization()))
            );
            profile.append("\n");
        }

        return profile.length() > 0 ? profile.toString() : "신입 학생";
    }

    private String buildActivityPrompt(String userProfile) {
        return String.format("""
        다음 사용자 정보를 바탕으로 마감일이 임박한 추천 대외활동 3개를 생성해주세요:
        
        사용자 정보:
        %s
        
        마감일이 가장 임박한 순서대로 정렬해서 추천해주세요.
        
        다음 JSON 형식으로만 응답해주세요 (다른 텍스트 없이):
        [
          {
            "title": "활동명",
            "type": "CLUB",
            "description": "활동 설명",
            "period": "활동 시기",
            "benefits": "참여 시 이익"
          },
          {
            "title": "활동명",
            "type": "COMPETITION", 
            "description": "활동 설명",
            "period": "활동 시기",
            "benefits": "참여 시 이익"
          },
          {
            "title": "활동명",
            "type": "INTERNSHIP",
            "description": "활동 설명", 
            "period": "활동 시기",
            "benefits": "참여 시 이익"
          }
        ]
        """, userProfile);
    }

    private String buildJobPrompt(String userProfile) {
        return String.format("""
            다음 사용자 정보를 바탕으로 맞춤형 취업 공고 3개를 생성해주세요:
            
            사용자 정보:
            %s
            
            다음 JSON 형식으로만 응답해주세요 (다른 텍스트 없이):
            [
              {
                "companyName": "회사명",
                "position": "포지션명",
                "description": "직무 설명",
                "requirements": "지원 자격 요건",
                "deadline": "D-30",
                "matchReason": "이 공고를 추천하는 이유"
              },
              {
                "companyName": "회사명",
                "position": "포지션명", 
                "description": "직무 설명",
                "requirements": "지원 자격 요건",
                "deadline": "D-45",
                "matchReason": "이 공고를 추천하는 이유"
              },
              {
                "companyName": "회사명",
                "position": "포지션명",
                "description": "직무 설명", 
                "requirements": "지원 자격 요건",
                "deadline": "D-60",
                "matchReason": "이 공고를 추천하는 이유"
              }
            ]
            """, userProfile);
    }

    private List<RecommendedActivityResponse> parseActivityRecommendations(String aiResponse) {
        try {
            String jsonPart = extractJsonArray(aiResponse);
            return objectMapper.readValue(jsonPart, new TypeReference<List<RecommendedActivityResponse>>() {});
        } catch (Exception e) {
            log.warn("AI 응답 파싱 실패, 기본값 반환: {}", e.getMessage());
            return getDefaultActivityRecommendations();
        }
    }

    private List<RecommendedJobResponse> parseJobRecommendations(String aiResponse) {
        try {
            String jsonPart = extractJsonArray(aiResponse);
            return objectMapper.readValue(jsonPart, new TypeReference<List<RecommendedJobResponse>>() {});
        } catch (Exception e) {
            log.warn("AI 응답 파싱 실패, 기본값 반환: {}", e.getMessage());
            return getDefaultJobRecommendations();
        }
    }

    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']') + 1;
        if (start != -1 && end > start) {
            return response.substring(start, end);
        }
        throw new RuntimeException("JSON 배열을 찾을 수 없습니다");
    }

    private List<RecommendedActivityResponse> getDefaultActivityRecommendations() {
        return List.of(
                new RecommendedActivityResponse(
                        "대학생 IT 동아리", "CLUB",
                        "프로그래밍 및 프로젝트 경험을 쌓을 수 있는 동아리",
                        "학기 중", "실무 경험 및 네트워킹"
                ),
                new RecommendedActivityResponse(
                        "창업 경진대회", "COMPETITION",
                        "혁신적인 아이디어로 사업계획서를 작성하는 대회",
                        "방학 중", "창업 역량 및 상금"
                ),
                new RecommendedActivityResponse(
                        "인턴십 프로그램", "INTERNSHIP",
                        "실무 경험을 쌓을 수 있는 단기 인턴",
                        "여름/겨울방학", "실무 경험 및 취업 연계"
                )
        );
    }

    private List<RecommendedJobResponse> getDefaultJobRecommendations() {
        return List.of(
                new RecommendedJobResponse(
                        "네이버", "신입 개발자",
                        "웹/앱 서비스 개발 및 운영",
                        "컴퓨터공학 전공, 프로그래밍 경험",
                        "D-30", "IT 분야 대표 기업으로 성장 기회가 많음"
                ),
                new RecommendedJobResponse(
                        "카카오", "데이터 분석가",
                        "서비스 데이터 분석 및 인사이트 도출",
                        "통계학/경영학 전공, 데이터 분석 경험",
                        "D-45", "데이터 기반 의사결정 경험을 쌓을 수 있음"
                ),
                new RecommendedJobResponse(
                        "삼성전자", "마케팅 전문가",
                        "제품 마케팅 전략 수립 및 실행",
                        "마케팅/경영학 전공, 관련 경험",
                        "D-60", "글로벌 기업에서 마케팅 전문성을 기를 수 있음"
                )
        );
    }
}