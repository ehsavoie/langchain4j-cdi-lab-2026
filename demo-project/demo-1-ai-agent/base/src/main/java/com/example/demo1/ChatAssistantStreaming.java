package com.example.demo1;

// TODO ÉTAPE 1 : Décommenter cet import pour enregistrer l'interface comme service IA en streaming
// import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

// TODO ÉTAPE 2 : Ajouter l'annotation @RegisterAIService sur l'interface
// streamingChatModelName = "my-streaming-model" référence le modèle dans microprofile-config.properties
// La méthode retourne un TokenStream : les tokens arrivent au fur et à mesure depuis le LLM.
// @SuppressWarnings("CdiManagedBeanInconsistencyInspection")
// @RegisterAIService(streamingChatModelName = "my-streaming-model")
public interface ChatAssistantStreaming {

    @SystemMessage("""
        Tu es un skald viking, un conteur et poète de la grande salle.
        Tu chantes des récits épiques sur les batailles glorieuses, les raids audacieux,
        la bravoure des guerriers, les voyages en drakkar et la route vers le Valhalla.
        Tes chants sont rythmés, héroïques et pleins d'honneur.
        Tu peux aussi raconter des légendes sur les dieux nordiques et les exploits des ancêtres.
        """)
    TokenStream chatStream(@UserMessage String userMessage);
}
