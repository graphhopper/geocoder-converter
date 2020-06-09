package com.graphhopper.converter;

import com.graphhopper.converter.api.IPFilter;
import com.graphhopper.converter.health.NominatimHealthCheck;
import com.graphhopper.converter.resources.*;

import com.graphhopper.converter.resources.ConverterResourceNetToolKit;
import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

import javax.servlet.DispatcherType;
import javax.ws.rs.client.Client;

import java.util.EnumSet;

/**
 * @author Robin Boldt,David Masclet, Xuejing Dong
 */
public class ConverterApplication extends Application<ConverterConfiguration> {

    public static void main(String[] args) throws Exception {
        new ConverterApplication().run(args);
    }

    @Override
    public String getName() {
        return "graphhopper-geocoder-converter";
    }

    @Override
    public void initialize(Bootstrap<ConverterConfiguration> bootstrap) {
        bootstrap.setObjectMapper(io.dropwizard.jackson.Jackson.newMinimalObjectMapper());
        // Enable variable substitution with environment variables
        bootstrap.setConfigurationSourceProvider(
                new SubstitutingSourceProvider(bootstrap.getConfigurationSourceProvider(),
                        new EnvironmentVariableSubstitutor(false)
                )
        );

    }

    @Override
    public void run(ConverterConfiguration converterConfiguration, Environment environment) throws Exception {

        JerseyClientConfiguration cfg = converterConfiguration.getJerseyClientConfiguration();
        cfg.setTimeout(Duration.seconds(10));
        cfg.setConnectionTimeout(Duration.seconds(10));
        cfg.setConnectionRequestTimeout(Duration.seconds(10));

        final Client client = new JerseyClientBuilder(environment).using(cfg)
                .build(getName());

        if (converterConfiguration.isNominatim()) {
            final ConverterResourceNominatim resource = new ConverterResourceNominatim(
                    converterConfiguration.getNominatimURL(),
                    converterConfiguration.getNominatimReverseURL(),
                    converterConfiguration.getNominatimEmail(),
                    client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isOpenCageData()) {
            final ConverterResourceOpenCageData resource = new ConverterResourceOpenCageData(
                    converterConfiguration.getOpenCageDataURL(), converterConfiguration.getOpenCageDataKey(), client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isPelias()) {
            final ConverterResourcePelias resource = new ConverterResourcePelias(
                    converterConfiguration.getPeliasURL(), converterConfiguration.getPeliasKey(), client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isGisgraphy()) {
            final ConverterResourceGisgraphy resource = new ConverterResourceGisgraphy(
                    converterConfiguration.getGisgraphyGeocodingURL(), converterConfiguration.getGisgraphyReverseGeocodingURL(), converterConfiguration.getGisgraphySearchURL(), converterConfiguration.getGisgraphyAPIKey(), client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isNetToolKit()) {
            final ConverterResourceNetToolKit resource = new ConverterResourceNetToolKit(
                    converterConfiguration.getNetToolKitGeocodingURL(),
                    converterConfiguration.getNetToolKitReverseGeocodingURL(),
                    converterConfiguration.getNetToolKitKey(),
                    client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isPhoton()) {
            final ConverterResourcePhoton resource = new ConverterResourcePhoton(
                    converterConfiguration.getPhotonURL(),
                    converterConfiguration.getPhotonReverseURL(),
                    client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isHealthCheck()) {
            final NominatimHealthCheck healthCheck =
                    new NominatimHealthCheck(converterConfiguration.getNominatimURL(), client);
            environment.healthChecks().register("template", healthCheck);
        }

        environment.servlets().addFilter("ip-filter", new IPFilter(converterConfiguration.getIPWhiteList(), converterConfiguration.getIPBlackList())).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
