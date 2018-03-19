package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphyAddressEntry {

    private long id;

    private double lat;

    private double lng;

    private long sourceId;

    private String countryCode;
    
    private String country;

    private String city;

    private String state;

    private String zipCode;

    private String houseNumber;

    private String name;

    private String streetName;

    private String formatedPostal;

    private String geocodingLevel;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getSourceId() {
        return sourceId;
    }

    public void setSourceId(long sourceId) {
        this.sourceId = sourceId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getFormatedPostal() {
        return formatedPostal;
    }

    public void setFormatedPostal(String formatedPostal) {
        this.formatedPostal = formatedPostal;
    }

    public String getDisplayName() {
        // NONE,HOUSE_NUMBER,STREET,CITY,STATE,COUNTRY,CITY_SUBDIVISION
        if (geocodingLevel != null && (geocodingLevel.equals("HOUSE_NUMBER")
                || geocodingLevel.equals("STREET"))) {
            // for an address, the name field can have be a place name (i.e : name of a retaurant)
            return getStreetName();
        } else {
            return getName();
        }
    }

    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country){
        this.country = country;
    }

    public String getGeocodingLevel() {
        return geocodingLevel;
    }

    public void setGeocodingLevel(String geocodingLevel) {
        this.geocodingLevel = geocodingLevel;
    }

}
