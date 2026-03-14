package com.redcare.repositoryscoring.client;

import com.redcare.repositoryscoring.service.dto.GithubSearchResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface GithubClient {

    @GetExchange("/search/repositories")
    GithubSearchResponse searchRepositories(
            @RequestParam("q")        String query,
            @RequestParam("sort")     String sort,
            @RequestParam("order")    String order,
            @RequestParam("per_page") int perPage,
            @RequestParam("page")     int page
    );
}
