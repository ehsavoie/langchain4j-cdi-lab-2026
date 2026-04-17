package com.example.demo5.scorer;

import io.a2a.server.PublicAgentCard;
import io.a2a.spec.AgentCapabilities;
import io.a2a.spec.AgentCard;
import io.a2a.spec.AgentInterface;
import io.a2a.spec.AgentSkill;
import io.a2a.spec.TransportProtocol;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Producteur CDI de l'AgentCard A2A pour le Style Scorer.
 * L'AgentCard décrit les capacités de l'agent aux autres agents du réseau A2A.
 *
 * TODO: À compléter :
 * 1. Implémenter la méthode agentCard() pour construire et retourner un AgentCard
 * 2. Configurer les capacités (streaming, etc.)
 * 3. Déclarer le skill "style_scorer"
 */
@ApplicationScoped
public class StyleScorerAgentCardProducer {

    @Inject
    @ConfigProperty(name = "a2a.server.url", defaultValue = "http://localhost:8080")
    String serverUrl;

    @Produces
    @PublicAgentCard
    public AgentCard agentCard() {
        // TODO: Construire l'AgentCard avec le Builder :
        // - name: "Style Scorer"
        // - description: "Score a story based on how well it aligns with a given style"
        // - url: serverUrl
        // - version: "1.0.0"
        // - capabilities: streaming=true, pushNotifications=false, stateTransitionHistory=false
        // - defaultInputModes / defaultOutputModes: "text"
        // - skills: un AgentSkill avec id="style_scorer", name="Style Scorer"
        // - protocolVersion: "0.3.0"
        // - preferredTransport: TransportProtocol.JSONRPC
        // - additionalInterfaces: AgentInterface JSONRPC sur serverUrl
        throw new UnsupportedOperationException("TODO: Implémenter la construction de l'AgentCard");
    }
}
