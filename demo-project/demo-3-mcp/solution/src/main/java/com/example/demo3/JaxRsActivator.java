package com.example.demo3;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS activator that registers the application under the {@code /api} base path.
 *
 * <p>All REST resources (e.g. {@link GameResource}) are automatically discovered
 * by the CDI container and exposed under {@code /api}.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
}
