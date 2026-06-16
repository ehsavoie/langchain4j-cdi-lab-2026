package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.agentic.supervisor.SupervisorResponseStrategy;
import dev.langchain4j.cdi.spi.RegisterSupervisorAgent;
import dev.langchain4j.service.V;

// TODO - Étape 4 : Annoter cette interface avec @RegisterSupervisorAgent.
// Le superviseur LLM remplace à la fois le loop et la condition de sortie codée en dur.
// Il décide lui-même quand s'arrêter grâce au supervisorContext, en invoquant les sous-agents
// style-scorer et style-editor jusqu'à ce que le score atteigne 0.8 ou après 5 raffinements.
//
// Ajouter "extends AgenticScopeAccess" et changer le type de retour en ResultWithAgenticScope<String>
// pour accéder au scope partagé après exécution (lire story et score).
//
// IMPORTANT : ajouter aussi une méthode statique @SupervisorRequest pour que le planificateur LLM
// reçoive le contexte complet (story + style). Sans elle, le planificateur voit une requête vide.
//
// @RegisterSupervisorAgent(
//         name = "style-review-supervisor",
//         subAgentNames = {"style-scorer", "style-editor"},
//         chatModelName = "ollama",
//         maxAgentsInvocations = 10,
//         supervisorContext = """
//                 You are a Viking saga quality judge overseeing a story refinement process.
//                 Use the style-scorer to evaluate how well the story matches the requested style (score 0.0–1.0).
//                 Use the style-editor to rewrite the story if the score is below 0.8.
//                 Repeat score → edit cycles until the score reaches 0.8 or you have made 5 refinements.
//                 """,
//         supervisorResponseStrategy = SupervisorResponseStrategy.LAST)
public interface StyleReviewSupervisor extends AgenticScopeAccess {

//     @SupervisorRequest
//     static String buildRequest(@V("story") String story, @V("style") String style) {
//         return "Evaluate and if necessary refine the following story to better match the '" + style + "' style:\n\n" + story;
//     }

    ResultWithAgenticScope<String> refineStory(@V("story") String story, @V("style") String style);
}
