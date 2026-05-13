package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.cdi.agent.CommonAgentCreator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Producteur CDI pour la boucle de révision de style.
 *
 * La boucle ne peut pas être exprimée par @RegisterAgent (exitCondition est une lambda),
 * elle est donc construite programmatiquement et exposée comme bean @Named.
 *
 * TODO: Compléter styleReviewLoop() :
 *   1. Utiliser CommonAgentCreator.toAgentExecutor() pour convertir les beans CDI injectés
 *   2. Construire AgenticServices.loopBuilder() avec :
 *      - subAgents(styleScorerInstance, styleEditor)
 *      - maxIterations(5)
 *      - exitCondition : score >= 0.8
 */
@ApplicationScoped
public class AgentProducers {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentProducers.class);

    @Inject
    @Named("style-editor")
    StyleEditor styleEditor;

    @Inject
    @Named("style-scorer")
    A2AStyleScorer styleScorerInstance;

    @Produces
    @Named("style-review-loop")
    public UntypedAgent styleReviewLoop() {
        // TODO : Construire la boucle de révision
        // return AgenticServices.loopBuilder()
        //         .subAgents(CommonAgentCreator.toAgentExecutor(styleScorerInstance),
        //                    CommonAgentCreator.toAgentExecutor(styleEditor))
        //         .maxIterations(5)
        //         .exitCondition(scope -> {
        //             Object raw = scope.readState("score");
        //             LOGGER.info("Score : " + raw);
        //             if (raw == null) return false;
        //             if (raw instanceof Number n) return n.doubleValue() >= 0.8;
        //             try { return Double.parseDouble(raw.toString()) >= 0.8; }
        //             catch (NumberFormatException e) { return false; }
        //         })
        //         .build();
        throw new UnsupportedOperationException("TODO: Construire la boucle de révision");
    }
}
