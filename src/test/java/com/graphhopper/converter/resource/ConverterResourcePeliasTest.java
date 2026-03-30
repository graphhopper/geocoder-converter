package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.graphhopper.converter.api.GHResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Robin Boldt
 */
@Disabled
@ExtendWith(DropwizardExtensionsSupport.class)
public class ConverterResourcePeliasTest {
    static final DropwizardAppExtension<ConverterConfiguration> RULE =
            new DropwizardAppExtension<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));
    private static Client client;

    @BeforeAll
    public static void setup() {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);
    }

    @Test
    public void testHandleForward() {
        Response response = client.target(
                String.format("http://localhost:%d/pelias?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertEquals(200, response.getStatus());
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("en", entry.getLocale());

        // This might change in OSM and we might need to update this test then
        List<Double> extent = entry.getHits().get(0).getExtent().getExtent();
        assertEquals(13.1, extent.get(0), .1);
        assertEquals(52.6, extent.get(1), .1);
        assertEquals(13.7, extent.get(2), .1);
        assertEquals(52.3, extent.get(3), .1);
    }

    @Test
    public void testIssue50() {
        Response response = client.target(
                String.format("http://localhost:%d/pelias?point=48.4882,2.6996&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertEquals(200, response.getStatus());
        GHResponse entry = response.readEntity(GHResponse.class);

        assertEquals("Seine-et-Marne", entry.getHits().get(0).getState());
    }

}
