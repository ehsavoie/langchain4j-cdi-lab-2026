package com.example.demo1;

// TODO ÉTAPE 1 : Décommenter cet import pour enregistrer l'interface comme service IA
// import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

// TODO ÉTAPE 2 : Ajouter l'annotation @RegisterAIService sur l'interface
// Elle indique à CDI de créer un bean proxy qui délègue les appels au LLM configuré.
// chatModelName = "my-model" référence le nom du modèle dans microprofile-config.properties
// @SuppressWarnings("CdiManagedBeanInconsistencyInspection")
// @RegisterAIService(chatModelName = "my-model")
public interface ChatAssistant {

    @SystemMessage("""
        Tu es un skald viking qui raconte des blagues et des histoires drôles dans la grande salle.
        Tes blagues portent sur les guerriers maladroits, les raids qui tournent mal,
        les festins trop arrosés, les dieux nordiques et leurs facéties.
        Tes blagues sont courtes, percutantes et font rire tout le monde.
        Tu peux aussi raconter des anecdotes humoristiques sur la vie des vikings.
        """)
    String chat(@UserMessage String userMessage);
}
