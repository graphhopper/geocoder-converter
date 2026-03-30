package com.graphhopper.converter.api;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GisgraphyAddressEntryTest {

    @Test
    public void getDisplayName() {
        GisgraphyAddressEntry gisgraphyAddressEntry = new GisgraphyAddressEntry();
        Assertions.assertNull(gisgraphyAddressEntry.getDisplayName());

        gisgraphyAddressEntry.setGeocodingLevel("HOUSE_NUMBER");
        gisgraphyAddressEntry.setStreetName("streetname");
        gisgraphyAddressEntry.setName("name");
        Assertions.assertEquals(gisgraphyAddressEntry.getStreetName(),
                gisgraphyAddressEntry.getDisplayName());

        gisgraphyAddressEntry.setGeocodingLevel("CITY");
        Assertions.assertEquals(gisgraphyAddressEntry.getName(),
                gisgraphyAddressEntry.getDisplayName());

        gisgraphyAddressEntry.setGeocodingLevel("STATE");
        Assertions.assertEquals(gisgraphyAddressEntry.getName(),
                gisgraphyAddressEntry.getDisplayName());

    }

}
