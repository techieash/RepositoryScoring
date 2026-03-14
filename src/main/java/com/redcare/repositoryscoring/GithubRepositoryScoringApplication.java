package com.redcare.repositoryscoring;

import com.redcare.repositoryscoring.config.RepositoryScoringProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RepositoryScoringProperties.class)
public class GithubRepositoryScoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubRepositoryScoringApplication.class, args);
	}

}
