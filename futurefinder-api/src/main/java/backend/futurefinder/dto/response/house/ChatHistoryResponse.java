package backend.futurefinder.dto.response.house;

import java.util.List;

public record ChatHistoryResponse(
        List<ChatResponse> messages,
        int totalCount
) {}