package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.example.demo5.orchestrator.Agents.A2ACreativeWriter;
import com.example.demo5.orchestrator.Agents.A2AStyleScorer;
import com.example.demo5.orchestrator.Agents.StyleEditor;
import com.example.demo5.orchestrator.Agents.StyledWriter;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class OrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorService.class);
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
                    LOGGER.info("The score of the story " + scope.readState("story") + " is " + raw);
                    if (raw == null) {
                        return false;
                    }
                    if (raw instanceof Number n) {
                        return n.doubleValue() >= 0.8;
                    }
                    try { return Double.parseDouble(raw.toString()) >= 0.8; }
                    catch (NumberFormatException e) { return false; }
                })
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
