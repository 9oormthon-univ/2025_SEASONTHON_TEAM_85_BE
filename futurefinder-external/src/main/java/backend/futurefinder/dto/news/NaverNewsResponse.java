package backend.futurefinder.dto.news;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** 네이버 뉴스 검색 응답 DTO (external 전용, record) */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverNewsResponse(
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<Item> items
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            String title,
            String originallink,
            String link,
            String description,
            String pubDate
    ) {}
}