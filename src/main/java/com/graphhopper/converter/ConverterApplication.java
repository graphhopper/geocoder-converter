package com.graphhopper.converter;

import com.graphhopper.converter.api.IPFilter;
import com.graphhopper.converter.health.NominatimHealthCheck;
import com.graphhopper.converter.resources.ConverterResourceGisgraphy;
import com.graphhopper.converter.resources.ConverterResourceNominatim;
import com.graphhopper.converter.resources.ConverterResourceOpenCageData;

import io.dropwizard.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Duration;

import javax.servlet.DispatcherType;
import javax.ws.rs.client.Client;

import java.util.EnumSet;

/**
 * @author Robin Boldt,David Masclet
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
        // nothing to do yet
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

        if (converterConfiguration.isGisgraphy()) {
            final ConverterResourceGisgraphy resource = new ConverterResourceGisgraphy(
                    converterConfiguration.getGisgraphyGeocodingURL(), converterConfiguration.getGisgraphyReverseGeocodingURL(),converterConfiguration.getGisgraphySearchURL(),converterConfiguration.getGisgraphyAPIKey(), client);
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
