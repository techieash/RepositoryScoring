package com.redcare.repositoryscoring.config;

import com.redcare.repositoryscoring.client.GithubClient;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
public class ApiClientConfig {

    private final String githubBaseUrl;
    private final String githubToken;
    private final ObjectMapper objectMapper;

    public ApiClientConfig(@Value("${client.github-url}") String githubBaseUrl,
                           @Value("${client.github-token}") String githubToken,
                           ObjectMapper objectMapper) {
        this.githubBaseUrl = githubBaseUrl;
        this.githubToken = githubToken;
        this.objectMapper = objectMapper;
    }

    @Bean
    public RestClient githubRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(10).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(30).toMillis());

        RestClient.Builder builder = RestClient.builder()
                .baseUrl(githubBaseUrl)
                .requestFactory(factory)
                .defaultStatusHandler(new ExternalClientErrorHandler(objectMapper))
                .requestInterceptor((request, body, execution) -> {
                    if (githubToken != null && !githubToken.isBlank()) request.getHeaders().setBearerAuth(githubToken);
                    return execution.execute(request, body);
                })
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28");

        return builder.build();
    }

    @Bean
    public GithubClient gitHubApiClient(RestClient githubRestClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(githubRestClient))
                .build();

        return factory.createClient(GithubClient.class);
    }
}
