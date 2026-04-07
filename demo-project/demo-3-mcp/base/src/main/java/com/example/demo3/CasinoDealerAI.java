package com.example.demo3;

// TODO: Import the necessary LangChain4j annotations
// import dev.langchain4j.cdi.spi.RegisterAIService;
// import dev.langchain4j.service.SystemMessage;
// import dev.langchain4j.service.UserMessage;

/**
 * TODO: Agent IA qui anime un jeu de Craps aux dés dans un casino de Vegas.
 *
 * À compléter :
 * 1. Annoter avec @RegisterAIService(chatModelName = "mistral", toolProviderName = "mcp")
 * 2. Définir la méthode play() avec @SystemMessage et @UserMessage
 * 3. Le @SystemMessage doit décrire le rôle du croupier et les règles du Craps
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
// TODO: Ajouter l'annotation @RegisterAIService avec chatModelName et toolProviderName
public interface CasinoDealerAI {

    // TODO: Ajouter l'annotation @SystemMessage avec le prompt du croupier de casino
    // Indice : Lucky Jack Diamond, "The Golden Ace Casino", règles du Craps
    // IMPORTANT : Chaque lancer doit être affiché avec le format :
    // DÉS: [X, Y]
    // TOTAL: [somme]
    // RÉSULTAT: [ce qui s'est passé]
    // Il lance avec roll(numberOfDice=2)
    String play(/* TODO: Ajouter l'annotation @UserMessage */ String playerAction);
}
