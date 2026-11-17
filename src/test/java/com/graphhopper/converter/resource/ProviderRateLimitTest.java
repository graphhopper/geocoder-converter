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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A provider that rate-limits us must lead to the same answer as dirigent's own rate limiter for that provider:
 * 429 with message "Please retry later", without retrying the provider.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class ProviderRateLimitTest {
    private static final AtomicInteger upstreamCalls = new AtomicInteger();
    private static final HttpServer upstream = startUpstream();

    static final DropwizardAppExtension<ConverterConfiguration> RULE = new DropwizardAppExtension<>(
            ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"),
            ConfigOverride.config("photonURL", "http://localhost:" + upstream.getAddress().getPort() + "/api/"));

    private static HttpServer startUpstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/api/", exchange -> {
                upstreamCalls.incrementAndGet();
                exchange.sendResponseHeaders(429, -1);
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

    @Test
    public void testProviderRateLimit() throws Exception {
        // not RULE.client(): it uses HttpClient's default retry strategy, which repeats a 429 itself
        HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + RULE.getLocalPort() + "/photon?q=berlin")).build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(429, response.statusCode());
        assertEquals("Please retry later", RULE.getObjectMapper().readValue(response.body(), Map.class).get("message"));
        assertEquals(1, upstreamCalls.get(), "a 429 must not be retried");
    }
}
