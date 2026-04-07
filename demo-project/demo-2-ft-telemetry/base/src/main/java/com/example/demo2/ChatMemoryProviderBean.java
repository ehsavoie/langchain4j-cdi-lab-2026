package com.example.demo2;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a ChatMemory per session (identified by @MemoryId).
 * Each browser tab has its own conversation history.
 * Keeps a reference to allow inspection (debug).
 */
@ApplicationScoped
@Named("my-memory")
public class ChatMemoryProviderBean implements ChatMemoryProvider {

    private final Map<Object, ChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public ChatMemory get(Object memoryId) {
        return memories.computeIfAbsent(memoryId, id ->
                MessageWindowChatMemory.builder()
                        .id(id)
                        .maxMessages(20)
                        .build()
        );
    }

    /**
     * Returns the messages for a session for debug/inspection.
     */
    public List<ChatMessage> getMessages(String sessionId) {
        ChatMemory memory = memories.get(sessionId);
        if (memory == null) return Collections.emptyList();
        return memory.messages();
    }

    public int getSessionCount() {
        return memories.size();
    }
}
