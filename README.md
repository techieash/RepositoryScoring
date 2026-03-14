# Repository Scoring Service

This project is a **Spring Boot 4 backend service** that fetches
repositories from the GitHub API and ranks them using a configurable
**popularity scoring algorithm**.

The service exposes a REST API that allows clients to search
repositories by **language** and **creation date**, returning results
sorted by **popularity score**.

------------------------------------------------------------------------

# Features

-   Fetch repositories from the GitHub Search API
-   Configurable popularity scoring algorithm
-   Pagination support (`page`, `limit`)
-   Recency-based ranking
-   Caching of external API calls
-   Validation and error handling
-   Unit and controller tests

------------------------------------------------------------------------

# Tech Stack

-   Java 21
-   Spring Boot 4
-   Spring Web MVC
-   Spring interface rest client
-   Spring Cache
-   Caffeine cache
-   JUnit 5
-   Mockito
-   Gradle

------------------------------------------------------------------------

# Architecture

The project follows a layered architecture:

Controller → Service → CachedGithubClient → GithubClient → GitHub API

Layer                Responsibility
  -------------------- ---------------------------------------
Controller           API endpoints and request validation
Service              Business logic and repository scoring
CachedGithubClient   Caching external API responses
GithubClient         Communication with GitHub API

------------------------------------------------------------------------

# API Endpoint

## Search Repositories

GET /api/github-repositories

### Query Parameters

|Parameter  |    Required |  Description|
 | --------------| ---------- |----------------------------------------|
language     |  yes |       Programming language|
createdAfter |  yes |       Filter repositories created after date with format YYYY-MM-DD|
|page  |         no  |       Page number (default = 1)
|limit  |        no    |     Results per page (default = 30)|
|sort  |          no    |     Sort by (default = score, options: score, stars, forks)
|order |          no    |     Sort order (default = desc, options: asc, desc)

### Example Request

GET /api/github-repositories?language=java&createdAfter=2026-01-01&limit=1

### Example Response
```json
{
"total_count": 588298,
"page": 1,
"limit": 1,
"items": [
{
"name": "interview-guide",
"language": "Java",
"created_at": "2026-01-03T11:25:48Z",
"stars": 960,
"forks": 229,
"updated_at": "2026-03-14T16:17:57Z",
"popularity_score": 568.7
}
]
}
```
------------------------------------------------------------------------

# Scoring Algorithm

Repositories are ranked using a configurable **popularity score**.

### Formula

score = (stars × starsWeight) + (forks × forksWeight) + (recency ×
recencyWeight)*100

Where:

-   stars → number of GitHub stars
-   forks → number of forks
-   recency → score based on last update time

### Implementation

``` java
public double calculateScore(long stars, long forks, Instant updatedAt) {
        long days = Duration.between(updatedAt, Instant.now()).toDays();
        return stars * scoringProperties.getStarsWeight() + forks * scoringProperties.getForksWeight() + (getRecency(days) * scoringProperties.getRecencyWeight()) * 100;
    }
```

------------------------------------------------------------------------

#  Score Calculation

Score calculation is derived from configurable thresholds.

Example configuration: in application.yaml

``` yaml
repository:
  scoring:
    stars-weight: 0.5 // 50% weight to stars
    forks-weight: 0.3 // 30% weight to forks
    recency-weight: 0.2 // 20% weight to recency
    recency-thresholds:
      0: 1.0
      30: 0.8
      60: 0.5
      180: 0.0
```

| Days Since Update | Recency Score |
|-------------------|-----|
| 0                 | 1.0 |
| <=30 days         | 0.8 |
| <=60 days         | 0.5 |        
| <=180+ days       | 0.0 |       

------------------------------------------------------------------------

# Caching(Performance Optimization)

External API calls are cached to reduce requests to GitHub and avoid
rate limits.

Example:

``` java
@Cacheable(
    value = "githubRepositories",
    key = "#query + '-' + #page + '-' + #limit",
)
public GithubSearchResponse searchRepositories(...)
```

Cache implementation:

-   Caffeine
-   TTL configurable

Example configuration:

``` 
 @Bean
    public CacheManager cacheManager() {

        CaffeineCacheManager manager = new CaffeineCacheManager();

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(100)
        );

        return manager;
    }
```

------------------------------------------------------------------------

# Rate Limiting

GitHub API limits requests:

Request Type      Limit
  ----------------- --------------------
Unauthenticated   60 requests/hour
Authenticated     5000 requests/hour

Authentication using a GitHub token is recommended.

export GITHUB_TOKEN=your_token

------------------------------------------------------------------------

# Validation

Request parameters are validated using Bean Validation.

Examples:

-   page ≥ 1
-   limit ≤ 100
-   required query parameters

Invalid requests return **HTTP 400**.

------------------------------------------------------------------------

# Testing

The project includes:

### Controller Tests

-   Valid request
-   Missing parameters
-   Invalid pagination
-   Non-numeric parameters

### Service Tests

-   Score calculation
-   Recency thresholds
-   Edge cases

Parameterized tests are used for multiple scoring scenarios.

Example:

``` java
@ParameterizedTest
@MethodSource("scoreCases")
void shouldCalculateScoreCorrectly(...)
```

------------------------------------------------------------------------

# Running the Application

### Build

./gradlew build

### Run

./gradlew bootRun

Application starts at:

http://localhost:8080

------------------------------------------------------------------------

# Future Improvements

Possible enhancements:

-   GitHub authentication support
-   More advanced ranking algorithms
-   Distributed caching (Redis) if it has to be deployed in a cluster
-   Circuit breaker for external API calls
-   Metrics and monitoring

------------------------------------------------------------------------

# Author

Backend coding challenge implementation demonstrating:

-   clean architecture
-   configurable scoring
-   caching
-   testing
-   modern Spring Boot practices
