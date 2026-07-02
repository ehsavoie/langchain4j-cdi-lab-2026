package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.cdi.spi.RegisterSequenceAgent;
import dev.langchain4j.service.V;

@RegisterSequenceAgent(
        name = "styled-writer",
        subAgentNames = {"creative-writer", "style-review-supervisor"})
public interface StyledWriter extends AgenticScopeAccess {

    ResultWithAgenticScope<String> writeStoryWithStyle(
            @V("topic") String topic, @V("style") String style);
}
