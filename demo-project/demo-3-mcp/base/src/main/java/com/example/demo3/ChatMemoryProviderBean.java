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
 * CDI-managed {@link ChatMemoryProvider} that creates a {@link LastDiceRollChatMemory}
 * per session (indexed by the value passed via {@code @MemoryId}).
 *
 * <p>Named {@code "my-memory"} to be referenced from
 * {@code @RegisterAIService(chatMemoryProviderName = "my-memory")}.
 * Each session keeps the last two cast exchanges, allowing the model
 * to compare the current result with the previous one without accumulating stale history.
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
     * Returns the current message list for a session (for debugging purposes only).
     *
     * @param sessionId the memory identifier used when the session was created
     * @return the messages in memory, or an empty list if the session is unknown
     */
    public List<ChatMessage> getMessages(String sessionId) {
        ChatMemory memory = memories.get(sessionId);
        if (memory == null) return Collections.emptyList();
        return memory.messages();
    }

    /** Returns the number of active sessions in memory. */
    public int getSessionCount() {
        return memories.size();
    }
}
