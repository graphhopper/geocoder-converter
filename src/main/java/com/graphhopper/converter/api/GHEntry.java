package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * @author Robin Boldt
 */
// ignore serialization of fields that are null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GHEntry {

    private Long osmId;
    private String osmType;

    private Point point;
    @JsonUnwrapped
    private Extent extent;

    private String name;
    private String country;
    private String city;
    private String state;
    private String street;
    private String houseNumber;
    private String postcode;
    private String osmValue;

    public GHEntry(Long osmId, String type, double lat, double lng, String name, String osmValue, String country, String city, String state, String street, String houseNumber, String postcode, Extent extent) {
        this.osmId = osmId;
        this.osmType = type;
        this.point = new Point(lat, lng);
        this.name = name;
        this.country = country;
        this.city = city;
        this.state = state;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.osmValue = osmValue;
        this.extent = extent;
    }

    public GHEntry(Long osmId, String type, double lat, double lng, String name, String osmValue, AbstractAddress address, Extent extent) {
        this(osmId, type, lat, lng, name, osmValue, address.country, address.getGHCity(), address.state, address.getStreetName(), address.houseNumber, address.postcode, extent);
    }

    public GHEntry(){}

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    public String getCountry() {
        return country;
    }

    @JsonProperty
    public void setCountry(String country) {
        this.country = country;
    }

    @JsonProperty
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty
    public String getState() {
        return state;
    }

    @JsonProperty
    public String getCity() {
        return city;
    }

    @JsonProperty
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty
    public Point getPoint() {
        return point;
    }

    @JsonProperty
    public void setPoint(Point point) {
        this.point = point;
    }

    @JsonProperty("osm_id")
    public Long getOsmId() {
        return osmId;
    }

    @JsonProperty("osm_id")
    public void setOsmId(Long osmId) {
        this.osmId = osmId;
    }

    @JsonProperty("osm_type")
    public String getOsmType() {
        return osmType;
    }

    @JsonProperty("osm_type")
    public void setOsmType(String type) {
        this.osmType = type;
    }

    @JsonProperty
    public String getStreet() {
        return street;
    }

    @JsonProperty
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("house_number")
    public String getHouseNumber() {
        return houseNumber;
    }

    @JsonProperty("house_number")
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @JsonProperty
    public String getPostcode() {
        return postcode;
    }

    @JsonProperty
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @JsonProperty("osm_value")
    public String getOsmValue() {
        return osmValue;
    }

    @JsonProperty("osm_value")
    public void setOsmValue(String osmValue) {
        this.osmValue = osmValue;
    }

    @JsonProperty
    public Extent getExtent() {
        return this.extent;
    }

    @JsonProperty
    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public class Point {

        private double lat;
        private double lng;

        public Point(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public Point(){}

        @JsonProperty
        public double getLat() {
            return lat;
        }

        @JsonProperty
        public void setLat(double lat) {
            this.lat = lat;
        }

        @JsonProperty
        public double getLng() {
            return lng;
        }

        @JsonProperty
        public void setLng(double lng) {
            this.lng = lng;
        }
    }

}
