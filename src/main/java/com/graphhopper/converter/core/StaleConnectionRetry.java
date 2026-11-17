package com.graphhopper.converter.core;

import io.opentelemetry.api.trace.Span;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Repeats a GET once, right away, when it fails with an I/O error before any response, typically because the
 * provider has just closed the pooled connection it was sent on (NoHttpResponseException). This is HttpClient's
 * default strategy without its retries on 429/503 responses, which wait for Retry-After (unbounded) or 1s:
 * longer than dirigent waits for us, and they would hit a provider again right after it rate-limited us.
 * Timeouts, connection failures and TLS errors are not retried, as in the default strategy.
 */
public class StaleConnectionRetry extends DefaultHttpRequestRetryStrategy {

    public StaleConnectionRetry() {
        super(1, TimeValue.ZERO_MILLISECONDS,
                List.of(InterruptedIOException.class, UnknownHostException.class, ConnectException.class,
                        ConnectionClosedException.class, NoRouteToHostException.class, SSLException.class),
                List.of());
    }

    @Override
    public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
        boolean retry = super.retryRequest(request, exception, execCount, context);
        // the javaagent makes one client span for all attempts, so a retry is not visible otherwise
        if (retry)
            Span.current().setAttribute("http.request.resend_count", execCount);
        return retry;
    }
}
