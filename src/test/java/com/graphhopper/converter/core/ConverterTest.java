package com.graphhopper.converter.core;

import com.graphhopper.converter.api.*;
import org.junit.Test;

import java.util.Arrays;

import static com.graphhopper.converter.api.OpenCageDataEntry.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Robin Boldt,David Masclet, Xuejing Dong
 */
public class ConverterTest {

    @Test
    public void testNominatimConvert() {
        // Build a Response
        NominatimEntry nominatimResponse = new NominatimEntry(1L, "node", 1, 1, "test", "de", "Berlin", Arrays.asList(new Double[]{52.3570365, 52.6770365, 13.2288599, 13.5488599}));
        assertEquals("N", nominatimResponse.getGHOsmType());

        GHEntry ghResponse = Converter.convertFromNominatim(nominatimResponse);

        assertEquals(1L, (long) ghResponse.getOsmId());
        assertEquals("N", ghResponse.getOsmType());
        assertEquals(1, ghResponse.getPoint().getLat(), 0.001);
        assertEquals(1, ghResponse.getPoint().getLng(), 0.001);
        assertEquals("test", ghResponse.getName());
        assertEquals("de", ghResponse.getCountry());
        assertEquals("Berlin", ghResponse.getCity());
        Extent extent = ghResponse.getExtent();
        assertEquals(13.2288599, extent.getExtent().get(0), .000001);
        assertEquals(52.3570365, extent.getExtent().get(1), .000001);
        assertEquals(13.5488599, extent.getExtent().get(2), .000001);
        assertEquals(52.6770365, extent.getExtent().get(3), .000001);
    }

    @Test
    public void testNominatimConvertCityFallback() {
        // Build a Response
        NominatimEntry nominatimResponse = new NominatimEntry();
        nominatimResponse.setOsmId(1L);
        nominatimResponse.setDisplayName("test");
        nominatimResponse.setLat(1);
        nominatimResponse.setLon(1);
        nominatimResponse.getAddress().town = "townie";
        nominatimResponse.getAddress().country = "gb";
        GHEntry ghResponse = Converter.convertFromNominatim(nominatimResponse);

        assertEquals(1L, (long) ghResponse.getOsmId());
        assertEquals(1, ghResponse.getPoint().getLat(), 0.001);
        assertEquals(1, ghResponse.getPoint().getLng(), 0.001);
        assertEquals("test", ghResponse.getName());
        assertEquals("gb", ghResponse.getCountry());
        assertEquals("townie", ghResponse.getCity());
    }

    @Test
    public void testNominatimStreetEntry() {
        // Build a Response
        NominatimEntry nominatimResponse = new NominatimEntry();
        nominatimResponse.setOsmId(1L);
        nominatimResponse.setDisplayName("test");
        nominatimResponse.setLat(1);
        nominatimResponse.setLon(1);
        nominatimResponse.setClassString("highway");
        nominatimResponse.getAddress().town = "townie";
        nominatimResponse.getAddress().country = "gb";
        nominatimResponse.getAddress().pedestrian = "secret way";
        GHEntry ghResponse = Converter.convertFromNominatim(nominatimResponse);

        assertEquals(1L, (long) ghResponse.getOsmId());
        assertEquals(1, ghResponse.getPoint().getLat(), 0.001);
        assertEquals(1, ghResponse.getPoint().getLng(), 0.001);
        assertEquals("test", ghResponse.getName());
        assertEquals("gb", ghResponse.getCountry());
        assertEquals("townie", ghResponse.getCity());
        assertEquals("secret way", ghResponse.getStreet());
    }

    @Test
    public void testOCDConvert() {
        OCDComponents components = new OCDComponents();
        components.type = "city";
        components.countryCode = "DE";
        components.country = "Germany";
        components.postcode = "10117";
        components.city = "Berlin";

        OCDAnnoations annoations = new OCDAnnoations();
        annoations.osm = new OCDAnnotationOSM();
        annoations.osm.editUrl = "https://www.openstreetmap.org/edit?node=240109189#map=17/52.51704/13.38886";
        annoations.osm.url = "https://www.openstreetmap.org/?mlat=52.51704&mlon=13.38886#map=17/52.51704/13.38886";

        OCDBounds bounds = new OCDBounds();
        bounds.northeast = new OCDGeometry();
        bounds.northeast.lat = 52.6770365;
        bounds.northeast.lng = 13.5488599;

        bounds.southwest = new OCDGeometry();
        bounds.southwest.lat = 52.3570365;
        bounds.southwest.lng = 13.2288599;

        OpenCageDataEntry entry = new OpenCageDataEntry(52.5170365, 13.3888599, "10117 Berlin, Germany", components, annoations, bounds);

        GHEntry ghEntry = Converter.convertFromOpenCageData(entry);
        assertEquals("Berlin", ghEntry.getCity());
        assertEquals(240109189, ghEntry.getOsmId(), .1);
        Extent extent = ghEntry.getExtent();
        assertEquals(13.2288599, extent.getExtent().get(0), .000001);
        assertEquals(52.3570365, extent.getExtent().get(1), .000001);
        assertEquals(13.5488599, extent.getExtent().get(2), .000001);
        assertEquals(52.6770365, extent.getExtent().get(3), .000001);
    }

    @Test
    public void testPeliasonvert() {
        PeliasEntry entry = new PeliasEntry();

        entry.geometry = new PeliasEntry.PeliasGeometry();
        entry.geometry.coordinates = Arrays.asList(new Double[]{-0.087652, 51.508096});

        entry.properties = new PeliasEntry.PeliasProperties();
        entry.properties.source = "openstreetmap";
        entry.properties.source_id = "way/378493707";
        entry.properties.country = "United Kingdom";
        entry.properties.locality = "London";
        entry.properties.macrocounty = "England";
        entry.properties.name = "London Bridge";

        entry.bbox = Arrays.asList(new Double[]{-0.0883452, 51.5067548, -0.0870196, 51.5092817,});

        GHEntry ghEntry = Converter.convertFromPelias(entry);
        assertEquals("London", ghEntry.getCity());
        assertEquals(378493707, ghEntry.getOsmId(), .1);
        Extent extent = ghEntry.getExtent();
        assertEquals(-0.0883452, extent.getExtent().get(0), .000001);
        assertEquals(51.5092817, extent.getExtent().get(1), .000001);
        assertEquals( -0.0870196, extent.getExtent().get(2), .000001);
        assertEquals(51.5067548, extent.getExtent().get(3), .000001);

    }

    @Test
    public void testGisgraphyWithCity() {
        GisgraphyAddressEntry city = new GisgraphyAddressEntry();
        city.setCity("Berlin");
        city.setCountryCode("DE");
        city.setFormatedPostal("Berlin, 10115");
        city.setGeocodingLevel("CITY");
        city.setId(1111L);
        city.setLat(52.5);
        city.setLng(13.3);
        city.setName("Berlin");
        city.setState("state");
        city.setZipCode("10115");

        GHEntry ghResponse = Converter.convertFromGisgraphyAddress(city);
        checkGisgraphyEntity(city, ghResponse);
    }

    @Test
    public void testGisgraphyWithAddress() {
        GisgraphyAddressEntry address = new GisgraphyAddressEntry();
        address.setCity("Paris");
        address.setCountryCode("FR");
        address.setFormatedPostal("103 Avenue des champs élysées,75008 paris");
        address.setGeocodingLevel("HOUSE_NUMBER");
        address.setId(1111L);
        address.setLat(48.8);
        address.setLng(2.3);
        address.setName("Berlin");
        address.setState("Ile de france");
        address.setStreetName("Avenue des champs élysées");
        address.setZipCode("75008");
        address.setHouseNumber("103");

        GHEntry ghResponse = Converter.convertFromGisgraphyAddress(address);
        checkGisgraphyEntity(address, ghResponse);
    }

    protected void checkGisgraphyEntity(GisgraphyAddressEntry address,
                                        GHEntry ghResponse) {
        assertEquals(null, ghResponse.getOsmId());
        assertEquals(null, ghResponse.getOsmType());
        assertEquals(null, ghResponse.getOsmValue());
        assertEquals(address.getLat(), ghResponse.getPoint().getLat(), 0.001);
        assertEquals(address.getLng(), ghResponse.getPoint().getLng(), 0.001);
        assertEquals(address.getDisplayName(), ghResponse.getName());
        assertEquals(address.getCountry(), ghResponse.getCountry());
        assertEquals(address.getCity(), ghResponse.getCity());
        assertEquals(address.getState(), ghResponse.getState());
        assertEquals(address.getZipCode(), ghResponse.getPostcode());
        assertEquals(address.getStreetName(), ghResponse.getStreet());
        assertEquals(address.getHouseNumber(), ghResponse.getHouseNumber());
    }

    @Test
    public void testNetToolKitConvert() {
        // Build a Response
        NetToolKitAddressEntry ntkEntry = new NetToolKitAddressEntry();
        ntkEntry.setLat(37.3865);
        ntkEntry.setLng(-122.0112); 
        ntkEntry.setAddress("790 Duane Avenue E, Sunnyvale, Santa Clara, CA, 94085, USA");
        ntkEntry.setStreetNumber("790");
        ntkEntry.setStreet("E Duane Avenue");
        ntkEntry.setStreetName("Duane");
        ntkEntry.setStreetType("Avenue");
        ntkEntry.setCity("Sunnyvale");
        ntkEntry.setCounty("Santa Clara");
        ntkEntry.setState("California");
        ntkEntry.setStateCode("CA");
        ntkEntry.setPostalCode("94085");
        ntkEntry.setPrecision("rooftop");
        ntkEntry.setProvider("NetToolKit");


        GHEntry ghEntry = Converter.convertFromNetToolKitAddress(ntkEntry);

        assertEquals(ntkEntry.getLat(), ghEntry.getPoint().getLat(), 0.0001);
        assertEquals(ntkEntry.getLng(), ghEntry.getPoint().getLng(), 0.0001);
        assertEquals(ntkEntry.getAddress(), ghEntry.getName());
        assertEquals(ntkEntry.getStreetNumber(), ghEntry.getHouseNumber());
        assertEquals(ntkEntry.getCity(), ghEntry.getCity());
        assertEquals(ntkEntry.getCounty(), ghEntry.getCounty());
        assertEquals(ntkEntry.getState(), ghEntry.getState());
        assertEquals(ntkEntry.getPostalCode(), ghEntry.getPostcode());
        assertEquals(ntkEntry.getStreet(), ghEntry.getStreet());
    }
}
