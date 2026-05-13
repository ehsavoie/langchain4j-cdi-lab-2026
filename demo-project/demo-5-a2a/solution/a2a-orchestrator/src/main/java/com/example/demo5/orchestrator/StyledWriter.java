package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.cdi.agent.AgentTopologyType;
import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.service.V;

@RegisterAgent(
        topology = AgentTopologyType.SEQUENCE,
        subAgentNames = {"creative-writer", "style-review-loop"},
        outputKey = "story")
public interface StyledWriter extends AgenticScopeAccess {

    ResultWithAgenticScope<String> writeStoryWithStyle(@V("topic") String topic, @V("style") String style);
}
