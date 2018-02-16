package com.graphhopper.converter.resources;

import com.graphhopper.converter.api.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.WebTarget;
import java.util.Locale;
import java.util.MissingResourceException;

/**
 * @author Robin Boldt
 */
abstract class AbstractConverterResource {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    int fixLimit(int limit) {
        if (limit > 10) {
            return 10;
        }
        return limit;
    }

    void checkInvalidParameter(boolean reverse, String query, String point) {
        if (reverse) {
            if (point == null || point.trim().isEmpty()) {
                throw new BadRequestException("When setting reverse=true you have to pass the point parameter");
            }
            String[] cords = point.split(",");
            if (cords.length != 2) {
                throw new BadRequestException("You have to pass the point in the format \"lat,lon\"");
            }
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(cords[0]);
                lon = Double.parseDouble(cords[1]);
            } catch (RuntimeException e) {
                throw new BadRequestException("The coordinates of point need to be valid numbers");
            }
            if (Double.isNaN(lat) || Double.isNaN(lon) || lat < -180 || lat > 180 || lon < -90 || lon > 90) {
                throw new BadRequestException("The coordinates of point need to be valid coordinates");
            }
        } else {
            if (query == null || query.trim().isEmpty()) {
                throw new BadRequestException("q cannot be empty");
            }
        }
    }

    String getLocaleFromParameter(String locale) {
        Locale lo = Locale.forLanguageTag(locale);
        if (isValid(lo)) {
            return lo.toLanguageTag();
        } else {
            return "en";
        }
    }

    boolean isValid(Locale locale) {
        try {
            return locale.getISO3Language() != null && locale.getISO3Country() != null && !locale.toLanguageTag().equals("und");
        } catch (MissingResourceException e) {
            return false;
        }
    }

    void failIfResponseNotSuccessful(WebTarget target, Status status) {
        // TODO Maybe limit to == 200?
        if (status.code < 200 || status.code >= 300) {
            LOGGER.error("There was an issue with the target " + target.getUri() + " the provider returned: " + status.code + " - " + status.message);
            throw new BadRequestException("The geocoding provider responded with an unexpected error.");
        }
    }
}
