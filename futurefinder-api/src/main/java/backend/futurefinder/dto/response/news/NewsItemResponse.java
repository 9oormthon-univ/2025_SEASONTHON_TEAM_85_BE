package backend.futurefinder.dto.response.news;

import backend.futurefinder.controller.news.NewsController;
import backend.futurefinder.model.news.NewsItem;

import java.time.format.DateTimeFormatter;


public record NewsItemResponse(
        String title,
        String content,
        String imageUrl,
        String publishedAt, // ISO8601 문자열로 반환 (예: 2025-09-05T00:15:00Z)
        String press          // ← 추가
) {
    public static NewsItemResponse from(NewsItem i) {
        return new NewsItemResponse(
                i.getTitle(),
                i.getContent(),
                i.getImageUrl(),
                i.getPublishedAt() == null ? null : i.getPublishedAt().toString(),
                i.getPress()
        );
    }
}