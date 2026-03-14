package com.redcare.repositoryscoring.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record RepositoryResponse(@JsonProperty("total_count")
                                 int totalCount,
                                 int page,
                                 int limit,
                                 List<EnrichedGithubRepo> items
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EnrichedGithubRepo(
            String name,
            String language,
            @JsonProperty("created_at")
            Instant createdAt,
            long stars,
            long forks,
            @JsonProperty("updated_at")
            Instant updatedAt,
            @JsonProperty("popularity_score")
            double popularityScore) {

    }


}
