package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.example.demo5.orchestrator.Agents.A2ACreativeWriter;
import com.example.demo5.orchestrator.Agents.A2AStyleScorer;
import com.example.demo5.orchestrator.Agents.StyleEditor;
import com.example.demo5.orchestrator.Agents.StyledWriter;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;

/**
 * Service d'orchestration qui coordonne les agents A2A.
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
    @Named("ollama")
    ChatModel chatModel;

    private StyledWriter styledWriter;

    // TODO - Étape 4d : Remplacer tout le câblage impératif ci-dessous par un simple @Inject.
    // Grâce aux annotations des étapes 4a–4c, LangChain4j-CDI produit automatiquement tous les beans.
    // Supprimer : creativeWriterUrl, styleScorerUrl, chatModel, styledWriter, et la méthode @PostConstruct.
    // Les remplacer par :
    //
    //   @Inject
    //   StyledWriter styledWriter;
    //
    // Mettre à jour writeStyledStory pour retourner ResultWithAgenticScope<String> directement :
    //
    //   public ResultWithAgenticScope<String> writeStyledStory(String topic, String style) {
    //       return styledWriter.writeStoryWithStyle(topic, style);
    //   }
    @PostConstruct
    void init() {

        A2ACreativeWriter creativeWriter = AgenticServices.a2aBuilder(creativeWriterUrl, A2ACreativeWriter.class)
                .outputKey("story")
                .build();

        A2AStyleScorer styleScorer = AgenticServices.a2aBuilder(styleScorerUrl, A2AStyleScorer.class)
                .outputKey("score")
                .build();

        StyleEditor styleEditor = AgenticServices.agentBuilder(StyleEditor.class)
                .chatModel(chatModel)
                .outputKey("story")
                .build();

        UntypedAgent styleReviewLoop = AgenticServices.loopBuilder()
                .subAgents(styleScorer, styleEditor)
                .maxIterations(5)
                .exitCondition(scope -> {
                    Object raw = scope.readState("score");
                    if (raw == null) {
                        return false;
                    }
                    if (raw instanceof Number n) {
                        return n.doubleValue() >= 0.8;
                    }
                    try {
                        return Double.parseDouble(raw.toString()) >= 0.8;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .build();

        styledWriter = AgenticServices.sequenceBuilder(StyledWriter.class)
                .subAgents(creativeWriter, styleReviewLoop)
                .outputKey("story")
                .build();
    }

    public String writeStyledStory(String topic, String style) {
        ResultWithAgenticScope<String> result = styledWriter.writeStoryWithStyle(topic, style);
        return result.result();
    }
}
