package backend.futurefinder.dto.word;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StatisticWord(
        @JsonProperty("list_total_count") Integer listTotalCount,
        @JsonProperty("row") List<Row> row,
        @JsonProperty("RESULT") Result result
) {}