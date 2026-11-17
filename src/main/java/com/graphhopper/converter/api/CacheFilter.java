package com.graphhopper.converter.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Answers repeated GET requests from memory without calling the provider. Only 200 responses are stored, as
 * serialized bytes (a parsed geometry needs ~7x its JSON size), bounded by total size.
 */
public class CacheFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String HIT = "cache_hit";

    private record Entry(byte[] body, MediaType mediaType) {
    }

    private final ObjectMapper objectMapper;
    private final Cache<String, Entry> cache;

    public CacheFilter(ObjectMapper objectMapper, long maxBytes, long ttlMinutes) {
        this.objectMapper = objectMapper;
        this.cache = CacheBuilder.newBuilder()
                .maximumWeight(maxBytes)
                .<String, Entry>weigher((key, entry) -> entry.body.length)
                .expireAfterWrite(ttlMinutes, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        if (!"GET".equals(requestContext.getMethod()))
            return;
        Entry entry = cache.getIfPresent(key(requestContext));
        if (entry != null) {
            requestContext.setProperty(HIT, true);
            requestContext.abortWith(Response.ok(entry.body, entry.mediaType).build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // aborted responses pass the response filters too
        if (!"GET".equals(requestContext.getMethod()) || requestContext.getProperty(HIT) != null
                || responseContext.getStatus() != 200 || !responseContext.hasEntity())
            return;
        byte[] body = responseContext.getEntity() instanceof byte[] bytes ? bytes : objectMapper.writeValueAsBytes(responseContext.getEntity());
        responseContext.setEntity(body);
        cache.put(key(requestContext), new Entry(body, responseContext.getMediaType()));
    }

    private static String key(ContainerRequestContext requestContext) {
        URI uri = requestContext.getUriInfo().getRequestUri();
        return uri.getRawPath() + "?" + uri.getRawQuery();
    }
}
