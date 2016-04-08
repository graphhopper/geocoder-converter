package com.graphhopper.converter;

import com.graphhopper.converter.health.NominatimHealthCheck;
import com.graphhopper.converter.resources.ConverterResource;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.ws.rs.client.Client;

/**
 * @author Robin Boldt
 */
public class ConverterApplication extends Application<ConverterConfiguration>
{

    public static void main( String[] args ) throws Exception
    {
        new ConverterApplication().run(args);
    }


    @Override
    public String getName()
    {
        return "graphhopper-nominatim-converter";
    }

    @Override
    public void initialize( Bootstrap<ConverterConfiguration> bootstrap )
    {
        // nothing to do yet
    }

    @Override
    public void run( ConverterConfiguration converterConfiguration, Environment environment ) throws Exception
    {

        final Client client = new JerseyClientBuilder(environment).using(converterConfiguration.getJerseyClientConfiguration())
                .build(getName());

        final ConverterResource resource = new ConverterResource(
                converterConfiguration.getNominatimUrl(),
                client
        );

        if(converterConfiguration.isHealthCheck()) 
        {
            final NominatimHealthCheck healthCheck =
                    new NominatimHealthCheck(converterConfiguration.getNominatimUrl(), client);
            environment.healthChecks().register("template", healthCheck);
        }

        environment.jersey().register(resource);

    }
}
