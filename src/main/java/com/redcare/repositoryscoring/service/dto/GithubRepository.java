package com.redcare.repositoryscoring.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record GithubRepository(long id,
                               String name,
                               String language,
                               @JsonProperty("stargazers_count")
                               long stargazersCount,
                               @JsonProperty("forks_count")
                               long forksCount,
                               @JsonProperty("created_at")
                               Instant createdAt,
                               @JsonProperty("updated_at")
                               Instant updatedAt) {
}
