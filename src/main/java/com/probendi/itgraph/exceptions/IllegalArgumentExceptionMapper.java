package com.probendi.itgraph.exceptions;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Allows RESTful endpoints to return custom error responses for {@link IllegalArgumentException}s.
 *
 * @author Daniele Di Salvo
 * @since 1.0.0
 */
@Provider
@SuppressWarnings("unused")
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    /**
     * Explicit empty constructor.
     */
    public IllegalArgumentExceptionMapper() {
    }

    @Override
    public Response toResponse(IllegalArgumentException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("BAD_REQUEST", e.getMessage()))
                .build();
    }
}
