package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeliasResponse {

    public List<PeliasEntry> features;

    @Override
    public String toString() {
        return "results:" + features.size();
    }
}
