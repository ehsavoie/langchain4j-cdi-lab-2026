package com.example.demo5.orchestrator;

// TODO: Importer les classes LangChain4j Agentic nécessaires
// import dev.langchain4j.agentic.AgenticServices;
// import dev.langchain4j.agentic.UntypedAgent;
// import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;
// TODO: Importer les interfaces d'agents
// import com.example.demo5.orchestrator.Agents.A2ACreativeWriter;
// import com.example.demo5.orchestrator.Agents.A2AStyleScorer;
// import com.example.demo5.orchestrator.Agents.StyleEditor;
// import com.example.demo5.orchestrator.Agents.StyleReviewLoop;
// import com.example.demo5.orchestrator.Agents.StyledWriter;

/**
 * Service d'orchestration qui coordonne les agents A2A.
 *
 * TODO: À compléter dans init() :
 * ÉTAPE 1 : Créer le ChatModel Ollama local (pour le StyleEditor)
 * ÉTAPE 2 : Créer l'agent A2A CreativeWriter via AgenticServices.a2aBuilder()
 * ÉTAPE 3 : Créer l'agent A2A StyleScorer via AgenticServices.a2aBuilder()
 * ÉTAPE 4 : Créer l'agent local StyleEditor via AgenticServices.agentBuilder()
 * ÉTAPE 5 : Créer la boucle de révision via AgenticServices.loopBuilder()
 *           avec exitCondition: score >= 0.8, maxIterations: 5
 * ÉTAPE 6 : Créer la séquence complète via AgenticServices.sequenceBuilder()
 */
@ApplicationScoped
public class OrchestratorService {

    @Inject
    @ConfigProperty(name = "a2a.creative-writer.url", defaultValue = "http://localhost:8080")
    String creativeWriterUrl;

    @Inject
    @ConfigProperty(name = "a2a.style-scorer.url", defaultValue = "http://localhost:8081")
    String styleScorerUrl;

    @Inject
    @ConfigProperty(name = "ollama.base-url", defaultValue = "http://127.0.0.1:11434")
    String ollamaBaseUrl;

    @Inject
    @ConfigProperty(name = "ollama.model-name", defaultValue = "qwen2.5:7b")
    String ollamaModelName;

    // TODO: Déclarer le champ StyledWriter
    // private StyledWriter styledWriter;

    @PostConstruct
    void init() {
        // TODO ÉTAPE 1 : Créer le ChatModel Ollama local
        // ChatModel chatModel = OllamaChatModel.builder()
        //         .baseUrl(ollamaBaseUrl)
        //         .modelName(ollamaModelName)
        //         .timeout(Duration.ofMinutes(10))
        //         .temperature(0.0)
        //         .logRequests(true)
        //         .logResponses(true)
        //         .build();

        // TODO ÉTAPE 2 : Créer l'agent A2A CreativeWriter
        // A2ACreativeWriter creativeWriter = AgenticServices.a2aBuilder(creativeWriterUrl, A2ACreativeWriter.class)
        //         .outputKey("story")
        //         .build();

        // TODO ÉTAPE 3 : Créer l'agent A2A StyleScorer
        // A2AStyleScorer styleScorer = AgenticServices.a2aBuilder(styleScorerUrl, A2AStyleScorer.class)
        //         .outputKey("score")
        //         .build();

        // TODO ÉTAPE 4 : Créer l'agent local StyleEditor
        // StyleEditor styleEditor = AgenticServices.agentBuilder(StyleEditor.class)
        //         .chatModel(chatModel)
        //         .outputKey("story")
        //         .build();

        // TODO ÉTAPE 5 : Créer la boucle de révision (loop)
        // UntypedAgent styleReviewLoop = AgenticServices.loopBuilder()
        //         .subAgents(styleScorer, styleEditor)
        //         .maxIterations(5)
        //         .exitCondition(scope -> scope.readState("score", 0.0) >= 0.8)
        //         .build();

        // TODO ÉTAPE 6 : Créer la séquence complète
        // styledWriter = AgenticServices.sequenceBuilder(StyledWriter.class)
        //         .subAgents(creativeWriter, styleReviewLoop)
        //         .outputKey("story")
        //         .build();
    }

    public String writeStyledStory(String topic, String style) {
        // TODO: Appeler styledWriter.writeStoryWithStyle() et retourner le résultat
        // ResultWithAgenticScope<String> result = styledWriter.writeStoryWithStyle(topic, style);
        // return result.result();
        throw new UnsupportedOperationException("TODO: Implémenter l'orchestration");
    }
}
