package com.example.demo5.orchestrator;

// TODO: Importer les annotations nécessaires
// import dev.langchain4j.cdi.agent.AgentTopologyType;
// import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.service.V;

/**
 * Proxy CDI vers l'agent A2A Creative Writer distant.
 *
 * TODO: Annoter l'interface avec @RegisterAgent :
 *   name = "creative-writer"
 *   topology = AgentTopologyType.A2A
 *   a2aServerUrl = "${a2a.creative-writer.url}"   ← lit la config MicroProfile
 *   outputKey = "story"
 *   description = "Generate a Norse saga based on the given topic"
 *
 * Une fois annoté, LangChain4j-CDI crée automatiquement un bean CDI
 * @Named("creative-writer") qui appelle l'agent distant via A2A.
 */
public interface A2ACreativeWriter {

    String generateStory(@V("topic") String topic);
}
