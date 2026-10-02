package com.ridelink.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;

/**
 * GatewayErrorHandler — Custom Exception Handler for the API Gateway
 *
 * <p>This class intercepts errors that occur WITHIN the gateway itself:
 * <ul>
 *   <li>Service unreachable (connection refused) → 503 Service Unavailable</li>
 *   <li>No route found for the request path → 404 Not Found</li>
 *   <li>Gateway timeout → 504 Gateway Timeout</li>
 * </ul>
 *
 * <p>Without this, the gateway returns a generic error page. With this handler,
 * the client receives a clean JSON error response that is easy to read in
 * Postman and easy to explain in a viva.
 *
 * <p>Viva explanation:
 * <ul>
 *   <li>{@code ErrorWebExceptionHandler} is the WebFlux equivalent of
 *       Spring MVC's {@code @ControllerAdvice} for exception handling.</li>
 *   <li>{@code @Order(-1)} ensures this handler runs before Spring Boot's
 *       default error handler (which has {@code @Order(-2)} … wait, actually
 *       we use -1 so we run AFTER the default WebFlux handler at -2 but
 *       BEFORE any other handler. More precisely, a lower number = higher
 *       priority in Spring; we use -1 which is higher priority than 0).</li>
 * </ul>
 */
@Component
@Order(-1)
public class GatewayErrorHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayErrorHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        String message;

        // ── Connection refused: downstream service is down ────────────────
        if (ex instanceof ConnectException
                || (ex.getCause() instanceof ConnectException)) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Service is currently unavailable. Please try again later.";
            log.error("[GATEWAY ERROR] Downstream service unreachable: {}", ex.getMessage());
        }
        // ── No route found: the requested path is not mapped ─────────────
        else if (ex instanceof NotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = "No route found for this path. Check the API documentation.";
            log.warn("[GATEWAY ERROR] No route found: {}", exchange.getRequest().getURI().getPath());
        }
        // ── ResponseStatusException (e.g. 404 from downstream) ───────────
        else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : "An error occurred.";
            log.warn("[GATEWAY ERROR] ResponseStatusException: {}", ex.getMessage());
        }
        // ── Gateway timeout ────────────────────────────────────────────────
        else if (ex instanceof java.util.concurrent.TimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = "The downstream service did not respond in time.";
            log.error("[GATEWAY ERROR] Gateway timeout: {}", ex.getMessage());
        }
        // ── Catch-all for any other error ─────────────────────────────────
        else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An unexpected gateway error occurred.";
            log.error("[GATEWAY ERROR] Unexpected error: {}", ex.getMessage(), ex);
        }

        response.setStatusCode(status);

        // Build a simple JSON error body
        String body = String.format(
                "{\"error\":\"%s\",\"status\":%d,\"message\":\"%s\",\"path\":\"%s\"}",
                status.getReasonPhrase(),
                status.value(),
                message,
                exchange.getRequest().getURI().getPath()
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
