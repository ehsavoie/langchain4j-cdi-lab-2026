package com.example.demo6.orchestrator;

import dev.langchain4j.cdi.spi.RegisterA2AAgent;
import dev.langchain4j.service.V;

public interface A2ACreativeWriter {

    String generateStory(@V("topic") String topic);
}
