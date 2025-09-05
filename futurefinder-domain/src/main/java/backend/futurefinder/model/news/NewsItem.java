package backend.futurefinder.model.news;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NewsItem {
    private final String title;     // 기사 제목
    private final String content;   // 요약(설명)
    private final String imageUrl;  // OG 이미지 (없으면 null)
    private final LocalDateTime publishedAt; // ← KST 기준 시간
    private final String press;              // ← 추가 (언론사)
}
