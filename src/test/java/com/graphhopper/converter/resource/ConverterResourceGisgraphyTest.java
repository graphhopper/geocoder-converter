package com.graphhopper.converter.resource;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientProperties;
import org.junit.ClassRule;
import org.junit.Test;

import com.graphhopper.converter.ConverterApplication;
import com.graphhopper.converter.ConverterConfiguration;
import com.graphhopper.converter.api.GHResponse;
import com.graphhopper.converter.resources.ConverterResourceGisgraphy;

/**
 * @author Robin Boldt
 */
public class ConverterResourceGisgraphyTest {
    @ClassRule
    public static final DropwizardAppRule<ConverterConfiguration> RULE =
            new DropwizardAppRule<>(ConverterApplication.class, ResourceHelpers.resourceFilePath("converter.yml"));

    @Test
    public void testHandleForward() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test forward client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/gisgraphy?q=berlin&querytype="+ConverterResourceGisgraphy.GEOCODING_QUERY_TYPE, RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size()>0);
        
        //now try with an Address
        response = client.target(
                String.format("http://localhost:%d/gisgraphy?q=103+avenue+des+champs+elysees,paris&querytype="+ConverterResourceGisgraphy.GEOCODING_QUERY_TYPE, RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size()>0);
    }

    @Test
    public void testHandleReverse() {
    	 Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test reverse client");

         client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
         client.property(ClientProperties.READ_TIMEOUT, 100000);

         Response response = client.target(
                 String.format("http://localhost:%d/gisgraphy/?lat=52.5487429714954&lng=-1.81602098644987&reverse&querytype="+ConverterResourceGisgraphy.REVERSE_QUERY_TYPE, RULE.getLocalPort()))
                 .request()
                 .get();

         assertThat(response.getStatus()).isEqualTo(200);
         GHResponse entry = response.readEntity(GHResponse.class);
         assertTrue(entry.getHits().size()>0);
         
 
    }
    
    @Test
    public void testHandleAutocomplete() {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test autocomplete client");

        client.property(ClientProperties.CONNECT_TIMEOUT, 100000);
        client.property(ClientProperties.READ_TIMEOUT, 100000);

        Response response = client.target(
                String.format("http://localhost:%d/gisgraphy?q=pari&querytype="+ConverterResourceGisgraphy.AUTOCOMPLETE_QUERY_TYPE, RULE.getLocalPort()))
                .request()
                .get();

        assertThat(response.getStatus()).isEqualTo(200);
        GHResponse entry = response.readEntity(GHResponse.class);
        assertTrue(entry.getHits().size()>0);
        
    }

   

   

  

   
}
