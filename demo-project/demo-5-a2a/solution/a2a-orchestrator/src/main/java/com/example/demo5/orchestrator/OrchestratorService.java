package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.example.demo5.orchestrator.Agents.A2ACreativeWriter;
import com.example.demo5.orchestrator.Agents.A2AStyleScorer;
import com.example.demo5.orchestrator.Agents.StyleEditor;
import com.example.demo5.orchestrator.Agents.StyleReviewLoop;
import com.example.demo5.orchestrator.Agents.StyledWriter;

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

    private StyledWriter styledWriter;

    @PostConstruct
    void init() {
        ChatModel chatModel = OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(ollamaModelName)
                .timeout(Duration.ofMinutes(10))
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .build();

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
                .exitCondition(scope -> scope.readState("score", 0.0) >= 0.8)
                .build();

        styledWriter = AgenticServices.sequenceBuilder(StyledWriter.class)
                .subAgents(creativeWriter, styleReviewLoop)
                .outputKey("story")
                .build();
    }

    public ResultWithAgenticScope<String> writeStyledStory(String topic, String style) {
        return styledWriter.writeStoryWithStyle(topic, style);
    }
}
