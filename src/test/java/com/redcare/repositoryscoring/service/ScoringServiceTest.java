package com.redcare.repositoryscoring.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ScoringServiceTest {
    @Autowired
    private ScoringService scoringService;

    @ParameterizedTest
    @MethodSource("scoreCases")
    void shouldCalculateScoreCorrectly(
            long stars,
            long forks,
            long daysAgo,
            double expectedScore
    ) {

        Instant updatedAt = Instant.now().minus(daysAgo, ChronoUnit.DAYS);

        double score = scoringService.calculateScore(stars, forks, updatedAt);

        assertEquals(expectedScore, score);
    }

    static Stream<Arguments> scoreCases() {

        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        100, 50, 0,
                        85.0
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        100, 50, 10,
                        81.0
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        0, 0, 120,
                        0
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        500, 200, 30,
                        326.0
                )
        );
    }

}
