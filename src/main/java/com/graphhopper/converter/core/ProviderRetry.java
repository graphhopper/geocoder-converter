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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

/**
 * Repeats a GET to a provider once, right away, when it fails with an I/O error before any response, typically
 * because the provider has just closed the pooled connection it was sent on (NoHttpResponseException). For the
 * given hosts (OpenCage) a timeout is repeated too: in OpenCage's incidents only some requests hang, about one in
 * ten, so a second attempt on another connection usually gets an answer.
 * <p>
 * This is HttpClient's default strategy without its retries on 429/503 responses, which wait for Retry-After
 * (unbounded) or 1s: longer than dirigent waits for us, and they would hit a provider again right after it
 * rate-limited us. Other timeouts, connection failures and TLS errors are not retried, as in the default strategy.
 */
public class ProviderRetry extends DefaultHttpRequestRetryStrategy {

    private final Set<String> timeoutRetryHosts;

    public ProviderRetry(Set<String> timeoutRetryHosts) {
        super(1, TimeValue.ZERO_MILLISECONDS,
                List.of(InterruptedIOException.class, UnknownHostException.class, ConnectException.class,
                        ConnectionClosedException.class, NoRouteToHostException.class, SSLException.class),
                List.of());
        this.timeoutRetryHosts = timeoutRetryHosts;
    }

    @Override
    public boolean retryRequest(HttpRequest request, IOException exception, int execCount, HttpContext context) {
        boolean retry = super.retryRequest(request, exception, execCount, context)
                || execCount == 1 && exception instanceof SocketTimeoutException && handleAsIdempotent(request)
                && request.getAuthority() != null && timeoutRetryHosts.contains(request.getAuthority().getHostName());
        // the javaagent makes one client span for all attempts, so a retry is not visible otherwise
        if (retry)
            Span.current().setAttribute("http.request.resend_count", execCount);
        return retry;
    }
}
