package com.example.demo1;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * JAX-RS Application activation point.
 * All REST endpoints will be available under /api path.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
}
