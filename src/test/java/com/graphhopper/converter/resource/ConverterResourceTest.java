package com.graphhopper.converter.resource;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Robin Boldt
 */
public class ConverterResourceTest
{
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));

    @Test
    public void testHandle()
    {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT,    100000);

        Response response = client.target(
                String.format("http://localhost:%d/nominatim?q=berlin", RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
