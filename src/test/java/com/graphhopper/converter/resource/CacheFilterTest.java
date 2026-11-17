package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.sun.net.httpserver.HttpServer;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
public class CacheFilterTest {
    private static final List<String> upstreamQueries = new ArrayList<>();
    private static volatile int upstreamStatus = 200;
    private static final HttpServer upstream = startUpstream();

    static final DropwizardAppExtension<ConverterConfiguration> RULE = new DropwizardAppExtension<>(
            ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"),
            ConfigOverride.config("photonURL", "http://localhost:" + upstream.getAddress().getPort() + "/api/"));

    private static HttpServer startUpstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/api/", exchange -> {
                upstreamQueries.add(exchange.getRequestURI().getQuery());
                byte[] body = ("{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[13.4,52.5]},"
                        + "\"properties\":{\"name\":\"Berlin\",\"osm_id\":1,\"osm_type\":\"N\",\"osm_key\":\"place\",\"osm_value\":\"city\"}}]}").getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(upstreamStatus, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @AfterAll
    public static void stopUpstream() {
        upstream.stop(0);
    }

    private Response get(String query) {
        return RULE.client().target("http://localhost:" + RULE.getLocalPort() + "/photon?" + query).request().get();
    }

    @Test
    public void testCache() {
        upstreamStatus = 500;
        assertEquals(500, get("q=berlin").getStatus());
        upstreamStatus = 200;
        Response first = get("q=berlin");
        assertEquals(200, first.getStatus());
        String firstBody = first.readEntity(String.class);
        assertEquals(2, upstreamQueries.size(), "error must not be cached");

        Response second = get("q=berlin");
        assertEquals(200, second.getStatus());
        assertEquals(first.getMediaType(), second.getMediaType());
        assertEquals(firstBody, second.readEntity(String.class));
        assertEquals(2, upstreamQueries.size(), "second request must come from cache");

        assertEquals(200, get("q=berlin&geometry=true&limit=50").getStatus());
        assertTrue(upstreamQueries.get(2).contains("limit=5&"), upstreamQueries.get(2));
    }
}
