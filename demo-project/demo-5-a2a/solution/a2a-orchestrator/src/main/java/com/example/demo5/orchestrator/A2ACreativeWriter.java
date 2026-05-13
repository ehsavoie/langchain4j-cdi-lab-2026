package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.agent.AgentTopologyType;
import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.service.V;

@RegisterAgent(
        name = "creative-writer",
        topology = AgentTopologyType.A2A,
        a2aServerUrl = "${a2a.creative-writer.url}",
        outputKey = "story",
        description = "Generate a Norse saga based on the given topic")
public interface A2ACreativeWriter {

    String generateStory(@V("topic") String topic);
}
