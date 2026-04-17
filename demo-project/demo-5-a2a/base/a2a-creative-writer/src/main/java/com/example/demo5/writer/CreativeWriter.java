package com.example.demo5.writer;

// TODO: Importer les annotations LangChain4j nécessaires
// import dev.langchain4j.cdi.spi.RegisterAIService;
// import dev.langchain4j.service.UserMessage;
// import dev.langchain4j.service.V;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Agent IA "Creative Writer" qui génère des histoires courtes.
 *
 * TODO: À compléter :
 * 1. Annoter l'interface avec @RegisterAIService(chatModelName = "ollama", scope = ApplicationScoped.class)
 * 2. Ajouter @UserMessage avec le prompt de génération d'histoire
 * 3. Utiliser @V("topic") pour injecter le sujet dans le template
 */
public interface CreativeWriter {

    // TODO: Ajouter l'annotation @UserMessage avec le prompt suivant :
    // """
    // You are a creative writer.
    // Generate a draft of a story long no more than 3 sentence around the given topic.
    // Return only the story and nothing else.
    // The topic is {{topic}}.
    // """
    String generateStory(/* TODO: @V("topic") */ String topic);
}
