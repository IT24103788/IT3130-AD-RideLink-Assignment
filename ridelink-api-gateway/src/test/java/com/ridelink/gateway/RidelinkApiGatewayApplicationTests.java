package com.ridelink.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Gateway Application Context Load Test
 *
 * <p>Verifies that the Spring application context loads successfully.
 * This catches configuration errors (missing beans, invalid YAML, wrong
 * dependencies) early, at test time rather than at runtime.
 *
 * <p>WebEnvironment.RANDOM_PORT starts the real server on a random port so we
 * do not conflict with ports 8080-8084 during the test run.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RidelinkApiGatewayApplicationTests {

    @Test
    void contextLoads() {
        // If the application context fails to start, this test fails with a
        // descriptive error. No assertions needed — the test itself IS the check.
    }
}
