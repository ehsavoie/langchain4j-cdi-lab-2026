package com.example.demo4;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activates JAX-RS to expose REST endpoints.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
}
