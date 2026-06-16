package com.example.demo6.orchestrator;

import dev.langchain4j.cdi.spi.RegisterA2AAgent;
import dev.langchain4j.service.V;

// TODO - Étape 2 : Annoter cette interface avec @RegisterA2AAgent.
// outputKey "score" permet au scope partagé de stocker la valeur retournée.
// Le superviseur (Étape 4) lit scope["score"] via @SupervisorRequest pour décider quand s'arrêter.
//
// @RegisterA2AAgent(
//         name = "style-scorer",
//         a2aServerUrl = "${a2a.style-scorer.url}",
//         outputKey = "score",
//         description = "Score a saga based on how well it captures a given style")
public interface A2AStyleScorer {

    double scoreStyle(@V("story") String story, @V("style") String style);
}
