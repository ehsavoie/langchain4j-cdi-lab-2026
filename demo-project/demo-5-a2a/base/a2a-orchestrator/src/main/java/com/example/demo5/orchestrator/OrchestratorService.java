package com.example.demo5.orchestrator;

// TODO: Importer les classes nécessaires
// import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Service d'orchestration qui délègue au pipeline StyledWriter.
 *
 * Avec @RegisterAgent, LangChain4j-CDI crée automatiquement le bean CDI StyledWriter.
 * Il suffit de l'injecter — plus besoin de @PostConstruct ni de builders manuels.
 *
 * TODO: À compléter :
 * 1. Injecter StyledWriter avec @Inject
 * 2. Appeler styledWriter.writeStoryWithStyle(topic, style)
 * 3. Retourner result.result() pour extraire la saga du scope agentic
 */
@ApplicationScoped
public class OrchestratorService {

    // TODO: Injecter StyledWriter
    // @Inject
    // StyledWriter styledWriter;

    public String writeStyledStory(String topic, String style) {
        // TODO: Appeler le pipeline et retourner le résultat
        // ResultWithAgenticScope<String> result = styledWriter.writeStoryWithStyle(topic, style);
        // return result.result();
        throw new UnsupportedOperationException("TODO: Implémenter l'orchestration");
    }
}
