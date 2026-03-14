package com.redcare.repositoryscoring.service;

import com.redcare.repositoryscoring.config.RepositoryScoringProperties;
import com.redcare.repositoryscoring.service.dto.GithubRepository;
import com.redcare.repositoryscoring.service.dto.GithubSearchResponse;
import com.redcare.repositoryscoring.service.dto.RepositoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScoringService {

    private final RepositoryScoringProperties scoringProperties;

    public RepositoryResponse enhanceWithPopularity(int page, GithubSearchResponse githubSearchResponse) {
        if (githubSearchResponse.incompleteResults()) {
            return new RepositoryResponse(githubSearchResponse.totalCount(), page, 0, List.of());
        }
        try {
            List<RepositoryResponse.EnrichedGithubRepo> enrichedGithubRepos = githubSearchResponse.items().stream()
                    .map(this::mapWithScore)
                    .sorted(Comparator.comparingDouble(RepositoryResponse.EnrichedGithubRepo::popularityScore).reversed())
                    .toList();
            return new RepositoryResponse(githubSearchResponse.totalCount(), page, enrichedGithubRepos.size(), enrichedGithubRepos);
        } catch (Exception e) {
            log.error("Error while calculating popularity scores", e);
            throw new RuntimeException("Failed to calculate popularity scores", e);
        }
    }

    private RepositoryResponse.EnrichedGithubRepo mapWithScore(GithubRepository repo) {
        double popularityScore = calculateScore(
                repo.stargazersCount(),
                repo.forksCount(),
                repo.updatedAt()
        );
        return new RepositoryResponse.EnrichedGithubRepo(
                repo.name(),
                repo.language(),
                repo.createdAt(),
                repo.stargazersCount(),
                repo.forksCount(),
                repo.updatedAt(),
                popularityScore
        );
    }

    public double calculateScore(long stars, long forks, Instant updatedAt) {
        long days = Duration.between(updatedAt, Instant.now()).toDays();
        return stars * scoringProperties.getStarsWeight() + forks * scoringProperties.getForksWeight() + (getRecency(days) * scoringProperties.getRecencyWeight()) * 100;
    }

    private double getRecency(long days) {
        Map<Integer, Double> recencyThresholds = scoringProperties.getRecencyThresholds();
        double defaultScore = 0.0;
        for (Map.Entry<Integer, Double> entry : recencyThresholds.entrySet()) {
            if (days <= entry.getKey()) {
                return entry.getValue();
            }

        }
        return defaultScore;
    }
}
