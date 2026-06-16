package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class OrchestratorService {

    @Inject
    StyledWriter styledWriter;

    public ResultWithAgenticScope<String> writeStyledStory(String topic, String style) {
        return styledWriter.writeStoryWithStyle(topic, style);
    }
}
