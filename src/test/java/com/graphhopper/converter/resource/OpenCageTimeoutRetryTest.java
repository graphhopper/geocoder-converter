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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An OpenCage request that hangs is given up after 5s and tried once more, which can then succeed.
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class OpenCageTimeoutRetryTest {
    private static final AtomicInteger upstreamCalls = new AtomicInteger();
    private static final HttpServer upstream = startUpstream();

    static final DropwizardAppExtension<ConverterConfiguration> RULE = new DropwizardAppExtension<>(
            ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"),
            ConfigOverride.config("openCageDataURL", "http://localhost:" + upstream.getAddress().getPort() + "/geocode/v1/json"),
            ConfigOverride.config("openCageDataKey", "test"));

    private static HttpServer startUpstream() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.setExecutor(Executors.newCachedThreadPool());  // the retry must not wait for the hanging request
            server.createContext("/geocode/v1/json", exchange -> {
                if (upstreamCalls.incrementAndGet() == 1) {
                    try {
                        Thread.sleep(7_000);  // hang beyond the 5s timeout
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                byte[] body = "{\"results\":[],\"status\":{\"code\":200,\"message\":\"OK\"}}".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                try {
                    exchange.sendResponseHeaders(200, body.length);
                    exchange.getResponseBody().write(body);
                } catch (IOException ignored) {
                    // the converter gave up on the first request already
                }
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
    public void testHangingRequestIsRetried() throws Exception {
        long start = System.nanoTime();
        // not RULE.client(): it has its own retry strategy
        HttpResponse<String> response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + RULE.getLocalPort() + "/opencagedata?q=berlin")).build(), HttpResponse.BodyHandlers.ofString());
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        assertEquals(200, response.statusCode(), response.body());
        assertEquals(2, upstreamCalls.get());
        assertTrue(tookMs >= 5_000 && tookMs < 7_000, "gave up after the 5s timeout and retried, took " + tookMs + "ms");
    }
}
