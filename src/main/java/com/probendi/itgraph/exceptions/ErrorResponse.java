package com.probendi.itgraph.exceptions;

/**
 * The custom error response returned by RESTful endpoints.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public record ErrorResponse(String message, String details) {
}
