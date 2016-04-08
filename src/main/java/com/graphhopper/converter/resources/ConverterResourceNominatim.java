package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.GHResponse;
import com.graphhopper.converter.api.NominatimEntry;
import com.graphhopper.converter.api.OpenCageDataResponse;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;
import java.util.HashMap;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author Robin Boldt
 */
@Path("/nominatim")
@Produces(MediaType.APPLICATION_JSON)
public class ConverterResourceNominatim {

    private final String nominatimUrl;
    private final String nominatimEmail;
    private final Client jerseyClient;

    public ConverterResourceNominatim(String nominatimUrl, String nominatimEmail, Client jerseyClient) {
        this.nominatimUrl = nominatimUrl;
        this.nominatimEmail = nominatimEmail;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@NotNull @QueryParam("q") String query,
            @QueryParam("limit") @DefaultValue("5") int limit,
            @QueryParam("locale") @DefaultValue("") String locale,
            @QueryParam("viewbox") @DefaultValue("") String viewbox,
            @QueryParam("viewboxlbrt") @DefaultValue("") String viewboxlbrt,
            @QueryParam("bounded") @DefaultValue("") String bounded
    ) {
        if (limit > 10) {
            limit = 10;
        }

        WebTarget target = jerseyClient.
                target(nominatimUrl).
                queryParam("q", query).
                queryParam("limit", limit).
                queryParam("format", "json").
                queryParam("email", nominatimEmail).
                queryParam("addressdetails", "1");

        if (!locale.isEmpty()) {
            target = target.queryParam("locale", locale);
        }
        if (!viewbox.isEmpty()) {
            target = target.queryParam("viewbox", viewbox);
        }
        if (!viewboxlbrt.isEmpty()) {
            target = target.queryParam("viewboxlbrt", viewboxlbrt);
        }
        if (!bounded.isEmpty()) {
            target = target.queryParam("bounded", bounded);
        }

        Response response = target.request().accept("application/json").
                get();

        Status status = new Status(response.getStatus(), response.getStatusInfo().getReasonPhrase());
        List<NominatimEntry> entitiesFromResponse = response.readEntity(new GenericType<List<NominatimEntry>>() {
        });
        return Converter.convertFromNominatimList(entitiesFromResponse, status);
    }
}
