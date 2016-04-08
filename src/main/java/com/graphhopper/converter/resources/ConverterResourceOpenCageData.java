package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.OpenCageDataResponse;
import com.graphhopper.converter.core.Converter;
import java.util.HashMap;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Robin Boldt
 */
@Path("/opencagedata")
@Produces(MediaType.APPLICATION_JSON)
public class ConverterResourceOpenCageData {

    private final String url;
    private final String key;
    private final Client jerseyClient;

    public ConverterResourceOpenCageData(String url, String key, Client jerseyClient) {
        this.url = url;
        this.key = key;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@NotNull @QueryParam("q") String query,
            @QueryParam("limit") @DefaultValue("5") int limit,
            @QueryParam("locale") @DefaultValue("") String locale,
            @QueryParam("countrycode") @DefaultValue("") String countrycode,
            @QueryParam("bounds") @DefaultValue("") String bounds
    ) {
        if (limit > 10) {
            limit = 10;
        }

        WebTarget target = jerseyClient.
                target(url).
                queryParam("q", query).
                queryParam("key", key).
                queryParam("no_annotations", "1").
                // queryParam("pretty", "1").
                queryParam("limit", limit);

        if (!locale.isEmpty()) {
            target = target.queryParam("language", locale);
        }

        if (!countrycode.isEmpty()) {
            target = target.queryParam("countrycode", countrycode);
        }

        if (!bounds.isEmpty()) {
            target = target.queryParam("bounds", bounds);
        }

        Response response = target.request().accept("application/json").
                get();

        OpenCageDataResponse ocdResponse = response.readEntity(OpenCageDataResponse.class);
        return Converter.convertFromOpenCageData(ocdResponse, ocdResponse.status);
    }
}
