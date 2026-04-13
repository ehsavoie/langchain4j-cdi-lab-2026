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
 * {@link ChatMemoryProvider} géré par CDI qui crée une {@link LastDiceRollChatMemory}
 * par session (indexée par la valeur passée en {@code @MemoryId}).
 *
 * <p>Nommé {@code "my-memory"} pour être référencé depuis
 * {@code @RegisterAIService(chatMemoryProviderName = "my-memory")}.
 * Chaque session conserve les deux derniers échanges de lancer, permettant au modèle
 * de comparer le résultat actuel avec le précédent sans accumuler d'historique obsolète.
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
     * Retourne la liste de messages courante pour une session (à des fins de débogage uniquement).
     *
     * @param sessionId l'identifiant mémoire utilisé lors de la création de la session
     * @return les messages en mémoire, ou une liste vide si la session est inconnue
     */
    public List<ChatMessage> getMessages(String sessionId) {
        ChatMemory memory = memories.get(sessionId);
        if (memory == null) return Collections.emptyList();
        return memory.messages();
    }

    /** Retourne le nombre de sessions actives en mémoire. */
    public int getSessionCount() {
        return memories.size();
    }
}
