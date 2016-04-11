package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.OpenCageDataResponse;
import com.graphhopper.converter.core.Converter;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class requests the geocoding service from opencagedata.com
 *
 * @author Peter Karich
 */
@Path("/opencagedata")
@Produces(MediaType.APPLICATION_JSON)
public class ConverterResourceOpenCageData {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConverterResourceOpenCageData.class);
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
            @QueryParam("bounds") @DefaultValue("") String bounds,
            @QueryParam("nominatim") @DefaultValue("false") boolean nominatim,
            @QueryParam("find_osm_id") @DefaultValue("true") boolean find_osm_id            
    ) {
        if (limit > 10) {
            limit = 10;
        }

        WebTarget target = jerseyClient.
                target(url).
                queryParam("q", query).
                queryParam("key", key).
                queryParam("limit", limit);

        if (!find_osm_id) {
            target = target.queryParam("no_annotations", "1");
        }

        if (nominatim) {
            target = target.queryParam("only_nominatim", "1");
        }

        if (!locale.isEmpty()) {
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

        OpenCageDataResponse ocdResponse = response.readEntity(OpenCageDataResponse.class);
        return Converter.convertFromOpenCageData(ocdResponse, ocdResponse.status);
    }
}
