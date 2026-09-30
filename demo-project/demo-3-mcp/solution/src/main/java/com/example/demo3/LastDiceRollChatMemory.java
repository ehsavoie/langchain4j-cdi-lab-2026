package com.example.demo3;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.memory.ChatMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A ChatMemory that keeps the last two rune cast exchanges.
 *
 * The model always sees:
 *   [SystemMessage]
 *   + [previous UserMessage] + [AiMessage(tool call)] + [ToolResult] + [AiMessage(response)]
 *   + [current UserMessage] + ... (in progress)
 *
 * This allows the LLM to compare the current cast result with the previous one.
 * Eviction only triggers when a third UserMessage arrives,
 * removing the oldest exchange.
 */
public class LastDiceRollChatMemory implements ChatMemory {

    private static final Logger LOG = Logger.getLogger(LastDiceRollChatMemory.class.getName());

    private final Object id;
    private final List<ChatMessage> messages = new ArrayList<>();

    public LastDiceRollChatMemory(Object id) {
        this.id = id;
        LOG.fine("[memory:%s] Created".formatted(id));
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void add(ChatMessage message) {
        LOG.fine("[memory:%s] Adding message type=%s".formatted(id, message.type()));
        messages.add(message);
        evict();
    }

    /**
     * Keeps the SystemMessage (if present) + the last two exchanges (previous cast + current cast).
     * Eviction only triggers when a third UserMessage is present.
     * Called after each add() to always enforce the window.
     */
    private void evict() {
        // Gather indices of all USER messages
        List<Integer> userIndices = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).type() == ChatMessageType.USER) {
                userIndices.add(i);
            }
        }

        // Keep up to 2 exchanges: nothing to evict with <= 2 user messages
        if (userIndices.size() <= 2) {
            return;
        }

        // Cut from the second-to-last UserMessage so the model sees:
        //   previous exchange (cast N-1) + current exchange (cast N)
        int keepFromIndex = userIndices.get(userIndices.size() - 2);

        // Find the SystemMessage at the head (there is at most one)
        ChatMessage systemMessage = null;
        if (messages.get(0).type() == ChatMessageType.SYSTEM) {
            systemMessage = messages.get(0);
        }

        List<ChatMessage> retained = new ArrayList<>();
        if (systemMessage != null) {
            retained.add(systemMessage);
        }
        retained.addAll(messages.subList(keepFromIndex, messages.size()));

        LOG.fine("[memory:%s] Evicting oldest cast: %d messages kept (%d removed)"
                .formatted(id, retained.size(), messages.size() - retained.size()));
        messages.clear();
        messages.addAll(retained);
    }

    @Override
    public List<ChatMessage> messages() {
        return List.copyOf(messages);
    }

    @Override
    public void clear() {
        LOG.fine("[memory:%s] Cleared".formatted(id));
        messages.clear();
    }
}
