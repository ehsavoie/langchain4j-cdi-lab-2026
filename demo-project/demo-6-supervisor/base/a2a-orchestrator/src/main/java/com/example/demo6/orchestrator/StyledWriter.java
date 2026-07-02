package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.cdi.spi.RegisterSequenceAgent;
import dev.langchain4j.service.V;

// TODO - Étape 5 : Annoter cette interface avec @RegisterSequenceAgent.
// Le pipeline exécute d'abord creative-writer (écrit scope["story"]),
// puis style-review-supervisor (lit scope["story"] + scope["style"] via @SupervisorRequest).
//
// @RegisterSequenceAgent(
//         name = "styled-writer",
//         subAgentNames = {"creative-writer", "style-review-supervisor"})
public interface StyledWriter extends AgenticScopeAccess {

    ResultWithAgenticScope<String> writeStoryWithStyle(
            @V("topic") String topic, @V("style") String style);
}
