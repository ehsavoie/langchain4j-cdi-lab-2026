package com.example.demo6.orchestrator;

import dev.langchain4j.cdi.spi.RegisterA2AAgent;
import dev.langchain4j.service.V;

public interface A2AStyleScorer {

    double scoreStyle(@V("story") String story, @V("style") String style);
}
