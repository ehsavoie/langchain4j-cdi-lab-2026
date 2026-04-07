package com.example.demo1;

import dev.langchain4j.service.TokenStream;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

@Path("/chat")
@ApplicationScoped
public class ChatResource {

    @Inject
    ChatAssistant assistant;

    @Inject
    ChatAssistantStreaming streamingAssistant;

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
                    // Send each token as an SSE event
                    sseEventSink.send(sse.newEvent("token", token.replace("\n","<br/>")));
                })
                .onCompleteResponse(response -> {
                    // Send completion event
                    sseEventSink.send(sse.newEvent("done", ""));
                    sseEventSink.close();
                })
                .onError(error -> {
                    // Send error event
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
