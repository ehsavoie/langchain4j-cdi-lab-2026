package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.agent.AgentTopologyType;
import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.service.V;

@RegisterAgent(
        name = "style-scorer",
        topology = AgentTopologyType.A2A,
        a2aServerUrl = "${a2a.style-scorer.url}",
        outputKey = "score",
        description = "Score a saga based on how well it captures a given style")
public interface A2AStyleScorer {

    double scoreStyle(@V("story") String story, @V("style") String style);
}
