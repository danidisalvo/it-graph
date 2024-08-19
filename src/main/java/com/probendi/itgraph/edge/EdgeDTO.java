package com.probendi.itgraph.edge;

import jakarta.validation.constraints.NotBlank;

/**
 * The DTO of an {@link Edge}.
 *
 * @param source the source
 * @param target the target
 */
public record EdgeDTO(@NotBlank(message = "source must not be blank") String source,
                      @NotBlank(message = "target must not be blank") String target) {
}
