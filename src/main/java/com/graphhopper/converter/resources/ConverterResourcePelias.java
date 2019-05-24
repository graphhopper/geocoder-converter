package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.PeliasResponse;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @author Robin Boldt
 */
@Path("/pelias")
@Produces("application/json; charset=utf-8")
public class ConverterResourcePelias extends AbstractConverterResource {

    private final String url;
    private final String key;
    private final Client jerseyClient;

    public ConverterResourcePelias(String url, String key, Client jerseyClient) {
        this.url = url;
        this.key = key;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("locale") @DefaultValue("") String locale,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("false") String point
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
                queryParam("size", limit).
                queryParam("api_key", this.key);

        if (!locale.isEmpty()) {
            locale = getLocaleFromParameter(locale);
            target = target.queryParam("lang", locale);
        }

        Response response = target.request().accept("application/json").get();
        Status status = failIfResponseNotSuccessful(target, response);


        try {
            PeliasResponse peliasResponse = response.readEntity(PeliasResponse.class);
            return Converter.convertFromPelias(peliasResponse, status, locale);
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target " + target.getUri() + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("error deserializing geocoding feed");
        } finally {
            response.close();
        }
    }

    private WebTarget buildForwardTarget(String query) {
        return jerseyClient.
                target(this.url + "search").
                queryParam("text", query);
    }

    private WebTarget buildReverseTarget(String point) {
        String[] cords = point.split(",");
        String lat = cords[0];
        String lon = cords[1];
        return jerseyClient.
                target(this.url + "reverse").
                queryParam("point.lat", lat).
                queryParam("point.lon", lon);
    }
}
