package com.graphhopper.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Robin Boldt, David Masclet, Xuejing Dong
 */
public class ConverterConfiguration extends Configuration {

    @NotEmpty
    private String nominatimURL = "https://nominatim.openstreetmap.org/search";
    @NotEmpty
    private String nominatimReverseURL = "https://nominatim.openstreetmap.org/reverse";
    private String nominatimEmail = "";

    private String openCageDataURL = "https://api.opencagedata.com/geocode/v1/json";
    private String openCageDataKey = "";

    private String peliasURL = "";
    private String peliasKey = "";

    private String gisgraphyGeocodingURL = "https://services.gisgraphy.com/geocoding/";
    private String gisgraphyReverseGeocodingURL = "https://services.gisgraphy.com/reversegeocoding/";
    private String gisgraphySearchURL = "https://services.gisgraphy.com/fulltext/";
    private String gisgraphyAPIKey = "";

    private String netToolKitGeocodingURL = "https://api.nettoolkit.com/v1/geo/geocodes?";
    private String netToolKitReverseGeocodingURL = "https://api.nettoolkit.com/v1/geo/reverse-geocodes?";
    private int netToolKitTimeout = 10_000;
    private String netToolKitKey = "";

    @NotEmpty
    private String photonURL = "https://photon.komoot.io/api/";
    @NotEmpty
    private String photonReverseURL = "https://photon.komoot.io/reverse/";
    private Set<String> photonSupportedLanguages = new HashSet<>();

    @Valid
    @NotNull
    private final JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

    private boolean healthCheck = true;
    private boolean nominatim = true;
    private boolean gisgraphy = true;
    private boolean opencagedata = true;
    private boolean pelias = true;
    private boolean nettoolkit = true;
    private boolean photon = true;

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
    public void setPelias(boolean pelias) {
        this.pelias = pelias;
    }

    @JsonProperty
    public boolean isPelias() {
        return this.pelias;
    }

    @JsonProperty
    public String getPeliasURL() {
        return peliasURL;
    }

    @JsonProperty
    public void setPeliasURL(String url) {
        this.peliasURL = url;
    }

    @JsonProperty
    public String getPeliasKey() {
        return peliasKey;
    }

    @JsonProperty
    public void setPeliasKey(String key) {
        this.peliasKey = key;
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

    @JsonProperty
    public String getNetToolKitGeocodingURL() {
        return netToolKitGeocodingURL;
    }

    @JsonProperty
    public void setNetToolKitGeocodingURL(String netToolKitGeocodingURL) {
        this.netToolKitGeocodingURL = netToolKitGeocodingURL;
    }

    @JsonProperty
    public String getNetToolKitReverseGeocodingURL() {
        return netToolKitReverseGeocodingURL;
    }

    @JsonProperty
    public void setNetToolKitReverseGeocodingURL(String netToolKitReverseGeocodingURL) {
        this.netToolKitReverseGeocodingURL = netToolKitReverseGeocodingURL;
    }

    @JsonProperty
    public String getNetToolKitKey() {
        return netToolKitKey;
    }

    @JsonProperty
    public void setNetToolKitKey(String netToolKitKey) {
        this.netToolKitKey = netToolKitKey;
    }

    @JsonProperty
    public int getNetToolKitTimeout() {
        return netToolKitTimeout;
    }

    @JsonProperty
    public void setNetToolKitTimeout(int netToolKitTimeout) {
        this.netToolKitTimeout = netToolKitTimeout;
    }

    @JsonProperty
    public boolean isNetToolKit() {
        return nettoolkit;
    }

    @JsonProperty
    public void setNetToolKit(boolean nettoolkit) {
        this.nettoolkit = nettoolkit;
    }

    @JsonProperty
    public String getPhotonURL() {
        return photonURL;
    }

    @JsonProperty
    public void setPhotonURL(String photonURL) {
        this.photonURL = photonURL;
    }

    @JsonProperty
    public String getPhotonReverseURL() {
        return photonReverseURL;
    }

    @JsonProperty
    public void setPhotonReverseURL(String photonReverseURL) {
        this.photonReverseURL = photonReverseURL;
    }

    @JsonProperty
    public void setPhotonSupportedLanguages(String supportedLanguages) {
        Set<String> set = new HashSet<>();
        for (String lang : supportedLanguages.split(",")) {
            set.add(lang.trim().toLowerCase(Locale.ROOT));
        }
        this.photonSupportedLanguages = set;
    }

    public Set<String> getPhotonSupportedLanguages() {
        return photonSupportedLanguages;
    }

    @JsonProperty
    public boolean isPhoton() {
        return photon;
    }

    @JsonProperty
    public void setPhoton(boolean photon) {
        this.photon = photon;
    }
}
