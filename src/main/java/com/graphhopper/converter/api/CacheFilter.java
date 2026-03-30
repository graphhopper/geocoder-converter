package com.graphhopper.converter.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Provider
@PreMatching
public class CacheFilter implements ContainerResponseFilter {

    private static final Cache<String, Response> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (!"GET".equalsIgnoreCase(requestContext.getMethod())) {
            return; // Only cache GET requests
        }

        // TODO headers are ignored. shouldn't be a problem

        String requestKey = requestContext.getUriInfo().getRequestUri().toString();
        Response cachedResponse = cache.getIfPresent(requestKey);
        if (cachedResponse != null) {
            // LoggerFactory.getLogger(getClass()).info("now accessing cache");
            responseContext.setStatus(cachedResponse.getStatus());
            responseContext.setEntity(cachedResponse.getEntity());
            return;
        }

        cache.put(requestKey, Response.status(responseContext.getStatus())
                .entity(responseContext.getEntity())
                .build());
    }
}
