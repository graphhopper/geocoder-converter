package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.OpenCageDataResponse;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;
import org.apache.commons.lang3.time.StopWatch;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * This class requests the geocoding service from opencagedata.com
 *
 * @author Peter Karich
 */
@Path("/opencagedata")
@Produces("application/json; charset=utf-8")
public class ConverterResourceOpenCageData extends AbstractConverterResource {

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
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("locale") @DefaultValue("") String locale,
                           @QueryParam("countrycode") @DefaultValue("") String countrycode,
                           @QueryParam("bounds") @DefaultValue("") String bounds,
                           @QueryParam("nominatim") @DefaultValue("false") boolean nominatim,
                           @QueryParam("find_osm_id") @DefaultValue("true") boolean find_osm_id,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("") String point
    ) {
        limit = fixLimit(limit);
        checkInvalidParameter(reverse, query, point);

        WebTarget target = jerseyClient.
                target(url).
                queryParam("q", reverse ? point : query).
                queryParam("limit", limit);

        target = target.queryParam("key", key);

        if (!find_osm_id) {
            target = target.queryParam("no_annotations", "1");
        }

        if (nominatim) {
            target = target.queryParam("only_nominatim", "1");
        }

        if (!locale.isEmpty()) {
            locale = getLocaleFromParameter(locale);
            target = target.queryParam("language", locale);
        }

        if (!countrycode.isEmpty()) {
            target = target.queryParam("countrycode", countrycode);
        }

        if (!bounds.isEmpty()) {
            target = target.queryParam("bounds", bounds);
        }

        StopWatch sw = new StopWatch();
        sw.start();
        Response response = target.request().accept("application/json").get();
        sw.stop();
        LOGGER.info("took:" + sw.getTime() / 1000f + " " + target.toString());

        Status status = failIfResponseNotSuccessful(target, response);

        try {
            OpenCageDataResponse ocdResponse = response.readEntity(OpenCageDataResponse.class);
            return Converter.convertFromOpenCageData(ocdResponse, ocdResponse.status, locale);
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target " + target.getUri() + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("error deserializing geocoding feed");
        } finally {
            response.close();
        }
    }
}
