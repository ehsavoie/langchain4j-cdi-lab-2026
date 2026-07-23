package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.model.chat.ChatModel;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import com.example.demo6.orchestrator.Agents.A2ACreativeWriter;
import com.example.demo6.orchestrator.Agents.A2AStyleScorer;
import com.example.demo6.orchestrator.Agents.StyleEditor;
import com.example.demo6.orchestrator.Agents.StyleReviewSupervisor;

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

    private A2ACreativeWriter creativeWriterService;
    private StyleReviewSupervisor supervisorService;

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

        StyleReviewSupervisor supervisor = AgenticServices.supervisorBuilder(StyleReviewSupervisor.class)
                .chatModel(chatModel)
                .subAgents(styleScorer, styleEditor)
                .maxAgentsInvocations(10)
                .supervisorContext("""
                        You are a Viking saga quality judge overseeing a story refinement process.
                        Use the style-scorer to evaluate how well the story matches the requested style (score 0.0–1.0).
                        Use the style-editor to rewrite the story if the score is below 0.8.
                        Repeat score → edit cycles until the score reaches 0.8 or you have made 5 refinements.
                        """)
                .requestGenerator(scope -> {
                    String s = scope.readState("story", "");
                    String st = scope.readState("style", "");
                    return "Evaluate and if necessary refine the following story to better match the '" + st + "' style:\n\n" + s;
                })
                .responseStrategy(SupervisorResponseStrategy.LAST)
                .build();

        this.creativeWriterService = creativeWriter;
        this.supervisorService = supervisor;
    }

    public ResultWithAgenticScope<String> writeStyledStory(String topic, String style) {
        String story = creativeWriterService.generateStory(topic);
        return supervisorService.refineStory(story, style);
    }
}
