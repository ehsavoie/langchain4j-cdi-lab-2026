package com.example.demo2;

import dev.langchain4j.data.message.*;
import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoint that exposes the ChatAssistant.
 * Manages the session identifier for conversational memory.
 */
@Path("/chat")
@ApplicationScoped
public class ChatResource {

    @Inject
    ChatAssistant assistant;

    @Inject
    ChatMemoryProviderBean memoryProvider;

    /**
     * POST /api/chat - Sends a message to the assistant.
     * The X-Session-Id header identifies the conversation (one per browser tab).
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String chat(@HeaderParam("X-Session-Id") String sessionId, String message) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }
        return assistant.chat(sessionId, message);
    }

    /**
     * GET /api/chat/memory - Dumps the memory content for a session.
     * Useful for debugging and showing in demo that memory works.
     */
    @GET
    @Path("/memory")
    @Produces(MediaType.APPLICATION_JSON)
    public String memory(@QueryParam("sessionId") String sessionId) {
        JsonObjectBuilder root = Json.createObjectBuilder()
                .add("sessionCount", memoryProvider.getSessionCount());

        if (sessionId == null || sessionId.isBlank()) {
            return root.build().toString();
        }

        root.add("sessionId", sessionId.substring(0, 8));
        List<ChatMessage> messages = memoryProvider.getMessages(sessionId);
        JsonArrayBuilder arr = Json.createArrayBuilder();
        int i = 0;
        for (ChatMessage m : messages) {
            JsonObjectBuilder msg = Json.createObjectBuilder()
                    .add("index", i++)
                    .add("type", m.type().toString())
                    .add("content", formatMessage(m));
            if (m instanceof ToolExecutionResultMessage tr) {
                msg.add("toolName", tr.toolName());
            }
            arr.add(msg);
        }
        root.add("messageCount", messages.size());
        root.add("messages", arr);
        return root.build().toString();
    }

    private String formatMessage(ChatMessage m) {
        if (m instanceof SystemMessage sm) return sm.text();
        if (m instanceof UserMessage um) return um.singleText();
        if (m instanceof AiMessage ai) return ai.text() != null ? ai.text() : "(tool calls)";
        if (m instanceof ToolExecutionResultMessage tr) return tr.toolName() + " → " + tr.text();
        return m.toString();
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
