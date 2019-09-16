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

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * In order to successfully run this class, you need to specify the ntkKey as environment variable, for example run:
 * export NTK_KEY="My_Key"
 *
 * @author Xuejing Dong
 */
public class ConverterResourceNetToolKitTest {
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));

    @Test
    public void testHandleForward() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test forward client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nettoolkit?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);

        //Try with an address
        response = client.target(
                String.format("http://localhost:%d/nettoolkit?q=790+E+Duane+Avenue,+Sunnyvale,+CA,+94085", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }

    @Test
    public void testHandleReverse() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test reverse client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/nettoolkit/?point=52.5487429714954,-1.81602098644987&reverse=true", RULE.getLocalPort()))
                .request()
                .get();
        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size() > 0);
    }
}

