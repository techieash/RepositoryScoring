package com.redcare.repositoryscoring.web;

import com.redcare.repositoryscoring.service.GithubService;
import com.redcare.repositoryscoring.service.dto.RepositoryResponse;
import com.redcare.repositoryscoring.web.dto.SearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/github-repositories")
@RequiredArgsConstructor
public class GithubRepositoryController {

    private final GithubService githubService;

    @GetMapping
    public ResponseEntity<RepositoryResponse> getRepositories(@Valid SearchRequest searchRequest) {
        RepositoryResponse repositories = githubService.getRepositories(searchRequest);
        return ResponseEntity.ok().body(repositories);
    }
}
