package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.cdi.spi.RegisterSupervisorAgent;
import dev.langchain4j.service.V;

public interface StyleReviewSupervisor extends AgenticScopeAccess {

//     @SupervisorRequest
//     static String buildRequest(@V("story") String story, @V("style") String style) {
//         return "Evaluate and if necessary refine the following story to better match the '" + style + "' style:\n\n" + story;
//     }

    ResultWithAgenticScope<String> refineStory(@V("story") String story, @V("style") String style);
}
