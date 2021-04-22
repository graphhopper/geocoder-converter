package com.graphhopper.converter.core;

import com.graphhopper.converter.api.*;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

/**
 * @author Robin Boldt, David Masclet, Xuejing Dong
 */
public class Converter {

    public static GHEntry convertFromNetToolKitAddress(NetToolKitAddressEntry ntkEntry) {
        GHEntry rsp = new GHEntry(null, null, ntkEntry.getLat(),
                ntkEntry.getLng(), ntkEntry.getAddress(), null, null,
                ntkEntry.getCountry(), null, ntkEntry.getCity(),
                ntkEntry.getState(), null, ntkEntry.getCounty(), ntkEntry.getStreet(),
                ntkEntry.getHouseNumber(), ntkEntry.getPostalCode(), null);
        return rsp;
    }

    public static Response convertFromNetToolKitList(
            List<NetToolKitAddressEntry> netToolKitEntries, Status status) {
        if (netToolKitEntries == null) {
            if (status == null) {
                status = new Status(500, "");
            }
            return createResponse(new GHResponse(), status);
        } else {
            GHResponse ghResponse = new GHResponse(netToolKitEntries.size());
            ghResponse.addCopyright("GraphHopper")
                    .addCopyright("NetToolKit");
            for (NetToolKitAddressEntry entry : netToolKitEntries) {
                ghResponse.add(convertFromNetToolKitAddress(entry));
                ghResponse.addCopyright(entry.getProvider());
            }
            return createResponse(ghResponse, status);
        }
    }

    public static GHEntry convertFromGisgraphyAddress(GisgraphyAddressEntry gisgraphyEntry) {
        GHEntry rsp = new GHEntry(null, null, gisgraphyEntry.getLat(),
                gisgraphyEntry.getLng(), gisgraphyEntry.getDisplayName(), null, null,
                gisgraphyEntry.getCountry(), null, gisgraphyEntry.getCity(),
                gisgraphyEntry.getState(), null, null, gisgraphyEntry.getStreetName(),
                gisgraphyEntry.getHouseNumber(), gisgraphyEntry.getZipCode(), null);
        return rsp;
    }

    public static GHEntry convertFromGisgraphySearch(GisgraphySearchEntry gisgraphyEntry) {
        GHEntry rsp = new GHEntry(null, null, gisgraphyEntry.getLat(),
                gisgraphyEntry.getLng(), gisgraphyEntry.getLabel(), null, null,
                gisgraphyEntry.getCountry(), null, gisgraphyEntry.getIsIn(),
                gisgraphyEntry.getAdm1Name(), null, null, gisgraphyEntry.getName(),
                gisgraphyEntry.getHouseNumber(), gisgraphyEntry.getZipCode(), null);
        return rsp;
    }

    public static Response convertFromGisgraphyList(
            List<GisgraphyAddressEntry> gisgraphyEntries, Status status) {
        if (gisgraphyEntries == null) {
            if (status == null) {
                status = new Status(500, "");
            }
            return createResponse(new GHResponse(), status);
        } else {
            GHResponse ghResponse = new GHResponse(gisgraphyEntries.size());
            for (GisgraphyAddressEntry entry : gisgraphyEntries) {
                ghResponse.add(convertFromGisgraphyAddress(entry));
            }

            ghResponse.addCopyright("OpenStreetMap")
                    .addCopyright("GraphHopper")
                    .addCopyright("Gisgraphy");
            return createResponse(ghResponse, status);
        }
    }

    public static Response convertFromGisgraphySearchList(
            GisgraphySearchResponse response, Status status) {
        if (response == null) {
            if (status == null) {
                status = new Status(500, "");
            }
            return createResponse(new GHResponse(), status);
        } else {
            GHResponse ghResponse = new GHResponse(response.getDocs().size());
            for (GisgraphySearchEntry entry : response.getDocs()) {
                ghResponse.add(convertFromGisgraphySearch(entry));
            }

            ghResponse.addCopyright("OpenStreetMap")
                    .addCopyright("GraphHopper")
                    .addCopyright("Gisgraphy");
            return createResponse(ghResponse, status);
        }
    }

    public static GHEntry convertFromNominatim(NominatimEntry response) {
        GHEntry rsp = new GHEntry(response.getOsmId(), response.getGHOsmType(), response.getLat(), response.getLon(),
                response.getDisplayName(), null, response.getType(), response.getAddress(), response.getExtent());
        return rsp;
    }

    public static Response convertFromNominatimList(List<NominatimEntry> nominatimResponses, Status status, String locale) {
        GHResponse ghResponse = new GHResponse(nominatimResponses.size());
        for (NominatimEntry nominatimResponse : nominatimResponses) {
            ghResponse.add(convertFromNominatim(nominatimResponse));
        }

        ghResponse.addCopyright("OpenStreetMap")
                .addCopyright("GraphHopper");
        if (!locale.isEmpty()) {
            ghResponse.setLocale(locale);
        }
        return createResponse(ghResponse, status);
    }

    public static GHEntry convertFromOpenCageData(OpenCageDataEntry response) {
        Long osmId = -1L;
        String type = "N";

        // extract OSM id and type from OSM annotation
        if (response.getAnnotations() != null && response.getAnnotations().osm != null && response.getAnnotations().osm.editUrl != null) {
            String url = response.getAnnotations().osm.editUrl;
            // skip nodeId;
            int index = url.indexOf("?");
            int index2 = url.indexOf("#");
            if (index > 0 && index2 > 0) {
                char typeChar = url.charAt(index + 1);
                if (typeChar == 'w') {
                    try {
                        // ?way=
                        osmId = Long.parseLong(url.substring(index + 5, index2));
                        type = "W";
                    } catch (Exception ex) {
                    }
                } else if (typeChar == 'r') {
                    try {
                        // ?relation=
                        osmId = Long.parseLong(url.substring(index + 10, index2));
                        type = "R";
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

        GHEntry rsp = new GHEntry(osmId, type, response.getGeometry().lat, response.getGeometry().lng,
                response.getFormatted(), null, response.getComponents().type, response.getComponents(), response.getExtent());

        return rsp;
    }

    public static Response convertFromOpenCageData(OpenCageDataResponse ocdRsp, Status status, String locale) {
        List<OpenCageDataEntry> ocdEntries = ocdRsp.results;
        GHResponse ghResponse = new GHResponse(ocdEntries.size());
        for (OpenCageDataEntry ocdResponse : ocdEntries) {
            ghResponse.add(convertFromOpenCageData(ocdResponse));
        }

        ghResponse.addCopyright("OpenCageData")
                .addCopyright("OpenStreetMap")
                .addCopyright("GraphHopper");
        if (!locale.isEmpty()) {
            ghResponse.setLocale(locale);
        }
        return createResponse(ghResponse, status);
    }

    public static GHEntry convertFromPelias(PeliasEntry response) {
        GHEntry rsp = new GHEntry(response.properties.getOsmId(), response.properties.getGHOsmType(),
                response.geometry.getLat(), response.geometry.getLon(), response.properties.name, null, null,
                response.properties.country, null, response.properties.locality, response.properties.region,
                response.properties.macrocounty, response.properties.county, response.properties.street,
                response.properties.housenumber, response.properties.postalcode, response.getExtent());
        return rsp;
    }

    public static Response convertFromPelias(PeliasResponse peliasRsp, Status status, String locale) {
        List<PeliasEntry> peliasEntries = peliasRsp.features;
        GHResponse ghResponse = new GHResponse(peliasEntries.size());
        for (PeliasEntry peliasEntry : peliasEntries) {
            ghResponse.add(convertFromPelias(peliasEntry));
        }

        ghResponse.addCopyright("geocode.earth")
                .addCopyright("geocode.earth/guidelines")
                .addCopyright("OpenStreetMap")
                .addCopyright("GraphHopper");
        if (!locale.isEmpty()) {
            ghResponse.setLocale(locale);
        }
        return createResponse(ghResponse, status);
    }

    public static GHEntry convertFromPhoton(PhotonEntry response) {
        GHEntry rsp = new GHEntry(response.properties.osmId, response.properties.osmType,
                response.geometry.getLat(), response.geometry.getLon(), response.properties.name,
                response.properties.osmKey, response.properties.osmValue, response.properties.country, response.properties.countrycode,
                response.properties.city, response.properties.state, null, null, response.properties.street, response.properties.housenumber,
                response.properties.postcode, response.properties.getExtent());
        if (rsp.getName() == null || rsp.getName().isEmpty()) {
            for (String tmp : new String[]{rsp.getStreet(), rsp.getCity(), rsp.getState(), rsp.getCountry()}) {
                if (tmp != null && !tmp.isEmpty()) {
                    rsp.setName(tmp);
                    break;
                }
            }
        }

        return rsp;
    }

    public static Response convertFromPhoton(PhotonResponse photonResponse, Status status, String locale) {
        List<PhotonEntry> photonEntries = photonResponse.features;
        GHResponse ghResponse = new GHResponse(photonEntries.size());
        for (PhotonEntry photonEntry : photonEntries) {
            ghResponse.add(convertFromPhoton(photonEntry));
        }

        ghResponse.addCopyright("OpenStreetMap")
                .addCopyright("GraphHopper");
        if (!locale.isEmpty()) {
            ghResponse.setLocale(locale);
        }
        return createResponse(ghResponse, status);
    }

    public static Response createResponse(GHResponse ghResponse, Status status) {
        if (status.code == 200) {

            return Response.status(Response.Status.OK).
                    entity(ghResponse).
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
