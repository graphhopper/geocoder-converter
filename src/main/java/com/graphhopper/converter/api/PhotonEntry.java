package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotonEntry {

    @JsonProperty("geometry")
    public GeojsonPoint geometry;

    @JsonProperty("type")
    public String type;

    @JsonProperty("properties")
    public PhotonProperties properties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PhotonProperties {

        @JsonProperty("osm_id")
        public long osmId;

        @JsonProperty("osm_type")
        public String osmType;

        @JsonProperty("name")
        public String name;

        @JsonProperty("osm_key")
        public String osmKey;

        @JsonProperty("osm_value")
        public String osmValue;

        @JsonProperty("country")
        public String country;

        @JsonProperty("state")
        public String state;

        @JsonProperty("city")
        public String city;

        @JsonProperty("street")
        public String street;

        @JsonProperty("housenumber")
        public String housenumber;

        @JsonProperty("postcode")
        public String postcode;

        @JsonProperty("extent")
        List<Double> extent;

        public Extent getExtent() {
            if (this.extent == null || this.extent.size() != 4)
                return null;

            return new Extent(extent.get(3), extent.get(0), extent.get(1), extent.get(2));
        }
    }

}
