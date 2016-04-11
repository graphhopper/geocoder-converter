package com.graphhopper.converter;

import com.graphhopper.converter.api.IPFilter;
import com.graphhopper.converter.health.NominatimHealthCheck;
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
 * @author Robin Boldt
 */
public class ConverterApplication extends Application<ConverterConfiguration> {

    public static void main(String[] args) throws Exception {
        new ConverterApplication().run(args);
    }


    @Override
    public String getName() {
        return "graphhopper-nominatim-converter";
    }

    @Override
    public void initialize(Bootstrap<ConverterConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(ConverterConfiguration converterConfiguration, Environment environment) throws Exception {

        JerseyClientConfiguration cfg = converterConfiguration.getJerseyClientConfiguration();
        cfg.setTimeout(Duration.seconds(5));
        cfg.setConnectionTimeout(Duration.seconds(5));
        cfg.setConnectionRequestTimeout(Duration.seconds(5));
        
        final Client client = new JerseyClientBuilder(environment).using(cfg)
                .build(getName());

        if (converterConfiguration.isNominatim()) {
            final ConverterResourceNominatim resource = new ConverterResourceNominatim(
                    converterConfiguration.getNominatimUrl(), converterConfiguration.getNominatimEmail(), client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isOpenCageData()) {
            final ConverterResourceOpenCageData resource = new ConverterResourceOpenCageData(
                    converterConfiguration.getOpenCageDataUrl(), converterConfiguration.getOpenCageDataKey(), client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isHealthCheck()) {
            final NominatimHealthCheck healthCheck =
                    new NominatimHealthCheck(converterConfiguration.getNominatimUrl(), client);
            environment.healthChecks().register("template", healthCheck);
        }

        environment.servlets().addFilter("ip-filter", new IPFilter(converterConfiguration.getIPWhiteList(), converterConfiguration.getIPBlackList())).addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
