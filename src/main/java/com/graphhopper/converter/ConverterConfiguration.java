package com.graphhopper.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author Robin Boldt, David Masclet
 */
public class ConverterConfiguration extends Configuration {

    @NotEmpty
    private String nominatimURL = "https://nominatim.openstreetmap.org/search/";
    @NotEmpty
    private String nominatimReverseURL = "https://nominatim.openstreetmap.org/reverse/";
    private String nominatimEmail = "";

    private String openCageDataURL = "https://api.opencagedata.com/geocode/v1/json";
    private String openCageDataKey = "";

    private String gisgraphyGeocodingURL = "https://services.gisgraphy.com/geocoding/";
    private String gisgraphyReverseGeocodingURL ="https://services.gisgraphy.com/reversegeocoding/";
    private String gisgraphySearchURL =  "https://services.gisgraphy.com/fulltext/";
    private String gisgraphyAPIKey="";

    @Valid
    @NotNull
    private final JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    private boolean healthCheck = true;
    private boolean nominatim = true;
    private boolean gisgraphy = true;
    private boolean opencagedata;

    private String ipBlackList = "";
    private String ipWhiteList = "";

    @JsonProperty
    public void setNominatim(boolean nom) {
        nominatim = nom;
    }

    @JsonProperty
    public boolean isNominatim() {
        return nominatim;
    }

    @JsonProperty
    public String getNominatimURL() {
        return nominatimURL;
    }

    @JsonProperty
    public void setNominatimURL(String url) {
        this.nominatimURL = url;
    }

    @JsonProperty
    public String getNominatimEmail() {
        return nominatimEmail;
    }

    @JsonProperty
    public void setNominatimEmail(String email) {
        this.nominatimEmail = email;
    }

    @JsonProperty
    public void setOpenCageData(boolean ocd) {
        opencagedata = ocd;
    }

    @JsonProperty
    public boolean isOpenCageData() {
        return opencagedata;
    }

    @JsonProperty
    public String getOpenCageDataURL() {
        return openCageDataURL;
    }

    @JsonProperty
    public void setOpenCageDataURL(String url) {
        this.openCageDataURL = url;
    }

    @JsonProperty
    public String getOpenCageDataKey() {
        return openCageDataKey;
    }

    @JsonProperty
    public void setOpenCageDataKey(String key) {
        this.openCageDataKey = key;
    }

    @JsonProperty
    public boolean isHealthCheck() {
        return healthCheck;
    }

    @JsonProperty
    public void setHealthCheck(boolean hc) {
        this.healthCheck = hc;
    }

    @JsonProperty("jerseyClient")
    public JerseyClientConfiguration getJerseyClientConfiguration() {
        return jerseyClient;
    }

    @JsonProperty(value = "ipBlackList")
    public String getIPBlackList() {
        return ipBlackList;
    }

    @JsonProperty(value = "ipBlackList")
    public void setIPBlackList(String ipBlackList) {
        this.ipBlackList = ipBlackList;
    }

    @JsonProperty(value = "ipWhiteList")
    public String getIPWhiteList() {
        return ipWhiteList;
    }

    @JsonProperty(value = "ipWhiteList")
    public void setIPWhiteList(String ipWhiteList) {
        this.ipWhiteList = ipWhiteList;
    }

    @JsonProperty
    public String getNominatimReverseURL() {
        return nominatimReverseURL;
    }

    @JsonProperty
    public void setNominatimReverseURL(String nominatimReverseURL) {
        this.nominatimReverseURL = nominatimReverseURL;
    }

    @JsonProperty
    public String getGisgraphyGeocodingURL() {
        return gisgraphyGeocodingURL;
    }

    @JsonProperty
    public void setGisgraphyGeocodingURL(String gisgraphyGeocodingURL) {
        this.gisgraphyGeocodingURL = gisgraphyGeocodingURL;
    }

    @JsonProperty
    public String getGisgraphyReverseGeocodingURL() {
        return gisgraphyReverseGeocodingURL;
    }

    @JsonProperty
    public void setGisgraphyReverseGeocodingURL(String gisgraphyReverseGeocodingURL) {
        this.gisgraphyReverseGeocodingURL = gisgraphyReverseGeocodingURL;
    }

    @JsonProperty
    public String getGisgraphySearchURL() {
        return gisgraphySearchURL;
    }

    @JsonProperty
    public void setGisgraphySearchURL(String gisgraphySearchURL) {
        this.gisgraphySearchURL = gisgraphySearchURL;
    }

    @JsonProperty
    public String getGisgraphyAPIKey() {
        return gisgraphyAPIKey;
    }

    @JsonProperty
    public void setGisgraphyAPIKey(String gisgraphyAPIKey) {
        this.gisgraphyAPIKey = gisgraphyAPIKey;
    }

    @JsonProperty
    public boolean isGisgraphy() {
        return gisgraphy;
    }

    @JsonProperty
    public void setGisgraphy(boolean gisgraphy) {
        this.gisgraphy = gisgraphy;
    }
}
