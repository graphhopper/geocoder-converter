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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In order to successfully run this class, you need to specify the ntkKey as environment variable, for example run:
 * export NTK_KEY="My_Key"
 *
 * @author Xuejing Dong
 */
@ExtendWith(DropwizardExtensionsSupport.class)
public class ConverterResourceNetToolKitTest {
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
                String.format("http://localhost:%d/nettoolkit?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertEquals(200, response.getStatus());
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);

        //Try with an address
        response = client.target(
                String.format("http://localhost:%d/nettoolkit?q=790+E+Duane+Avenue,+Sunnyvale,+CA,+94085", RULE.getLocalPort()))
                .request()
                .get();

        assertEquals(200, response.getStatus());
        entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }

    @Test
    public void testHandleReverse() {
        Response response = client.target(
                String.format("http://localhost:%d/nettoolkit/?point=52.5487429714954,-1.81602098644987&reverse=true", RULE.getLocalPort()))
                .request()
                .get();
        assertEquals(200, response.getStatus());
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }
}
