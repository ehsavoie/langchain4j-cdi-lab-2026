package com.example.demo5.orchestrator;

// TODO: Importer les annotations nécessaires
// import dev.langchain4j.cdi.agent.AgentTopologyType;
// import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.service.V;

/**
 * Proxy CDI vers l'agent A2A Style Scorer distant.
 *
 * TODO: Annoter l'interface avec @RegisterAgent :
 *   name = "style-scorer"
 *   topology = AgentTopologyType.A2A
 *   a2aServerUrl = "${a2a.style-scorer.url}"   ← lit la config MicroProfile
 *   outputKey = "score"
 *   description = "Score a saga based on how well it captures a given style"
 *
 * Une fois annoté, LangChain4j-CDI crée automatiquement un bean CDI
 * @Named("style-scorer") qui appelle l'agent distant via A2A.
 */
public interface A2AStyleScorer {

    double scoreStyle(@V("story") String story, @V("style") String style);
}
