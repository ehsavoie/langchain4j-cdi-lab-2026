package com.example.demo3;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Activateur JAX-RS qui enregistre l'application sous le chemin de base {@code /api}.
 *
 * <p>Toutes les ressources REST (ex. {@link GameResource}) sont automatiquement découvertes
 * par le conteneur CDI et exposées sous {@code /api}.
 */
@ApplicationPath("/api")
public class JaxRsActivator extends Application {
}
