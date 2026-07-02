package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.spi.RegisterLoopAgent;
import dev.langchain4j.service.V;

// TODO - Étape 4c : Annoter cette interface avec @RegisterLoopAgent.
// Cette annotation remplace entièrement le bloc AgenticServices.loopBuilder() dans OrchestratorService.
// exitConditionName référence le bean @Named("scoreExitCondition") créé à l'étape 4c.
// subAgentNames liste les agents exécutés à chaque itération (scorer puis editor).
//
// @RegisterLoopAgent(
//         name = "style-review-loop",
//         subAgentNames = {"style-scorer", "style-editor"},
//         maxIterations = 5,
//         exitConditionName = "scoreExitCondition",
//         exitConditionDescription = "Exit when the story score reaches 0.8 or higher")
public interface StyleReviewLoop {

    String refineStory(@V("story") String story, @V("style") String style);
}
