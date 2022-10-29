package com.graphhopper.converter.api;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author David Masclet
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GisgraphySearchEntry {

    @JsonProperty("feature_id")
    private long featureId;
    private double lat;
    private double lng;
    private String name;

    @JsonProperty("country_code")
    private String countryCode;
    
    private String country;

    @JsonProperty("is_in")
    private String isIn;

    @JsonProperty("is_in_place")
    private String isInPlace;

    @JsonProperty("is_in_zip")
    private List<String> isInZip;

    @JsonProperty("adm1_name")
    private String adm1Name;

    @JsonProperty("zip_code")
    private List<String> zipCodes;

    @JsonProperty("house_numbers")
    private List<String> houseNumbers = new ArrayList<String>();

    public List<String> getHouseNumbers() {
        return houseNumbers;
    }

    public void setHouseNumbers(List<String> houseNumbers) {
        this.houseNumbers = houseNumbers;
    }

    @JsonProperty("house_number")
    private String houseNumber;



    @JsonProperty("label")
    private String label;


    public long getFeatureId() {
        return featureId;
    }

    public void setFeatureId(long featureId) {
        this.featureId = featureId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getIsIn() {
        return isIn;
    }

    public void setIsIn(String isIn) {
        this.isIn = isIn;
    }
    @JsonProperty("is_in_place")
    public String getIsInPlace() {
        return isInPlace;
    }

    @JsonProperty("is_in_place")
    public void setIsInPlace(String isInPlace) {
        this.isInPlace = isInPlace;
    }

    @JsonProperty("is_in_zip")
    public List<String> getIsInZip() {
        return isInZip;
    }

    @JsonProperty("is_in_zip")
    public void setIsInZip(List<String> isInZip) {
        this.isInZip = isInZip;
    }

    public String getAdm1Name() {
        return adm1Name;
    }

    public void setAdm1Name(String adm1Name) {
        this.adm1Name = adm1Name;
    }

    public List<String> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(List<String> zipCodes) {
        this.zipCodes = zipCodes;
    }

    public void setCountry(String country){
        this.country = country;
    }

    public String getCountry() {
       return country;
    }

    @JsonProperty("house_number")
    public String getHouseNumber() {
        return houseNumber;
    }

    @JsonProperty("house_number")
    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getZipCode() {
        if (isInZip!=null && isInZip.size()>0){
            return isInZip.get(0);
        }
        if (zipCodes!=null && zipCodes.size()>0){
            return zipCodes.get(0);
        }
        return null;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        String stringfeatureId = "GisgraphySearchEntry [featureId="+featureId+", ";
        builder.append(stringfeatureId);
        if (name != null) {
            String strName = "name=" + name + ", ";
            builder.append(strName);
        }
        if (isIn != null) {
            builder.append("isIn="+isIn+", ");
        }
        builder.append("]");
        return builder.toString();
    }

}
