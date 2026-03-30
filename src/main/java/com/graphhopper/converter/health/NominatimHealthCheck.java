package com.graphhopper.converter.health;

import com.codahale.metrics.health.HealthCheck;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

/**
 * @author Robin Boldt
 */
public class NominatimHealthCheck extends HealthCheck {
    private final String nominatimUrl;
    private final Client jerseyClient;

    public NominatimHealthCheck(String nominatimUrl, Client jerseyClient) {
        this.nominatimUrl = nominatimUrl;
        this.jerseyClient = jerseyClient;
    }

    @Override
    protected Result check() throws Exception {
        Response response = jerseyClient.
                target(nominatimUrl).
                queryParam("q", "berlin").
                queryParam("limit", 1).
                queryParam("format", "json").
                queryParam("addressdetails", "1").
                request().
                accept("application/json").
                get();

        if (response.getStatus() != 200) {
            return Result.unhealthy("There exists an issue with Nominatim. " + response.readEntity(String.class));
        }
        return Result.healthy();
    }
}