package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.NetToolKitResponse;
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
 * @author Xuejing Dong
 */
@Path("/nettoolkit")
@Produces("application/json; charset=utf-8")
public class ConverterResourceNetToolKit extends AbstractConverterResource {

    private final String netToolKitUrl;
    private final String netToolKitReverseUrl;
    private final String apiKey;
    private final Client jerseyClient;

    public ConverterResourceNetToolKit(String netToolKitUrl, 
                                       String netToolKitReverseUrl, 
                                       String netToolKitKey, 
                                       Client jerseyClient) {
        this.netToolKitUrl = netToolKitUrl;
        this.netToolKitReverseUrl = netToolKitReverseUrl;
        this.apiKey = netToolKitKey; 
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("false") String point,
                           @QueryParam("country_code") @DefaultValue("") String countryCode,
                           @QueryParam("source") @DefaultValue("nettoolkit") String provider)
    {
        limit = fixLimit(limit);
        checkInvalidParameter(reverse, query, point);
        WebTarget target;
        if (reverse) {
            target = buildReverseTarget(point);
        } else {
            target = buildForwardTarget(query, provider, countryCode);
        }
        Response response = target.request().accept("application/json").get();
        Status status = failIfResponseNotSuccessful(target, response);
        try {
            NetToolKitResponse ntkResponse = response.readEntity(NetToolKitResponse.class);
            return Converter.convertFromNetToolKitList(ntkResponse.results, status);
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target " + target.getUri() 
                + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("error deserializing geocoding feed");
        } finally {
            response.close();
        }
    }

    private WebTarget buildForwardTarget(String query, String provider, String countryCode) {
        return jerseyClient.
                target(netToolKitUrl).
                queryParam("address", query).
                queryParam("provider", provider).
                queryParam("country_code", countryCode).
                queryParam("key", apiKey);
    }

    private WebTarget buildReverseTarget(String point) {
        String[] cords = point.split(",");
        String lat = cords[0];
        String lon = cords[1];
        return jerseyClient.
                target(netToolKitReverseUrl).
                queryParam("latitude", lat).
                queryParam("longitude", lon).
                queryParam("key", apiKey);
    }
}

