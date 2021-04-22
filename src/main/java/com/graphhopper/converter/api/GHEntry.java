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
    private String countrycode;
    private String city;
    private String state;
    private String stateDistrict;
    private String county;
    private String street;
    private String houseNumber;
    private String postcode;
    private String osmKey;
    private String osmValue;

    public GHEntry(Long osmId, String osmType, double lat, double lng, String name, String osmKey, String osmValue,
                   String country, String countrycode, String city, String state, String stateDistrict, String county, String street,
                   String houseNumber, String postcode, Extent extent) {
        this.osmId = osmId;
        this.osmType = osmType;
        this.osmKey = osmKey;
        this.osmValue = osmValue;
        this.point = new Point(lat, lng);
        this.name = name;
        this.country = country;
        this.countrycode = countrycode;
        this.city = city;
        this.state = state;
        this.stateDistrict = stateDistrict;
        this.county = county;
        this.street = street;
        this.houseNumber = houseNumber;
        this.postcode = postcode;
        this.extent = extent;
    }

    public GHEntry(Long osmId, String osmType, double lat, double lng, String name, String osmKey, String osmValue,
                   AbstractAddress address, Extent extent) {
        this(osmId, osmType, lat, lng, name, osmKey, osmValue, address.country, null, address.getGHCity(), address.state,
                address.stateDistrict, address.county, address.getStreetName(), address.houseNumber, address.postcode, extent);
    }

    public GHEntry() {
    }

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
    public void setStateDistrict(String stateDistrict) {
        this.stateDistrict = stateDistrict;
    }

    @JsonProperty
    public String getStateDistrict() {
        return stateDistrict;
    }

    @JsonProperty
    public void setCounty(String county) {
        this.county = county;
    }

    @JsonProperty
    public String getCounty() {
        return county;
    }

    @JsonProperty
    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    @JsonProperty
    public String getCountrycode() {
        return countrycode;
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

    @JsonProperty("housenumber")
    public String getHouseNumber() {
        return houseNumber;
    }

    // TODO this getter will be deleted soon (Aug 2019) and is only available due to the breaking API change of renaming house_number to housenumber
    @JsonProperty("house_number")
    public String getHouse_Number() {
        return houseNumber;
    }

    @JsonProperty("housenumber")
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

    @JsonProperty("osm_key")
    public String getOsmKey() {
        return osmKey;
    }

    @JsonProperty("osm_key")
    public void setOsmKey(String osmKey) {
        this.osmKey = osmKey;
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

        public Point() {
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
        public double getLng() {
            return lng;
        }

        @JsonProperty
        public void setLng(double lng) {
            this.lng = lng;
        }
    }

}
