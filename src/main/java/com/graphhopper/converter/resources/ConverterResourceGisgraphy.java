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
	public static final String ADDRESS_PARAMETER = "address";
	public static final String LAT_PARAMETER = "lat";
	public static final String LONG_PARAMETER = "lng";
	public static final String COUNTRY_PARAMETER = "country";
	public static final String RADIUS_PARAMETER = "radius";
	public static final String DEFAULT_FORMAT = "json";
	public static final String FORMAT_PARAMETER = "format";
	public static final String APIKEY_PARAMETER = "apikey";
	// geocoding specid=fic
	public static final String GEOCODING_LIMIT_PARAMETER = "limitnbresult";
	// search
	public static final String SEARCH_LIMIT_PARAMETER = "to";
	public static final String SEARCH_QUERY_PARAMETER = "q";

	public static final String REVERSE_QUERY_TYPE = "reverse";
	public static final String GEOCODING_QUERY_TYPE = "geocoding";
	public static final String AUTOCOMPLETE_QUERY_TYPE = "autocomplete";

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
			@QueryParam("lat") @DefaultValue("") String lat,
			@QueryParam("lng") @DefaultValue("") String lng,
			@QueryParam("radius") @DefaultValue("") String radius,
			@QueryParam("country") @DefaultValue("") String country,
			@QueryParam("limit") @DefaultValue("5") Integer limit,
			@QueryParam("querytype") @DefaultValue("geocoding") String queryType) {
		limit = fixLimit(limit);

		WebTarget target;
		if (queryType.equalsIgnoreCase(REVERSE_QUERY_TYPE)) {
			target = buildReverseGeocodingTarget(query, lat, lng, radius,
					country, limit);
		} else if (queryType.equalsIgnoreCase(AUTOCOMPLETE_QUERY_TYPE)) {
			target = buildAutocompleteTarget(query, lat, lng, radius, country,
					limit);
		} else {
			queryType = GEOCODING_QUERY_TYPE;
			target = buildGeocodingTarget(query, lat, lng, radius, country,
					limit);
		}

		Response response = target.request().accept("application/json").get();
		Status status = new Status(response.getStatus(), response
				.getStatusInfo().getReasonPhrase());
		failIfResponseNotSuccessful(target, status);

		if (queryType.equalsIgnoreCase(GEOCODING_QUERY_TYPE)
				|| queryType.equalsIgnoreCase(REVERSE_QUERY_TYPE)) {
			GisgraphyGeocodingResult feed = response
					.readEntity(GisgraphyGeocodingResult.class);
			if (feed != null) {
				return Converter.convertFromGisgraphyList(feed.result, status);
			} else {
				LOGGER.error("There was an issue with the target "
						+ target.getUri() + " the provider returned: "
						+ status.code + " - " + status.message);
				throw new BadRequestException(
						"error deserializing geocoding feed");
			}
		} else if (queryType.equalsIgnoreCase(AUTOCOMPLETE_QUERY_TYPE)) {
			GisgraphySearchResult feed = response
					.readEntity(GisgraphySearchResult.class);
			if (feed != null) {
				return Converter.convertFromGisgraphySearchList(feed.getResponse(), status);
			} else {
				LOGGER.error("There was an issue with the target "
						+ target.getUri() + " the provider returned: "
						+ status.code + " - " + status.message);
				throw new BadRequestException(
						"error deserializing geocoding feed");
			}
		}
		return response;
	}

	private WebTarget buildGeocodingTarget(String query, String lat,
			String lng, String radius, String country, int limit) {
		WebTarget target = jerseyClient.target(geocodingUrl)
				.queryParam(ADDRESS_PARAMETER, query)
				.queryParam(FORMAT_PARAMETER, DEFAULT_FORMAT);
		if (!apiKey.isEmpty()) {
			target = target.queryParam(APIKEY_PARAMETER, apiKey);
		}
		if (!lat.isEmpty()) {
			target = target.queryParam(LAT_PARAMETER, lat);
		}
		if (!lng.isEmpty()) {
			target = target.queryParam(LONG_PARAMETER, lng);
		}
		if (!radius.isEmpty()) {
			target = target.queryParam(RADIUS_PARAMETER, radius);
		}
		if (!country.isEmpty()) {
			target = target.queryParam(COUNTRY_PARAMETER, country);
		}
		if (limit >0) {
			target = target.queryParam(GEOCODING_LIMIT_PARAMETER, limit);
		}
		return target;
	}

	private WebTarget buildReverseGeocodingTarget(String query, String lat,
			String lng, String radius, String country, int limit) {
		WebTarget target = jerseyClient.target(reverseGeocodingUrl).queryParam(
				FORMAT_PARAMETER, DEFAULT_FORMAT);
		if (!apiKey.isEmpty()) {
			target = target.queryParam(APIKEY_PARAMETER, apiKey);
		}
		if (!lat.isEmpty()) {
			target = target.queryParam(LAT_PARAMETER, lat);
		}
		if (!lng.isEmpty()) {
			target = target.queryParam(LONG_PARAMETER, lng);
		}
		return target;
	}

	private WebTarget buildAutocompleteTarget(String query, String lat,
			String lng, String radius, String country, int limit) {
		WebTarget target = jerseyClient.target(searchURL)
				.queryParam(SEARCH_QUERY_PARAMETER, query)
				.queryParam(FORMAT_PARAMETER, DEFAULT_FORMAT)
				.queryParam("suggest", "true");
		if (!apiKey.isEmpty()) {
			target = target.queryParam(APIKEY_PARAMETER, apiKey);
		}
		if (!lat.isEmpty()) {
			target = target.queryParam(LAT_PARAMETER, lat);
		}
		if (!lng.isEmpty()) {
			target = target.queryParam(LONG_PARAMETER, lng);
		}
		if (!radius.isEmpty()) {
			target = target.queryParam(RADIUS_PARAMETER, radius);
		}
		if (!country.isEmpty()) {
			target = target.queryParam(COUNTRY_PARAMETER, country);
		}
		if (limit >0) {
			target = target.queryParam(SEARCH_LIMIT_PARAMETER, limit);
		}
		return target;
	}

	private void checkInvalidParameter(boolean queryType) {
		// TODO Auto-generated method stub

	}

}
