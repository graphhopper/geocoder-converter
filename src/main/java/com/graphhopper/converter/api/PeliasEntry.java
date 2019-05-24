package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeliasEntry {

    public PeliasGeometry geometry;
    public PeliasProperties properties;

    public List<Double> bbox;

    public Extent getExtent(){
        if (this.bbox == null || this.bbox.size() < 4)
            return null;

        return new Extent(bbox.get(3), bbox.get(0), bbox.get(1), bbox.get(2));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeliasGeometry {

        public List<Double> coordinates;

        public Double getLat() {
            return ensureGeometryCapacity() ? this.coordinates.get(1) : null;
        }

        public Double getLon() {
            return ensureGeometryCapacity() ? this.coordinates.get(0) : null;
        }

        //TODO This check might be dangerous if pelias returns other geometries than points?
        private boolean ensureGeometryCapacity() {
            return this.coordinates.size() == 2;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PeliasProperties {

        public String source;
        public String source_id;

        public String name;
        public String country;
        public String locality;
        public String region;
        public String macrocounty;
        public String county;

        public String postalcode;
        public String street;
        public String housenumber;

        public String getGHOsmType() {
            return getOSMInfo(false);
        }

        public Long getOsmId() {
            String osmId = getOSMInfo(true);
            if (osmId == null)
                return null;
            return Long.parseLong(osmId);
        }

        private String getOSMInfo(boolean getId) {
            if (source == null || !source.equals("openstreetmap") || source_id == null) {
                return null;
            }

            // source_id looks like "node/4153840591"
            String[] sourceArr = source_id.split("/");

            if (sourceArr.length != 2)
                return null;

            // convert node into N, way into W and relation into R
            if(getId)
                return sourceArr[1];
            else
                return sourceArr[0].toUpperCase().substring(0, 1);
        }

    }

}

