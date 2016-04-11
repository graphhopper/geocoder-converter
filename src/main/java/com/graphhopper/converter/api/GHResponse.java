package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Robin Boldt
 */
public class GHResponse {

    private Long osmId;

    private Point point;

    private String name;
    private String country;
    private String city;
    private List<String> copyrights = new ArrayList<String>(5);

    public GHResponse(long osmId, double lat, double lng, String name, String country, String city) {
        this.osmId = osmId;
        this.point = new Point(lat, lng);
        this.name = name;
        this.country = country;
        this.city = city;
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

    @JsonProperty("copyrights")
    public List<String> getCopyrights() {
        return copyrights;
    }

    public GHResponse addCopyright(String cr) {
        copyrights.add(cr);
        return this;
    }

    @JsonProperty("copyrights")
    public void setCopyrights(List<String> copyrights) {
        this.copyrights = copyrights;
    }

    public class Point {

        private double lat;
        private double lng;

        public Point(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
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
