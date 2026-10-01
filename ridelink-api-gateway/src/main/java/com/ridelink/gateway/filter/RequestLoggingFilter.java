package com.ridelink.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * RequestLoggingFilter — Global Gateway Filter
 *
 * <p>This filter runs for EVERY request that passes through the gateway.
 * It logs:
 * <ul>
 *   <li>Incoming request method and path (before routing)</li>
 *   <li>The HTTP status code of the downstream service response</li>
 *   <li>Total time taken for the round trip</li>
 * </ul>
 *
 * <p>This is a cross-cutting concern — it belongs in the gateway, not in any
 * individual microservice.
 *
 * <p>During a viva you can explain:
 * <ul>
 *   <li>This implements {@code GlobalFilter} — Spring Cloud Gateway's
 *       interface for filters that apply to all routes.</li>
 *   <li>{@code Ordered.HIGHEST_PRECEDENCE} ensures this filter runs first
 *       (i.e., logs the request before any other processing).</li>
 *   <li>The filter is reactive — it returns a {@code Mono<Void>} and uses
 *       {@code .then()} to run code after the downstream call completes.</li>
 * </ul>
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    /**
     * Main filter method. Called by the gateway for every request.
     *
     * @param exchange contains the request and response
     * @param chain    used to pass control to the next filter / route
     * @return a Mono that completes when the response has been sent
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();

        // Log the incoming request BEFORE forwarding to the downstream service
        log.info("[GATEWAY >>>] {} {} — from {}",
                request.getMethod(),
                request.getURI().getPath(),
                request.getRemoteAddress());

        // Pass the request through; then log the response after it comes back
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {

            ServerHttpResponse response = exchange.getResponse();
            long duration = System.currentTimeMillis() - startTime;

            log.info("[GATEWAY <<<] {} {} — status: {} — {}ms",
                    request.getMethod(),
                    request.getURI().getPath(),
                    response.getStatusCode(),
                    duration);
        }));
    }

    /**
     * Run this filter first, before all others.
     * HIGHEST_PRECEDENCE = Integer.MIN_VALUE (lowest number = highest priority).
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
