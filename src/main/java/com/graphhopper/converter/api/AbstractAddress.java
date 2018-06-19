package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains shared address data between Nominatim and OpencageData
 *
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractAddress {

    @JsonProperty
    public String country;
    @JsonProperty
    public String city;
    @JsonProperty
    public String state;
    @JsonProperty
    public String county;
    @JsonProperty
    public String town;
    @JsonProperty
    public String village;
    @JsonProperty
    public String hamlet;
    @JsonProperty("house_number")
    public String houseNumber;
    @JsonProperty
    public String postcode;

    // Possible Street names TODO: Not sure what tags can be returned here
    @JsonProperty
    public String road;
    @JsonProperty
    public String pedestrian;
    @JsonProperty
    public String path;
    @JsonProperty
    public String footway;
    @JsonProperty
    public String construction;
    @JsonProperty
    public String cycleway;

    public String getGHCity() {
        if (city != null) {
            return city;
        }
        if (town != null) {
            return town;
        }
        if (village != null) {
            return village;
        }
        return hamlet;
    }

    public String getStreetName() {
        if (road != null) {
            return road;
        }
        if (pedestrian != null) {
            return pedestrian;
        }
        if (path != null) {
            return path;
        }
        if (footway != null) {
            return footway;
        }
        if (cycleway != null) {
            return cycleway;
        }
        return construction;
    }

}
