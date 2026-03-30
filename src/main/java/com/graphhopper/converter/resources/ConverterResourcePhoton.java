package com.graphhopper.converter.resources;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.PhotonResponse;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;

import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * @author Robin Boldt
 */
@Path("/photon")
@Produces("application/json; charset=utf-8")
public class ConverterResourcePhoton extends AbstractConverterResource {

    private final String photonUrl;
    private final String photonReverseUrl;
    private final Client jerseyClient;
    private final Set<String> supportedLanguages;

    public ConverterResourcePhoton(String photonUrl, String photonReverseUrl, Set<String> supportedLanguages, Client jerseyClient) {
        this.photonUrl = photonUrl;
        this.photonReverseUrl = photonReverseUrl;
        this.supportedLanguages = supportedLanguages;
        this.jerseyClient = jerseyClient;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
                           @QueryParam("limit") @DefaultValue("5") int limit,
                           @QueryParam("radius") @DefaultValue("1.0") double radius,
                           @QueryParam("locale") @DefaultValue("default") String locale,
                           @QueryParam("bbox") @DefaultValue("") String bbox,
                           @QueryParam("location_bias_scale") @DefaultValue("") String locationBiasScale,
                           @QueryParam("zoom") @DefaultValue("16") int zoom,
                           @QueryParam("osm_tag") List<String> osmTags,
                           @QueryParam("reverse") @DefaultValue("false") boolean reverse,
                           @QueryParam("point") @DefaultValue("") String point
    ) {
        limit = fixLimit(limit);
        checkInvalidParameter(reverse, query, point);

        if (query.length() > 300)
            throw new BadRequestException("q parameter cannot be longer than 300 characters");
        if (countSpaces(query) > 30)
            throw new BadRequestException("q parameter cannot contain more than 30 spaces");

        WebTarget target;
        if (reverse) {
            target = buildReverseTarget();
            target = target.queryParam("radius", radius);
        } else {
            target = buildForwardTarget(query);
            target = target.queryParam("zoom", zoom);
        }

        target = target.queryParam("limit", limit);

        if (!point.isEmpty()) {
            String[] cords = point.split(",");
            String lat = cords[0];
            String lon = cords[1];
            target = target.queryParam("lat", lat).queryParam("lon", lon);
        }

        if (!locale.isEmpty()) {
            if (!supportedLanguages.contains(locale))
                locale = "default";
            else
                locale = getLocaleFromParameter(locale);
            target = target.queryParam("lang", locale);
        }
        if (!bbox.isEmpty()) {
            target = target.queryParam("bbox", bbox);
        }
        if (!locationBiasScale.isEmpty()) {
            try {
                double num = Double.parseDouble(locationBiasScale);
                target = target.queryParam("location_bias_scale", num > 1 ? 1 : (num < 0 ? 0 : num));
            } catch (Exception ex) {
                throw new BadRequestException("location_bias_scale has invalid format " + locationBiasScale);
            }
        }

        for (String osmTag : osmTags) {
            target = target.queryParam("osm_tag", osmTag);
        }

        Response response = target.request().accept("application/json").get();
        Status status = failIfResponseNotSuccessful(target, response);

        try {
            PhotonResponse photonResponse = response.readEntity(PhotonResponse.class);
            return Converter.convertFromPhoton(photonResponse, status, locale);
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target " + target.getUri() + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("error deserializing geocoding feed");
        } finally {
            response.close();
        }
    }

    public static int countSpaces(String input) {
        int spaceCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ' ') {
                spaceCount++;
            }
        }
        return spaceCount;
    }

    private WebTarget buildForwardTarget(String query) {
        return jerseyClient.
                target(photonUrl).
                queryParam("q", query);
    }

    private WebTarget buildReverseTarget() {
        return jerseyClient.
                target(photonReverseUrl);
    }
}
