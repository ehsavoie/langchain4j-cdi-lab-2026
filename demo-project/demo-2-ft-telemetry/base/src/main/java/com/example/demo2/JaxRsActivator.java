package com.example.demo2;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activates JAX-RS support for this application.
 * All REST endpoints will be accessible under /api/* path.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
    // No implementation needed - just marks this as a JAX-RS application
}
