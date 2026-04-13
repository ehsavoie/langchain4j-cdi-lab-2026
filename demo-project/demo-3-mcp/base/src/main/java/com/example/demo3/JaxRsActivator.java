package com.example.demo3;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Active JAX-RS pour exposer les endpoints REST.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
}
