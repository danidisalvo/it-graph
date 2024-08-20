package com.probendi.itgraph;

import jakarta.validation.constraints.NotBlank;

/**
 * The DTO of an edge.
 */
public record Edge(@NotBlank String source, @NotBlank String target) {
}
