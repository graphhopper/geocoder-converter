package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimEntry {

    private long osmId;
    private String osmType;

    private double lat;
    private double lon;

    private String displayName;
    private String classString;

    // This is not the osmType, but for example "restaurant" or "tertiary"
    private String type;

    private List<Double> boundingbox;

    public Address address;

    public NominatimEntry(long osmId, String type, double lat, double lon, String displayName, String country, String city, List<Double> boundingbox) {
        this.osmId = osmId;
        this.osmType = type;
        this.lat = lat;
        this.lon = lon;
        this.displayName = displayName;

        this.address = new Address();
        address.country = country;
        address.city = city;
        this.boundingbox = boundingbox;
    }

    public NominatimEntry() {
        this.address = new Address();
    }

    @JsonProperty("osm_id")
    public long getOsmId() {
        return osmId;
    }

    @JsonProperty("osm_id")
    public void setOsmId(long osmId) {
        this.osmId = osmId;
    }

    @JsonProperty("osm_type")
    public String getOsmType() {
        return osmType;
    }

    public String getGHOsmType() {
        if (osmType == null) {
            return null;
        }

        // convert node into N, way into W and relation into R
        return osmType.toUpperCase().substring(0, 1);
    }

    @JsonProperty("osm_type")
    public void setOsmType(String type) {
        this.osmType = type;
    }

    @JsonProperty
    public double getLat() {
        return lat;
    }

    @JsonProperty
    public void setLat(double lat) {
        this.lat = lat;
    }

    @JsonProperty
    public double getLon() {
        return lon;
    }

    @JsonProperty
    public void setLon(double lon) {
        this.lon = lon;
    }

    @JsonProperty("display_name")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("display_name")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonProperty("class")
    public String getClassString() {
        return classString;
    }

    @JsonProperty("class")
    public void setClassString(String classString) {
        this.classString = classString;
    }

    @JsonProperty
    public String getType() {
        return type;
    }

    @JsonProperty
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty
    public List<Double> getBoundingbox() {
        return this.boundingbox;
    }

    @JsonProperty
    public void setBoundingbox(List<Double> boundingbox) {
        this.boundingbox = boundingbox;
    }

    public Extent getExtent() {
        if (this.boundingbox == null || this.boundingbox.size() < 4)
            return null;
        // the nominatim BBox is in minLat, maxLat, minLon, maxLon
        return new Extent(boundingbox.get(0), boundingbox.get(2), boundingbox.get(1), boundingbox.get(3));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Address extends AbstractAddress {

        public Address() {
        }


    }
}
