package com.redcare.repositoryscoring.service;

import com.redcare.repositoryscoring.client.GithubClient;
import com.redcare.repositoryscoring.service.dto.RepositoryResponse;
import com.redcare.repositoryscoring.web.dto.SearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class GithubService {

    private final CachedGithubClient cachedGithubClient;
    private final ScoringService scoringService;

    public RepositoryResponse getRepositories(SearchRequest searchRequest) {
        String query = buildSearchQuery(searchRequest.getLanguage(), searchRequest.getCreatedAfter());
        var response = cachedGithubClient.searchRepositories(query, searchRequest.getSortBy(), searchRequest.getSortDirection(), searchRequest.getLimit(), searchRequest.getPage());
        return scoringService.enhanceWithPopularity(searchRequest.getPage(), response);
    }


    private String buildSearchQuery(String language, String createdAfter) {
        return String.format("language:%s created:>%s", language, createdAfter);
    }
}
