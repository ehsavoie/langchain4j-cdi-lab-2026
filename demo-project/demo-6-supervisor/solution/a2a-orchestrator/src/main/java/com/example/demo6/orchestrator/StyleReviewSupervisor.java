package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.declarative.SupervisorRequest;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.cdi.spi.RegisterSupervisorAgent;
import dev.langchain4j.service.V;

@RegisterSupervisorAgent(
        name = "style-review-supervisor",
        subAgentNames = {"style-scorer", "style-editor"},
        chatModelName = "ollama",
        maxAgentsInvocations = 10,
        supervisorContext = """
                You are a Viking saga quality judge overseeing a story refinement process.
                Use the style-scorer to evaluate how well the story matches the requested style (score 0.0–1.0).
                Use the style-editor to rewrite the story if the score is below 0.8.
                Repeat score → edit cycles until the score reaches 0.8 or you have made 5 refinements.
                """,
        supervisorResponseStrategy = SupervisorResponseStrategy.LAST)
public interface StyleReviewSupervisor extends AgenticScopeAccess {

    @SupervisorRequest
    static String buildRequest(@V("story") String story, @V("style") String style) {
        return "Evaluate and if necessary refine the following story to better match the '" + style + "' style:\n\n" + story;
    }

    ResultWithAgenticScope<String> refineStory(@V("story") String story, @V("style") String style);
}
