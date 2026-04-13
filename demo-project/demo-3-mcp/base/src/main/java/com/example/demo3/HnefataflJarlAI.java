package com.example.demo3;

// TODO: Importer les annotations LangChain4j nécessaires
// import dev.langchain4j.cdi.spi.RegisterAIService;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * TODO: Agent IA qui anime un jeu de Hnefatafl au Grand Thing des guerriers du Nord.
 *
 * À compléter :
 * 1. Annoter avec @RegisterAIService(chatModelName = "mistral", toolProviderName = "mcp")
 * 2. Définir la méthode play() avec @SystemMessage et @UserMessage
 * 3. Le @SystemMessage doit décrire le rôle du Jarl et les règles du Hnefatafl
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
// TODO: Ajouter l'annotation @RegisterAIService avec chatModelName et toolProviderName
public interface HnefataflJarlAI {

    // TODO: Ajouter l'annotation @SystemMessage avec le prompt du Jarl du Thing
    // Indice : Ragnar le Skald, "Le Grand Thing", règles du Hnefatafl
    // IMPORTANT : Chaque lancer doit être affiché avec le format :
    // RUNES: [X, Y]
    // TOTAL: [somme]
    // DESTIN: [ce qui s'est passé]
    // Il lance avec roll(numberOfDice=2)
    String play(/* TODO: Ajouter l'annotation @UserMessage */ String playerAction);
}
