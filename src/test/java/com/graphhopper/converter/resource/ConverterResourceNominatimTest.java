package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.graphhopper.converter.api.GHResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author Robin Boldt
 */
public class ConverterResourceNominatimTest {
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));

    @Test
    public void testHandleForward() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test forward client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));

        // This might change in OSM and we might need to update this test then
        List<Double> extent = entry.getHits().get(0).getExtent().getExtent();
        assertEquals(extent.get(0), 13.2, .1);
        assertEquals(extent.get(1), 52.3, .1);
        assertEquals(extent.get(2), 13.5, .1);
        assertEquals(extent.get(3), 52.6, .1);
    }

    @Test
    public void testIssue38() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testIssue38");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        // Get the raw json String to check the structure of the json
        String responseString = response.readEntity(String.class);
        assertTrue(responseString.contains("\"extent\":["));
        assertFalse(responseString.contains("\"extent\":{\"extent\""));
    }

    @Test
    public void testHandleReverse() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test reverse client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?point=52.5487429714954,-1.81602098644987&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void testCorrectLocale() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testCorrectLocale");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("de"));
    }

    @Test
    public void testCorrectLocaleCountry() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testCorrectLocaleCountry");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de-ch", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("de-CH"));
    }

    @Test
    public void testIncorrectLocale() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testIncorrectLocale");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=IAmNotValid", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));
    }

    @Test
    public void testIncorrectLocaleCountry() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testIncorrectLocaleCountry");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin&locale=de-zz", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getLocale().equals("en"));
    }

    @Test
    public void testIncorrectFormattedPoint() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("testIncorrectFormattedPoint");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?reverse=true&point=NaN,NaN", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(400);
    }
}
