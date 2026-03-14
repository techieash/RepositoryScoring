package com.redcare.repositoryscoring.web;

import com.redcare.repositoryscoring.client.GithubClient;
import com.redcare.repositoryscoring.service.CachedGithubClient;
import com.redcare.repositoryscoring.service.dto.GithubRepository;
import com.redcare.repositoryscoring.service.dto.GithubSearchResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GithubRepositoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CachedGithubClient githubClient;

    private static final String BASE_URL = "/api/github-repositories";


    @Test
    public void shouldReturnRepositories_whenValidRequest() throws Exception {
        GithubRepository repository = getRepository("TestRepo", "java", 100, 50, Instant.now().minus(10, ChronoUnit.DAYS), Instant.now());
        GithubSearchResponse mockResponse = new GithubSearchResponse(1, false, List.of(repository));
        Mockito.when(githubClient.searchRepositories(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockResponse);

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2026-01-01")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnRepositoriesSortedByPopularity_whenValidRequest() throws Exception {
        GithubSearchResponse mockResponse = new GithubSearchResponse(1, false, List.of(getRepository("randomRepo", "java", 50, 20, Instant.now().minus(10, ChronoUnit.DAYS), Instant.now()),
                getRepository("AnotherRepo", "java", 100, 500, Instant.now().minus(20, ChronoUnit.DAYS), Instant.now().minus(1, ChronoUnit.DAYS)),
                getRepository("TestRepo", "java", 500, 50, Instant.now().minus(10, ChronoUnit.DAYS), Instant.now().minus(3, ChronoUnit.DAYS))));
        Mockito.when(githubClient.searchRepositories(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockResponse);

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2026-01-01")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", Matchers.is("TestRepo")))
                .andExpect(jsonPath("$.items[1].name", Matchers.is("AnotherRepo")))
                .andExpect(jsonPath("$.items[2].name", Matchers.is("randomRepo")));
    }


    @Test
    void shouldUseDefaultPagination_whenPageAndLimitMissing() throws Exception {
        GithubRepository repository = getRepository("TestRepo", "java", 100, 50, Instant.now().minus(10, ChronoUnit.DAYS), Instant.now());
        GithubSearchResponse mockResponse = new GithubSearchResponse(1, false, List.of(repository));
        Mockito.when(githubClient.searchRepositories(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(mockResponse);


        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2024-01-01"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFail_whenLanguageMissing() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("createdAfter", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_whenCreatedAfterMissing() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_whenPageIsZero() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2024-01-01")
                        .param("page", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_whenPageIsNegative() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2024-01-01")
                        .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_whenLimitTooLarge() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2024-01-01")
                        .param("limit", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("limit must be <= 100")));
    }

    @Test
    void shouldFail_whenPageNotANumber() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2024-01-01")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_whenLimitNotANumber() throws Exception {
        mockMvc.perform(get(BASE_URL)
                        .param("language", "java")
                        .param("createdAfter", "2024-01-01")
                        .param("limit", "xyz"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFail_whenEmptyLanguage() throws Exception {

        mockMvc.perform(get(BASE_URL)
                        .param("language", "")
                        .param("createdAfter", "2024-01-01"))
                .andExpect(status().isBadRequest());
    }

    private GithubRepository getRepository(String name, String language, long stars, long forks, Instant createdAt, Instant updatedAt) {
        return GithubRepository.builder()
                .id(UUID.randomUUID().getMostSignificantBits())
                .name(name)
                .language(language)
                .stargazersCount(stars)
                .forksCount(forks)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

}
