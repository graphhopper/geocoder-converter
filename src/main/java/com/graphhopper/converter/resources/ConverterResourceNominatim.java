package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.NominatimEntry;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Boldt
 */
@Path("/nominatim")
@Produces("application/json; charset=utf-8")
public class ConverterResourceNominatim extends AbstractConverterResource {

    private final String nominatimUrl;
    private final String nominatimReverseUrl;
    private final String nominatimEmail;
    private final Client jerseyClient;

    public ConverterResourceNominatim(String nominatimUrl, String nominatimReverseUrl, String nominatimEmail, Client jerseyClient) {
        this.nominatimUrl = nominatimUrl;
        this.nominatimReverseUrl = nominatimReverseUrl;
        this.nominatimEmail = nominatimEmail;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("locale") @DefaultValue("") String locale,
                           @QueryParam("viewbox") @DefaultValue("") String viewbox,
                           @QueryParam("viewboxlbrt") @DefaultValue("") String viewboxlbrt,
                           @QueryParam("bounded") @DefaultValue("") String bounded,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("") String point
    ) {
        limit = fixLimit(limit);
        checkInvalidParameter(reverse, query, point);

        WebTarget target;
        if (reverse) {
            target = buildReverseTarget(point);
        } else {
            target = buildForwardTarget(query);
        }

        target = target.
                queryParam("limit", limit).
                queryParam("format", "json").
                queryParam("email", nominatimEmail).
                queryParam("addressdetails", "1");

        if (!locale.isEmpty()) {
            locale = getLocaleFromParameter(locale);
            target = target.queryParam("accept-language", locale);
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

        Response response = target.request().accept("application/json").get();
        Status status = failIfResponseNotSuccessful(target, response);

        try {
            List<NominatimEntry> entitiesFromResponse;
            if (reverse) {
                entitiesFromResponse = new ArrayList<>(1);
                entitiesFromResponse.add(0, response.readEntity(NominatimEntry.class));
            } else {
                entitiesFromResponse = response.readEntity(new GenericType<List<NominatimEntry>>() {
                });
            }
            return Converter.convertFromNominatimList(entitiesFromResponse, status, locale);
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target " + target.getUri() + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("error deserializing geocoding feed");
        } finally {
            response.close();
        }
    }

    private WebTarget buildForwardTarget(String query) {
        return jerseyClient.
                target(nominatimUrl).
                queryParam("q", query);
    }

    private WebTarget buildReverseTarget(String point) {
        String[] cords = point.split(",");
        String lat = cords[0];
        String lon = cords[1];
        return jerseyClient.
                target(nominatimReverseUrl).
                queryParam("lat", lat).
                queryParam("lon", lon);
    }
}
