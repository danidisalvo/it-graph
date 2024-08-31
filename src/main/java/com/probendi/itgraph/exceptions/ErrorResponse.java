package com.probendi.itgraph.exceptions;

/**
 * The custom error response returned by RESTful endpoints.
 *
 * @param message the error message
 * @param details the error details
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public record ErrorResponse(String message, String details) {
}
