package com.graphhopper.converter.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.graphhopper.converter.api.GHEntry;
import com.graphhopper.converter.api.GisgraphyAddressEntry;
import com.graphhopper.converter.api.NominatimEntry;

/**
 * @author Robin Boldt,David Masclet
 */
public class ConverterTest {

    @Test
    public void testConvert() {

        // Build a Response
        NominatimEntry nominatimResponse = new NominatimEntry(1L, "node", 1, 1, "test", "de", "Berlin");
        assertEquals("N", nominatimResponse.getGHOsmType());

        GHEntry ghResponse = Converter.convertFromNominatim(nominatimResponse);

        assertEquals(1L, (long) ghResponse.getOsmId());
        assertEquals("N", ghResponse.getOsmType());
        assertEquals(1, ghResponse.getPoint().getLat(), 0.001);
        assertEquals(1, ghResponse.getPoint().getLng(), 0.001);
        assertEquals("test", ghResponse.getName());
        assertEquals("de", ghResponse.getCountry());
        assertEquals("Berlin", ghResponse.getCity());
    }

    @Test
    public void testConvertCityFallback() {
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
    public void testStreetEntry() {
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
    public void testGisgraphyWithCity(){
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
    	
    	GHEntry ghResponse  = Converter.convertFromGisgraphyAddress(city);
    	checkGisgraphyEntity(city, ghResponse);
    }
    
    
    @Test
    public void testGisgraphyWithAddress(){
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
    	
    	GHEntry ghResponse  = Converter.convertFromGisgraphyAddress(address);
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
}
