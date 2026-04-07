package com.example.demo3;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CDI-managed {@link ChatMemoryProvider} that creates one {@link LastDiceRollChatMemory}
 * per session (keyed by the value passed as {@code @MemoryId}).
 *
 * <p>Named {@code "my-memory"} so it can be referenced from
 * {@code @RegisterAIService(chatMemoryProviderName = "my-memory")}.
 * Each session retains the two most recent roll exchanges, letting the model
 * compare the current result with the previous one without accumulating stale history.
 */
@ApplicationScoped
@Named("my-memory")
public class ChatMemoryProviderBean implements ChatMemoryProvider {

    private final Map<Object, ChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public ChatMemory get(Object memoryId) {
        return memories.computeIfAbsent(memoryId, LastDiceRollChatMemory::new);
    }

    /**
     * Returns the current message list for a session (for debug/inspection only).
     *
     * @param sessionId the memory ID used when the session was created
     * @return the messages held in memory, or an empty list if the session is unknown
     */
    public List<ChatMessage> getMessages(String sessionId) {
        ChatMemory memory = memories.get(sessionId);
        if (memory == null) return Collections.emptyList();
        return memory.messages();
    }

    /** Returns the number of active sessions held in memory. */
    public int getSessionCount() {
        return memories.size();
    }
}
