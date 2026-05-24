package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.spi.RegisterLoopAgent;
import dev.langchain4j.service.V;

@RegisterLoopAgent(
        name = "style-review-loop",
        subAgentNames = {"style-scorer", "style-editor"},
        maxIterations = 5,
        exitConditionName = "scoreExitCondition",
        exitConditionDescription = "Exit when the story score reaches 0.8 or higher")
public interface StyleReviewLoop {

    String refineStory(@V("story") String story, @V("style") String style);
}
