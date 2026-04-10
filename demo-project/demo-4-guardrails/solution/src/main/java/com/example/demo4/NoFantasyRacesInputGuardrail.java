package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.Set;

/**
 * Guardrail d'entrée qui rejette les requêtes mentionnant des races fantastiques (nains ou elfes).
 * Les Vikings ne connaissent ni les nains ni les elfes !
 */
@ApplicationScoped
@Named("fantasy-input")
public class NoFantasyRacesInputGuardrail implements InputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "nain", "nains", "elfe", "elf", "elfes"
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText().toLowerCase();

        for (String word : FORBIDDEN_WORDS) {
            if (text.contains(word)) {
                return failure("Les Vikings ne connaissent ni les nains ni les elfes ! Gardez votre requête purement nordique.");
            }
        }
        return success();
    }
}
