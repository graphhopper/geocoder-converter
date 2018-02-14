package com.graphhopper.converter.api;

import org.junit.Assert;
import org.junit.Test;

import com.graphhopper.converter.data.CountryInfo;

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

	@Test
	public void getCountry() {
		GisgraphyAddressEntry gisgraphyAddressEntry = new GisgraphyAddressEntry();
		gisgraphyAddressEntry.setCountryCode("DE");
		Assert.assertEquals(CountryInfo.countryLookupMap.get("DE"),
				gisgraphyAddressEntry.getCountry());

		gisgraphyAddressEntry.setCountryCode("de");
		Assert.assertEquals("lower case should be managed",
				CountryInfo.countryLookupMap.get("DE"),
				gisgraphyAddressEntry.getCountry());
		Assert.assertNotNull(gisgraphyAddressEntry.getCountry());

		gisgraphyAddressEntry.setCountryCode("");
		Assert.assertEquals("empty code should be managed",
				CountryInfo.countryLookupMap.get(""),
				gisgraphyAddressEntry.getCountry());
		Assert.assertNull(gisgraphyAddressEntry.getCountry());

		gisgraphyAddressEntry.setCountryCode(null);
		Assert.assertEquals("null code should be managed",
				CountryInfo.countryLookupMap.get("de"),
				gisgraphyAddressEntry.getCountry());
		Assert.assertNull(gisgraphyAddressEntry.getCountry());
	}

}
