package com.graphhopper.converter.resources;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.graphhopper.converter.api.GisgraphyGeocodingResult;
import com.graphhopper.converter.api.GisgraphySearchResult;
import com.graphhopper.converter.api.Status;
import com.graphhopper.converter.core.Converter;

/**
 * @author David Masclet
 */
@Path("/gisgraphy")
@Produces("application/json; charset=utf-8")
public class ConverterResourceGisgraphy extends AbstractConverterResource {

    // all webservices
    private static final String ADDRESS_PARAMETER = "address";
    private static final String LAT_PARAMETER = "lat";
    private static final String LONG_PARAMETER = "lng";
    private static final String COUNTRY_PARAMETER = "country";
    private static final String RADIUS_PARAMETER = "radius";
    private static final String DEFAULT_FORMAT = "json";
    private static final String FORMAT_PARAMETER = "format";
    private static final String APIKEY_PARAMETER = "apikey";
    // geocoding specid=fic
    private static final String GEOCODING_LIMIT_PARAMETER = "limitnbresult";
    // search
    private static final String SEARCH_LIMIT_PARAMETER = "to";
    private static final String SEARCH_QUERY_PARAMETER = "q";

    private final String geocodingUrl;
    private final String reverseGeocodingUrl;
    private final String searchURL;
    private final String apiKey;
    private final Client jerseyClient;

    public ConverterResourceGisgraphy(String geocodingUrl,
            String reverseGeocodingUrl, String searchURL, String apiKey,
            Client jerseyClient) {
        this.geocodingUrl = geocodingUrl;
        this.reverseGeocodingUrl = reverseGeocodingUrl;
        this.searchURL = searchURL;
        this.jerseyClient = jerseyClient;
        this.apiKey = apiKey;
    }

    @GET
    @Timed
    public Response handle(@QueryParam("q") @DefaultValue("") String query,
            @QueryParam("point") @DefaultValue("") String point,
            @QueryParam("radius") @DefaultValue("") String radius,
            @QueryParam("country") @DefaultValue("") String country,
            @QueryParam("limit") @DefaultValue("5") Integer limit,
            @QueryParam("reverse") @DefaultValue("false") boolean reverse,
            @QueryParam("autocomplete") @DefaultValue("false") boolean autocomplete) {
        limit = fixLimit(limit);
        checkParameters(query, reverse, autocomplete, point);

        String lat = null;
        String lng = null;
        if (point!=null && !point.isEmpty() && point.indexOf(",")>0){
            String[] cords = point.split(",");
            lat = cords[0];
            lng = cords[1];
        }

        WebTarget target;
        if (reverse) {
            target = buildReverseGeocodingTarget(query, lat, lng, radius,
                    country, limit);
        } else if (autocomplete) {
            target = buildAutocompleteTarget(query, lat, lng, radius, country,
                    limit);
        } else {
            target = buildGeocodingTarget(query, lat, lng, radius, country,
                    limit);
        }

        Response response = target.request().accept("application/json").get();
        Status status = new Status(response.getStatus(), response
                .getStatusInfo().getReasonPhrase());
        failIfResponseNotSuccessful(target, status);

        try {
            if (!autocomplete) {
                GisgraphyGeocodingResult feed = response
                        .readEntity(GisgraphyGeocodingResult.class);
                return Converter.convertFromGisgraphyList(feed.result, status);
            }
            else {
                GisgraphySearchResult feed = response
                        .readEntity(GisgraphySearchResult.class);
                return Converter.convertFromGisgraphySearchList(feed.getResponse(), status);
            }
        } catch (Exception e) {
            LOGGER.error("There was an issue with the target "
                    + target.getUri() + " the provider returned: "
                    + status.code + " - " + status.message);
            throw new BadRequestException(
                    "error deserializing geocoding feed");
        }
    }

    private void checkParameters(String query, boolean reverse, boolean autocomplete, String point) {
        super.checkInvalidParameter(reverse,query,point);
        if (reverse && autocomplete){
            throw new BadRequestException("autocomplete is not available in reverse geocoding request, set reverse or autocomplete to false but not both");
        }

    }

    private WebTarget buildGeocodingTarget(String query, String lat,
            String lng, String radius, String country, int limit) {
        WebTarget target = buildBaseTarget(lat, lng, geocodingUrl)
                .queryParam(ADDRESS_PARAMETER, query);
                target = addRadiusAndCountryToTarget(radius, country, target);
                if (limit >0) {
                    target = target.queryParam(GEOCODING_LIMIT_PARAMETER, limit);
                }
                return target;
    }

    private WebTarget buildAutocompleteTarget(String query, String lat,
            String lng, String radius, String country, int limit) {
        WebTarget target = buildBaseTarget(lat, lng, searchURL)
                .queryParam(SEARCH_QUERY_PARAMETER, query)
        .queryParam("suggest", "true");
        target = addRadiusAndCountryToTarget(radius, country, target);
        if (limit >0) {
            target = target.queryParam(SEARCH_LIMIT_PARAMETER, limit);
        }
        return target;
    }

    private WebTarget buildReverseGeocodingTarget(String query, String lat,
            String lng, String radius, String country, int limit) {
        return  buildBaseTarget(lat, lng, reverseGeocodingUrl);
    }

    protected WebTarget buildBaseTarget(String lat, String lng, String URL) {
        WebTarget target = jerseyClient.target(URL)
                .queryParam(FORMAT_PARAMETER, DEFAULT_FORMAT);

        if (apiKey!=null && !apiKey.isEmpty()) {
            target = target.queryParam(APIKEY_PARAMETER, apiKey);
        }
        if (lat!=null && !lat.isEmpty()) {
            target = target.queryParam(LAT_PARAMETER, lat);
        }
        if (lng!=null && !lng.isEmpty()) {
            target = target.queryParam(LONG_PARAMETER, lng);
        }
        return target;
    }

    protected WebTarget addRadiusAndCountryToTarget(String radius,
            String country, WebTarget target) {
        if (!radius.isEmpty()) {
            target = target.queryParam(RADIUS_PARAMETER, radius);
        }
        if (!country.isEmpty()) {
            target = target.queryParam(COUNTRY_PARAMETER, country);
        }
        return target;
    }

}
