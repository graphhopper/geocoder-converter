package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotonResponse {

    @JsonProperty("features")
    public List<PhotonEntry> features;

    @Override
    public String toString() {
        return "results:" + features.size();
    }
}
