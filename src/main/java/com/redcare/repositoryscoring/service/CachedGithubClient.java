package com.redcare.repositoryscoring.service;

import com.redcare.repositoryscoring.client.GithubClient;
import com.redcare.repositoryscoring.service.dto.GithubSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachedGithubClient {
    private final GithubClient githubClient;

    @Cacheable(
            value = "githubRepositories",
            key = "#query + '-' + #page + '-' + #limit"
    )
    public GithubSearchResponse searchRepositories(
            String query,
            String sortBy,
            String sortOrder,
            int page,
            int limit
    ) {
        return githubClient.searchRepositories(query, sortBy, sortOrder, page, limit);
    }
}
