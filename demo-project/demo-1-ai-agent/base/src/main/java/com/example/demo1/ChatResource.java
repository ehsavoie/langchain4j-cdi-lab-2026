package com.example.demo1;

// TODO ÉTAPE 3 : Supprimer les quatre imports ci-dessous — ils ne sont plus nécessaires avec CDI.
// CDI instancie et injecte ChatAssistant et ChatAssistantStreaming automatiquement.
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;

// TODO ÉTAPE 3 : Décommenter cet import pour l'injection CDI
// import jakarta.inject.Inject;
import dev.langchain4j.service.TokenStream;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/chat")
@ApplicationScoped
public class ChatResource {

    // TODO ÉTAPE 3 : Remplacer ces deux champs par des injections CDI.
    // Une fois ChatAssistant et ChatAssistantStreaming annotés avec @RegisterAIService,
    // CDI peut les injecter directement — aucune instanciation manuelle nécessaire.
    //
    // @Inject
    // ChatAssistant assistant;
    //
    // @Inject
    // ChatAssistantStreaming streamingAssistant;
    private ChatAssistant assistant;
    private ChatAssistantStreaming streamingAssistant;

    // TODO ÉTAPE 3 : Supprimer toute cette méthode @PostConstruct.
    // Avec @Inject, CDI gère le cycle de vie des beans — ils sont disponibles
    // avant le premier appel sans aucune initialisation manuelle.
    @PostConstruct
    void init() {
        MistralAiChatModel model = MistralAiChatModel.builder()
                .apiKey(System.getenv("MISTRAL_API_KEY"))
                .modelName("mistral-small-latest")
                .logRequests(true)
                .logResponses(true)
                .build();

        assistant = AiServices.builder(ChatAssistant.class)
                .chatModel(model)
                .build();

        MistralAiStreamingChatModel streamingModel = MistralAiStreamingChatModel.builder()
                .apiKey(System.getenv("MISTRAL_API_KEY"))
                .modelName("mistral-small-latest")
                .build();

        streamingAssistant = AiServices.builder(ChatAssistantStreaming.class)
                .streamingChatModel(streamingModel)
                .build();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String chat(String message) {
        return assistant.chat(message);
    }

    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void chatStream(@QueryParam("message") String message, @Context SseEventSink sseEventSink, @Context Sse sse) {
        if (message == null || message.trim().isEmpty()) {
            try (sseEventSink) {
                sseEventSink.send(sse.newEvent("error", "Message parameter is required"));
            }
            return;
        }

        try {
            TokenStream tokenStream = streamingAssistant.chatStream(message);

            tokenStream
                .onPartialResponse(token -> {
                    sseEventSink.send(sse.newEvent("token", token.replace("\n", "<br/>")));
                })
                .onCompleteResponse(response -> {
                    sseEventSink.send(sse.newEvent("done", ""));
                    sseEventSink.close();
                })
                .onError(error -> {
                    sseEventSink.send(sse.newEvent("error", error.getMessage()));
                    sseEventSink.close();
                })
                .start();

        } catch (Exception e) {
            try (sseEventSink) {
                sseEventSink.send(sse.newEvent("error", "Error: " + e.getMessage()));
            }
        }
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
