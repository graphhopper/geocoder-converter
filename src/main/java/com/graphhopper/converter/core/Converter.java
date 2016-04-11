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
        GHResponse rsp = new GHResponse(response.getOsmId(), response.getLat(), response.getLon(), response.getDisplayName(),
                response.getAddress().getCountry(), response.getAddress().getGHCity());
        rsp.addCopyright("OpenStreetMap").addCopyright("GraphHopper");
        return rsp;
    }

    public static Response convertFromNominatimList(List<NominatimEntry> nominatimResponses, Status status) {
        List<GHResponse> ghResponses = new ArrayList<>(nominatimResponses.size());
        for (NominatimEntry nominatimResponse : nominatimResponses) {
            ghResponses.add(convertFromNominatim(nominatimResponse));
        }

        return createResponse(ghResponses, status);
    }

    public static GHResponse convertFromOpenCageData(OpenCageDataEntry response) {
        GHResponse rsp = new GHResponse(1 /*TODO*/, response.getGeometry().lat, response.getGeometry().lng,
                response.getFormatted(), response.getComponents().country, response.getComponents().city);
        rsp.addCopyright("OpenCageData").addCopyright("OpenStreetMap").addCopyright("GraphHopper");
        return rsp;
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
