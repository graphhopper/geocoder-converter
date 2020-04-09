package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeojsonPoint {

    @JsonProperty("coordinates")
    public List<Double> coordinates;

    @JsonProperty("type")
    public String type;

    public Double getLat() {
        return ensureGeometryCapacity() ? this.coordinates.get(1) : null;
    }

    public Double getLon() {
        return ensureGeometryCapacity() ? this.coordinates.get(0) : null;
    }

    private boolean ensureGeometryCapacity() {
        return this.coordinates.size() == 2 && type.equals("Point");
    }

}

