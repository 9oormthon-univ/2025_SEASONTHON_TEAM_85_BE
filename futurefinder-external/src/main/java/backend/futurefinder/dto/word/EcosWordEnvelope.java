package backend.futurefinder.dto.word;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EcosWordEnvelope(
        @JsonProperty("StatisticWord") StatisticWord statisticWord
) {}