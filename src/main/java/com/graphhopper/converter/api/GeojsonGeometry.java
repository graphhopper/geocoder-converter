package com.graphhopper.converter.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * A GeoJSON geometry. Photon returns a Point unless geometry=true is requested, then it returns the full geometry
 * (e.g. MultiPolygon of a city) instead.
 *
 * @author Robin Boldt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeojsonGeometry {

    @JsonProperty("coordinates")
    public JsonNode coordinates;

    @JsonProperty("type")
    public String type;

    public boolean isPoint() {
        return "Point".equals(type);
    }

    public Double getLat() {
        return getLatLon()[0];
    }

    public Double getLon() {
        return getLatLon()[1];
    }

    public JsonNode toJson() {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("type", type);
        node.set("coordinates", coordinates);
        return node;
    }

    // [lat, lon]: the point itself or the centroid of the (largest) polygon, otherwise the mean of all vertices
    private double[] getLatLon() {
        if (coordinates == null) return new double[]{Double.NaN, Double.NaN};
        if (isPoint()) return new double[]{coordinates.get(1).asDouble(), coordinates.get(0).asDouble()};
        if ("Polygon".equals(type)) return ringCentroid(coordinates.get(0));
        if ("MultiPolygon".equals(type)) {
            double[] best = null;
            for (JsonNode polygon : coordinates) {
                double[] c = ringCentroid(polygon.get(0));
                if (best == null || c[2] > best[2]) best = c;
            }
            return best;
        }
        double[] sum = new double[3];
        sumVertices(coordinates, sum);
        return new double[]{sum[0] / sum[2], sum[1] / sum[2]};
    }

    // [lat, lon, area] via shoelace formula, area is in degrees^2 and only used for comparison
    private static double[] ringCentroid(JsonNode ring) {
        double area = 0, cx = 0, cy = 0;
        for (int i = 0; i + 1 < ring.size(); i++) {
            double x0 = ring.get(i).get(0).asDouble(), y0 = ring.get(i).get(1).asDouble();
            double x1 = ring.get(i + 1).get(0).asDouble(), y1 = ring.get(i + 1).get(1).asDouble();
            double cross = x0 * y1 - x1 * y0;
            area += cross;
            cx += (x0 + x1) * cross;
            cy += (y0 + y1) * cross;
        }
        if (Math.abs(area) < 1e-12) {
            double[] sum = new double[3];
            sumVertices(ring, sum);
            return new double[]{sum[0] / sum[2], sum[1] / sum[2], 0};
        }
        return new double[]{cy / (3 * area), cx / (3 * area), Math.abs(area) / 2};
    }

    // sum[0] += lat, sum[1] += lon, sum[2] += count
    private static void sumVertices(JsonNode node, double[] sum) {
        if (node.size() > 0 && node.get(0).isNumber()) {
            sum[0] += node.get(1).asDouble();
            sum[1] += node.get(0).asDouble();
            sum[2]++;
        } else {
            for (JsonNode child : node) sumVertices(child, sum);
        }
    }
}
