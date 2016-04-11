package com.graphhopper.converter.core;

import com.graphhopper.converter.api.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Robin Boldt
 */
public class Converter {

    public static GHResponse convertFromNominatim(NominatimEntry response) {
        return new GHResponse(response.getOsmId(), response.getGHOsmType(), response.getLat(), response.getLon(), response.getDisplayName(), response.getAddress().getCountry(), response.getAddress().getGHCity());
    }

    public static Response convertFromNominatimList(List<NominatimEntry> nominatimResponses, Status status) {
        List<GHResponse> ghResponses = new ArrayList<>(nominatimResponses.size());
        for (NominatimEntry nominatimResponse : nominatimResponses) {
            ghResponses.add(convertFromNominatim(nominatimResponse));
        }

        return createResponse(ghResponses, status);
    }

    public static GHResponse convertFromOpenCageData(OpenCageDataEntry response) {
        Long osmId = -1L;
        String type = "N";

        // extract OSM id and type from OSM annotation
        if (response.getAnnotations() != null && response.getAnnotations().osm != null && response.getAnnotations().osm.editUrl != null) {
            String url = response.getAnnotations().osm.editUrl;
            // skip nodeId;
            int index = url.indexOf("?");
            int index2 = url.indexOf("#");
            if (index > 0 && index2 > 0) {
                if (url.charAt(index + 1) == 'w') {
                    try {
                        // ?way=
                        osmId = Long.parseLong(url.substring(index + 5, index2));
                        type = "W";
                    } catch (Exception ex) {
                    }
                } else {
                    try {
                        // ?node=
                        osmId = Long.parseLong(url.substring(index + 6, index2));
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return new GHResponse(osmId, type, response.getGeometry().lat, response.getGeometry().lng,
                response.getFormatted(), response.getComponents().country, response.getComponents().getGHCity());
    }

    public static Response convertFromOpenCageData(OpenCageDataResponse ocdRsp, Status status) {
        List<OpenCageDataEntry> ocdEntries = ocdRsp.results;
        List<GHResponse> ghResponses = new ArrayList<>(ocdEntries.size());
        for (OpenCageDataEntry ocdResponse : ocdEntries) {
            ghResponses.add(convertFromOpenCageData(ocdResponse));
        }

        return createResponse(ghResponses, status);
    }

    public static Response createResponse(List<GHResponse> ghResponses, Status status) {
        if (status.code == 200) {

            return Response.status(Response.Status.OK).
                    entity(ghResponses).
                    build();
        } else {

            HashMap map = new HashMap();
            map.put("message", status.message);
            return Response.status(status.code).
                    entity(map).
                    build();
        }
    }
}
