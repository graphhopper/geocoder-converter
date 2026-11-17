package com.graphhopper.converter.api;

import io.opentelemetry.api.trace.Span;
import jakarta.ws.rs.WebApplicationException;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

/**
 * Records exceptions that end in a server error on the request's server span. Dropwizard's exception mappers turn
 * them into responses inside Jersey, so the OpenTelemetry agent only sees the 500 status, never the exception.
 * Exceptions that carry a client error status (4xx) are not recorded.
 */
public class SpanExceptionRecorder implements ApplicationEventListener {

    private static final RequestEventListener RECORDER = event -> {
        if (event.getType() != RequestEvent.Type.ON_EXCEPTION)
            return;
        Throwable e = event.getException();
        // Jersey wraps exceptions thrown by resource methods (other than WebApplicationException) in this
        if (e instanceof MappableException && e.getCause() != null)
            e = e.getCause();
        if (e instanceof WebApplicationException wae && wae.getResponse().getStatus() < 500)
            return;
        Span.current().recordException(e);
    };

    @Override
    public void onEvent(ApplicationEvent event) {
    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        return RECORDER;
    }
}
