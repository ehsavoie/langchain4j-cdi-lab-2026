package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

/**
 * Guardrail d'entrée qui rejette les requêtes mentionnant des races fantastiques (nains ou elfes).
 * Les Vikings ne connaissent ni les nains ni les elfes !
 */
@ApplicationScoped
public class NoFantasyRacesInputGuardrail implements InputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "nain", "nains", "elfe", "elf", "elfes"
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        // TODO: Get the user message text with userMessage.singleText()
        // TODO: Check if it contains any FORBIDDEN_WORDS (case-insensitive)
        // TODO: Return failure("Les Vikings ne connaissent ni les nains ni les elfes ! Gardez votre requête purement nordique.") if a forbidden word is found
        // TODO: Return success() otherwise
        return success();
    }
}
