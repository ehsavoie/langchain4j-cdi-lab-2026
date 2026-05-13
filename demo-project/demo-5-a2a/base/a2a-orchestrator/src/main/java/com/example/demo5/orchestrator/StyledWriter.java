package com.example.demo5.orchestrator;

// TODO: Importer les annotations nécessaires
// import dev.langchain4j.cdi.agent.AgentTopologyType;
// import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.V;

/**
 * Pipeline d'orchestration complet : écriture → boucle de révision de style.
 *
 * TODO: Annoter l'interface avec @RegisterAgent :
 *   topology = AgentTopologyType.SEQUENCE
 *   subAgentNames = {"creative-writer", "style-review-loop"}
 *   outputKey = "story"
 *
 * Le SEQUENCE enchaîne les sous-agents dans l'ordre :
 *   1. "creative-writer" (A2ACreativeWriter, distant)
 *   2. "style-review-loop" (UntypedAgent produit dans AgentProducers)
 *
 * AgenticScopeAccess permet de lire le scope partagé après exécution.
 */
public interface StyledWriter extends AgenticScopeAccess {

    ResultWithAgenticScope<String> writeStoryWithStyle(@V("topic") String topic, @V("style") String style);
}
