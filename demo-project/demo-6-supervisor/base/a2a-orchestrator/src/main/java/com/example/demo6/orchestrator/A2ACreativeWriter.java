package com.example.demo6.orchestrator;

import dev.langchain4j.cdi.spi.RegisterA2AAgent;
import dev.langchain4j.service.V;

// TODO - Étape 1 : Annoter cette interface avec @RegisterA2AAgent.
// LangChain4j-CDI créera automatiquement le proxy CDI qui appelle l'agent A2A distant.
// La propriété a2aServerUrl est résolue via MicroProfile Config (microprofile-config.properties).
//
// @RegisterA2AAgent(
//         name = "creative-writer",
//         a2aServerUrl = "${a2a.creative-writer.url}",
//         outputKey = "story",
//         description = "Generate a Norse saga based on the given topic")
public interface A2ACreativeWriter {

    String generateStory(@V("topic") String topic);
}
