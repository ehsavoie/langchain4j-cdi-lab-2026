package com.example.demo5.scorer;

// TODO: Importer les annotations LangChain4j nécessaires
// import dev.langchain4j.cdi.spi.RegisterAIService;
// import dev.langchain4j.service.UserMessage;
// import dev.langchain4j.service.V;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Agent IA "Style Scorer" qui évalue le style d'une histoire.
 *
 * TODO: À compléter :
 * 1. Annoter l'interface avec @RegisterAIService(chatModelName = "ollama", scope = ApplicationScoped.class)
 * 2. Ajouter @UserMessage avec le prompt d'évaluation de style
 * 3. Utiliser @V("story") et @V("style") pour injecter les paramètres dans le template
 */
public interface StyleScorer {

    // TODO: Ajouter l'annotation @UserMessage avec le prompt suivant :
    // """
    // You are a critical reviewer.
    // Give a review score between 0.0 and 1.0 for the following story based on how well it aligns with the style '{{style}}'.
    // Return only the score and nothing else.
    //
    // The story is: "{{story}}"
    // """
    double scoreStyle(/* TODO: @V("story") */ String story, /* TODO: @V("style") */ String style);
}
