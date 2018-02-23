package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Extent {

    private final List<Double> coords = new ArrayList<>(4);

    public Extent() {
    }

    public Extent(double minLat, double minLon, double maxLat, double maxLon) {
        coords.add(minLon);
        coords.add(minLat);
        coords.add(maxLon);
        coords.add(maxLat);
    }

    @JsonProperty
    public List<Double> getExtent() {
        return coords;
    }

}
