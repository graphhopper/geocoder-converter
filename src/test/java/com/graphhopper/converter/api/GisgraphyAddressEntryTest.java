package com.graphhopper.converter.api;

import org.junit.Assert;
import org.junit.Test;

public class GisgraphyAddressEntryTest {

    @Test
    public void getDisplayName() {
        GisgraphyAddressEntry gisgraphyAddressEntry = new GisgraphyAddressEntry();
        Assert.assertNull(gisgraphyAddressEntry.getDisplayName());

        gisgraphyAddressEntry.setGeocodingLevel("HOUSE_NUMBER");
        gisgraphyAddressEntry.setStreetName("streetname");
        gisgraphyAddressEntry.setName("name");
        Assert.assertEquals(gisgraphyAddressEntry.getStreetName(),
                gisgraphyAddressEntry.getDisplayName());

        gisgraphyAddressEntry.setGeocodingLevel("CITY");
        Assert.assertEquals(gisgraphyAddressEntry.getName(),
                gisgraphyAddressEntry.getDisplayName());

        gisgraphyAddressEntry.setGeocodingLevel("STATE");
        Assert.assertEquals(gisgraphyAddressEntry.getName(),
                gisgraphyAddressEntry.getDisplayName());

    }

}
