package com.example.demo3;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.memory.ChatMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Une ChatMemory qui conserve les deux derniers échanges de lancer de runes.
 *
 * Le modèle voit toujours :
 *   [SystemMessage]
 *   + [UserMessage précédent] + [AiMessage(appel outil)] + [ToolResult] + [AiMessage(réponse)]
 *   + [UserMessage courant] + ... (en cours)
 *
 * Ceci permet au LLM de comparer le résultat du lancer actuel avec le précédent.
 * L'éviction ne se déclenche que lorsqu'un troisième UserMessage arrive,
 * en supprimant l'échange le plus ancien.
 */
public class LastDiceRollChatMemory implements ChatMemory {

    private static final Logger LOG = Logger.getLogger(LastDiceRollChatMemory.class.getName());

    private final Object id;
    private final List<ChatMessage> messages = new ArrayList<>();

    public LastDiceRollChatMemory(Object id) {
        this.id = id;
        LOG.fine("[mémoire:%s] Créée".formatted(id));
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void add(ChatMessage message) {
        LOG.fine("[mémoire:%s] Ajout du message type=%s".formatted(id, message.type()));
        messages.add(message);
        evict();
    }

    /**
     * Conserve le SystemMessage (si présent) + les deux derniers échanges (lancer précédent + lancer courant).
     * L'éviction ne se déclenche que lorsqu'un troisième UserMessage est présent.
     * Appelé après chaque add() pour toujours faire respecter la fenêtre.
     */
    private void evict() {
        // Récupérer les indices de tous les messages USER
        List<Integer> userIndices = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).type() == ChatMessageType.USER) {
                userIndices.add(i);
            }
        }

        // Garder jusqu'à 2 échanges : rien à évincer avec <= 2 messages utilisateur
        if (userIndices.size() <= 2) {
            return;
        }

        // Couper à partir de l'avant-dernier UserMessage pour que le modèle voie :
        //   échange précédent (lancer N-1) + échange courant (lancer N)
        int keepFromIndex = userIndices.get(userIndices.size() - 2);

        // Trouver le SystemMessage en tête (il n'y en a qu'un au maximum)
        ChatMessage systemMessage = null;
        if (messages.get(0).type() == ChatMessageType.SYSTEM) {
            systemMessage = messages.get(0);
        }

        List<ChatMessage> retained = new ArrayList<>();
        if (systemMessage != null) {
            retained.add(systemMessage);
        }
        retained.addAll(messages.subList(keepFromIndex, messages.size()));

        LOG.fine("[mémoire:%s] Éviction du lancer le plus ancien : %d messages conservés (%d supprimés)"
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
        LOG.fine("[mémoire:%s] Effacée".formatted(id));
        messages.clear();
    }
}
