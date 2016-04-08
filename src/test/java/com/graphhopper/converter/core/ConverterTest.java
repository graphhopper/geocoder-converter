package com.graphhopper.converter.core;

import com.graphhopper.converter.api.GHResponse;
import com.graphhopper.converter.api.NominatimEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Robin Boldt
 */
public class ConverterTest
{

    @Test
    public void testConvert()
    {

        // Build a Response
        NominatimEntry nominatimResponse = new NominatimEntry(1L, 1, 1, "test", "de", "Berlin");
        GHResponse ghResponse = Converter.convertFromNominatim(nominatimResponse);

        assertEquals(1L, (long) ghResponse.getOsmId());
        assertEquals(1, ghResponse.getPoint().getLat(), 0.001);
        assertEquals(1, ghResponse.getPoint().getLng(), 0.001);
        assertEquals("test", ghResponse.getName());
        assertEquals("de", ghResponse.getCountry());
        assertEquals("Berlin", ghResponse.getCity());
    }

    @Test
    public void testConvertCityFallback()
    {
        // Build a Response
        NominatimEntry nominatimResponse = new NominatimEntry();
        nominatimResponse.setOsmId(1L);
        nominatimResponse.setDisplayName("test");
        nominatimResponse.setLat(1);
        nominatimResponse.setLon(1);
        nominatimResponse.getAddress().setTown("townie");
        nominatimResponse.getAddress().setCountry("gb");
        GHResponse ghResponse = Converter.convertFromNominatim(nominatimResponse);

        assertEquals(1L, (long)ghResponse.getOsmId());
        assertEquals(1, ghResponse.getPoint().getLat(), 0.001);
        assertEquals(1, ghResponse.getPoint().getLng(), 0.001);
        assertEquals("test", ghResponse.getName());
        assertEquals("gb", ghResponse.getCountry());
        assertEquals("townie", ghResponse.getCity());
    }
}
