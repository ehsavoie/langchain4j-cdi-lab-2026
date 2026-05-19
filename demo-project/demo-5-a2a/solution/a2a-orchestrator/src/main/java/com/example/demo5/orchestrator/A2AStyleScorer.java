package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.spi.RegisterA2AAgent;
import dev.langchain4j.service.V;

@RegisterA2AAgent(
        name = "style-scorer",
        a2aServerUrl = "${a2a.style-scorer.url}",
        outputKey = "score",
        description = "Score a saga based on how well it captures a given style")
public interface A2AStyleScorer {

    double scoreStyle(@V("story") String story, @V("style") String style);
}
