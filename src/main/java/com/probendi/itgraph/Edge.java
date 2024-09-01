package com.probendi.itgraph;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The DTO of an edge. The graph is undirected, hence the need of implementing the {@link Comparable} interface.
 *
 * @param source the source
 * @param target the target
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
public record Edge(@NotBlank String source, @NotBlank String target) implements Comparable<Edge> {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Edge edge)) {
            return false;
        }
        return Objects.equals(source, edge.source) && Objects.equals(target, edge.target) ||
                Objects.equals(source, edge.target) && Objects.equals(target, edge.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }

    @Override
    public int compareTo(@NotNull Edge o) {
        if (this.equals(o)) {
            return 0;
        }
        var thisMin = source.compareTo(target) <= 0 ? source : target;
        var thisMax = source.compareTo(target) > 0 ? source : target;

        var otherMin = o.source.compareTo(o.target) <= 0 ? o.source : o.target;
        var otherMax = o.source.compareTo(o.target) > 0 ? o.source : o.target;

        int minComparison = thisMin.compareTo(otherMin);
        return minComparison == 0 ? thisMax.compareTo(otherMax) : minComparison;
    }
}
