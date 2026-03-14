package com.redcare.repositoryscoring.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "repository.scoring")
@Data
public class RepositoryScoringProperties {
    private double starsWeight;
    private double forksWeight;
    private double recencyWeight;
    private Map<Integer, Double> recencyThresholds = new LinkedHashMap<>();

}
