package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.cdi.spi.RegisterSequenceAgent;
import dev.langchain4j.service.V;

// TODO - Étape 4d : Annoter cette interface avec @RegisterSequenceAgent.
// Cette annotation remplace le bloc AgenticServices.sequenceBuilder() dans OrchestratorService.
// AgenticScopeAccess (extends) permet de lire le scope partagé après exécution
// (ex. score final, story intermédiaire). ResultWithAgenticScope expose les deux.
//
// @RegisterSequenceAgent(
//         subAgentNames = {"creative-writer", "style-review-loop"},
//         outputKey = "story")
public interface StyledWriter extends AgenticScopeAccess {

    ResultWithAgenticScope<String> writeStoryWithStyle(@V("topic") String topic, @V("style") String style);
}
