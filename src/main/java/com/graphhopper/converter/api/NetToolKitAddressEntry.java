package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Xuejing Dong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NetToolKitAddressEntry {

    private double lat;
    private double lng;
    private String address;
    private String country;
    private String city;
    private String county;
    private String state;
    private String stateCode;
    private String postalCode;
    private String houseNumber;
    private String street;
    private String streetName;
    private String streetType;
    private String precision;
    private String provider;

    @JsonProperty("latitude")
    public double getLat() {
        return lat;
    }

    @JsonProperty("latitude")
    public void setLat(double lat) {
        this.lat = lat;
    }

    @JsonProperty("longitude")
    public double getLng() {
        return lng;
    }

    @JsonProperty("longitude")
    public void setLng(double lng) {
        this.lng = lng;
    }

    @JsonProperty
    public String getAddress() {
        return address;
    }

    @JsonProperty
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("state_code")
    public String getStateCode() {
        return stateCode;
    }

    @JsonProperty("state_code")
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
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
    public String getCounty() {
        return county;
    }

    @JsonProperty
    public void setCounty(String county) {
        this.county = county;
    }

    @JsonProperty
    public String getState() {
        return state;
    }

    @JsonProperty
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("postal_code")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("postal_code")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("house_number")
    public String getHouseNumber() {
        return houseNumber;
    }

    @JsonProperty("house_number")
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    @JsonProperty("street_name")
    public String getStreetName() {
        return streetName;
    }

    @JsonProperty("street_name")
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @JsonProperty
    public String getStreet() {
        return street;
    }

    @JsonProperty
    public void setStreet(String street) {
        this.street = street;
    }

    @JsonProperty("street_type")
    public String getStreetType() {
        return streetType;
    }

    @JsonProperty("street_type")
    public void setStreetType(String streetType) {
        this.streetType = streetType;
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
    public String getPrecision() {
        return precision;
    }

    @JsonProperty
    public void setPrecision(String precision) {
        this.precision = precision;
    }

    @JsonProperty
    public String getProvider() {
        return provider;
    }

    @JsonProperty
    public void setProvider(String provider) {
        this.provider = provider;
    }
}
