package com.example.demo2;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * AI Service for the Viking expedition assistant.
 * The agent already works with Tools + Memory -- now we add resilience!
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(chatModelName = "my-model",
                   chatMemoryProviderName = "my-memory",
                   contentRetrieverName = "my-rag",
                   tools = ExpeditionTools.class)
public interface ChatAssistant {

    // TODO STEP 1: Add @Retry(maxRetries = 3, delay = 1000)
    // TODO STEP 2: Add @Timeout(value = 30, unit = ChronoUnit.SECONDS)
    // TODO STEP 3: Add @Fallback(fallbackMethod = "chatFallback")
    // TODO STEP 4: Add @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)
    @SystemMessage("""
        Tu es l'assistant des expéditions vikings.
        Tu as accès à une base de connaissances sur les expéditions, les chefs et les destinations.
        Utilise-la pour répondre aux questions sur les détails des expéditions, les exigences et les chefs.

        IMPORTANT -- UTILISATION OBLIGATOIRE DES OUTILS :
        Tu DOIS appeler les outils pour CHAQUE action. Ne simule JAMAIS une action.
        - Pour lister les expéditions : appelle listExpeditions.
        - Pour inscrire un guerrier : appelle enrollWarrior. Ne dis JAMAIS "inscription confirmée" sans avoir appelé enrollWarrior.
        - Pour annuler une inscription : appelle cancelEnrollment.
        - Pour les places restantes : appelle remainingSlots.
        - Pour voir les inscriptions : appelle myEnrollments.
        Si tu n'appelles pas l'outil, l'action NE S'EST PAS produite.

        RÈGLES :
        - Pour inscrire un guerrier, tu as besoin de son prénom ET de son nom de famille.
          Si l'un des deux manque, demande-le.
        - N'affiche PAS les identifiants techniques (raid-angleterre, etc.) à l'utilisateur.
          Utilise-les en interne lors de l'appel des outils.
        - Réponds en français, sois concis.
        """)
    String chat(@MemoryId String sessionId, @UserMessage String message);

    // TODO STEP 5: Implement the fallback
    // default String chatFallback(String sessionId, String message) {
    //     return "Oops! The LLM is taking a nap. Please try again in a moment.";
    // }
}
