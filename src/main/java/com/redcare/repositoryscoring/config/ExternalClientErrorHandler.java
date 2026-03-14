package com.redcare.repositoryscoring.config;

import com.redcare.repositoryscoring.web.errors.GitHubRateLimitException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
public class ExternalClientErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        int statusCode = response.getStatusCode().value();
        String body = new String(response.getBody().readAllBytes());

        if (statusCode == HttpStatus.TOO_MANY_REQUESTS.value() || statusCode == HttpStatus.FORBIDDEN.value() && body.toLowerCase().contains("rate limit")) {
            throw new GitHubRateLimitException(extractMessage(body));
        }
        ResponseErrorHandler.super.handleError(url, method, response);
    }

    private String extractMessage(String body) {
        try {
            JsonNode node = objectMapper.readTree(body);
            if (node.has("message")) return node.get("message").stringValue();
        } catch (Exception ignored) {
        }
        return body;
    }
}
