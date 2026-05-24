package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.spi.RegisterA2AAgent;
import dev.langchain4j.service.V;

@RegisterA2AAgent(
        name = "creative-writer",
        a2aServerUrl = "${a2a.creative-writer.url}",
        outputKey = "story",
        description = "Generate a Norse saga based on the given topic")
public interface A2ACreativeWriter {

    String generateStory(@V("topic") String topic);
}
