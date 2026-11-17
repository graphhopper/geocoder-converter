package com.graphhopper.converter;

import com.graphhopper.converter.api.CacheFilter;
import com.graphhopper.converter.api.IPFilter;
import com.graphhopper.converter.api.SpanExceptionRecorder;
import com.graphhopper.converter.core.StaleConnectionRetry;
import com.graphhopper.converter.health.NominatimHealthCheck;
import com.graphhopper.converter.resources.*;
import io.dropwizard.core.Application;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.util.Duration;
import org.eclipse.jetty.server.handler.CrossOriginHandler;

import jakarta.servlet.DispatcherType;
import jakarta.ws.rs.client.Client;
import java.util.EnumSet;
import java.util.Set;

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
        // dirigent waits 12s for us. Keep 10s for the provider's answer (OpenCage has legitimate answers
        // at 9-10s), but bound the connection setup: the TLS handshake timeout defaults to the connect
        // timeout, so with 10s each a single provider call could run 20-30s, long after dirigent gave up.
        // 2s still survives one lost SYN (retransmitted after 1s).
        cfg.setTimeout(Duration.seconds(10));
        cfg.setConnectionTimeout(Duration.seconds(2));
        cfg.setConnectionRequestTimeout(Duration.seconds(1));
        // Dropwizard's default keepAlive of 0 closes the connection after every request, so every provider
        // call paid a full TCP+TLS handshake. Reuse connections, idle for less than common server keep-alive
        // timeouts (Apache httpd: 5s). Pooled connections are checked for staleness before reuse, but a provider
        // can still close one just as we send on it, so such requests are repeated once (StaleConnectionRetry).
        // Dropwizard's default of 0 retries ignores any retry strategy, hence setRetries(1).
        cfg.setKeepAlive(Duration.seconds(4));
        cfg.setRetries(1);

        final Client client = new JerseyClientBuilder(environment).using(cfg)
                .using(new StaleConnectionRetry())
                .build(getName());

        if (converterConfiguration.isWithCORS()) {
            CrossOriginHandler corsHandler = new CrossOriginHandler();
            corsHandler.setAllowedOriginPatterns(Set.of("*"));
            corsHandler.setAllowedHeaders(Set.of("X-Requested-With", "Content-Type", "Accept", "Origin"));
            corsHandler.setAllowedMethods(Set.of("OPTIONS", "GET", "PUT", "POST", "HEAD"));
            environment.getApplicationContext().insertHandler(corsHandler);
        }

        environment.jersey().register(new SpanExceptionRecorder());
        environment.jersey().register(new CacheFilter(environment.getObjectMapper(), 200_000_000L, 10));

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
                    converterConfiguration.getNetToolKitTimeout(),
                    client);
            environment.jersey().register(resource);
        }

        if (converterConfiguration.isPhoton()) {
            final ConverterResourcePhoton resource = new ConverterResourcePhoton(
                    converterConfiguration.getPhotonURL(),
                    converterConfiguration.getPhotonReverseURL(),
                    converterConfiguration.getPhotonSupportedLanguages(),
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
