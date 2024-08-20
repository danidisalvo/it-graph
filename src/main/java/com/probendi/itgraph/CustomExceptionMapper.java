package com.probendi.itgraph;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Allows RESTful endpoints to return custom error responses for {@link IllegalArgumentException}s.
 *
 * @since 1.0.0
 */
@Provider
public class CustomExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse("BAD_REQUEST", e.getMessage()))
                .build();
    }
}
