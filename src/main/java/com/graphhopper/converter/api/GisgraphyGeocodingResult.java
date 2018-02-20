package com.graphhopper.converter.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphyGeocodingResult {


    public String numFound;
    public List<GisgraphyAddressEntry> result;
    public Status status;

    @Override
    public String toString() {
        return "results:" + result.size();
    }


}
