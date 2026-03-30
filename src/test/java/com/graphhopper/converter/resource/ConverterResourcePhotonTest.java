package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.graphhopper.converter.api.GHResponse;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

/**
 * @author Robin Boldt
 */
public class ConverterResourcePhotonTest {
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));
    private static Client client;

    @BeforeClass
    public static void setup() {
        client = new JerseyClientBuilder(RULE.getEnvironment()).build("client");
        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);
    }

    @Test
    public void testHandleForward() {
        Response response = client.target(
                        String.format("http://localhost:%d/photon?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("default", entry.getLocale()); // by default don't use e.g. "en" as it would incorrectly use name:en instead of name
        assertFalse(entry.getHits().isEmpty());
        assertEquals("Berlin", entry.getHits().get(0).getName());
        assertEquals("Deutschland", entry.getHits().get(0).getCountry());
    }

    @Test
    public void testLocationBiasScale() {
        // First test a low bias -> big number (has reverse meaning in photon!? see https://github.com/komoot/photon/issues/600)
        Response response = client.target(String.format("http://localhost:%d/photon?q=beer&point=48.774675,9.172136&location_bias_scale=1", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertNotEquals("Beerstraße", entry.getHits().get(0).getName());

        // Now test a high bias
        response = client.target(String.format("http://localhost:%d/photon?q=beer&point=48.774675,9.172136&location_bias_scale=0.1", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertEquals("70199", entry.getHits().get(0).getPostcode());
        assertEquals("Beerstraße", entry.getHits().get(0).getName());
    }

    @Test
    public void testBBox() {
        Response response = client.target(String.format("http://localhost:%d/photon?q=berlin&bbox=9.5,51.5,11.5,53.5&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("30175", entry.getHits().get(0).getPostcode());
    }

    @Test
    public void testHandleReverse() {
        Response response = client.target(String.format("http://localhost:%d/photon?point=48.774675,9.172136&reverse=true", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Rotebühlplatz Position 2", entry.getHits().get(0).getName());
        assertEquals("Baden-Württemberg", entry.getHits().get(0).getState());
    }

    @Test
    public void osmTags() {
        Response response = client.target(String.format("http://localhost:%d/photon?q=berlin&osm_tag=place:city", RULE.getLocalPort()))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Berlin", entry.getHits().get(0).getName());
        assertEquals("city", entry.getHits().get(0).getOsmValue());

        response = client.target(
                        String.format("http://localhost:%d/photon?q=berlin&osm_tag=!place:city", RULE.getLocalPort()))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertNotEquals("Berlin", entry.getHits().get(0).getName());
    }

    @Test
    public void testReverseWithOSMTags() {
        Response response = client.target(String.format("http://localhost:%d/photon?point=40.694632,-74.097403&reverse=true&osm_tag=aeroway:aerodrome&radius=30", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);

        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals("Newark Liberty International Airport", entry.getHits().get(0).getName());
    }

    @Test
    public void testLongQuery() {
        Response response = client.target(String.format("http://localhost:%d/photon?q=hh+hh+hhh+hh+hh+hhh+hh+hh+hhh+hh+hhhh+hh+hhh+hh+hh+hhh+hh+hh+hhh+hh+hhhh+hh+hhh+hh+hh+hhh+hh+hh+hhh+hh+hh+hh", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.readEntity(String.class)).contains("q parameter cannot contain more than 30 spaces");
    }

    @Test
    public void testCorrectLocale() {
        Response response = client.target(
                        String.format("http://localhost:%d/photon?q=berlin&locale=de", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertEquals(entry.getLocale(), "de");
    }

}
