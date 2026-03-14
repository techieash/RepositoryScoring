package com.redcare.repositoryscoring.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SearchRequest {
    @NotBlank(message = "language must not be blank")
    private String language;

    @NotBlank(message = "date must not be blank")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "createdAfter must be in format YYYY-MM-DD")
    private String createdAfter;

    @Min(value = 1, message = "page must be >= 1")
    private int page = 1;

    @Min(value = 1, message = "limit must be >= 1")
    @Max(value = 100, message = "limit must be <= 100")
    private int limit = 30;

    @Pattern(regexp = "stars|forks|updated|help-wanted-issues",
            message = "sortBy must be one of: stars, forks, updated, help-wanted-issues")
    private String sortBy = "stars";

    @Pattern(regexp = "asc|desc", message = "sortDirection must be asc or desc")
    private String sortDirection = "desc";

}
