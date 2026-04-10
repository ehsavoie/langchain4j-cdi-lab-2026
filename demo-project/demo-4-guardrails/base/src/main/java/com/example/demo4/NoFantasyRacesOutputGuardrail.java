package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

/**
 * Guardrail de sortie qui rejette les chants générés mentionnant des races fantastiques.
 * Garantit que le LLM n'a pas glissé des nains ou des elfes dans le chant viking.
 */
@ApplicationScoped
public class NoFantasyRacesOutputGuardrail implements OutputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "nain", "nains", "elfe", "elf", "elfes"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        // TODO: Get the AI response text with responseFromLLM.text()
        // TODO: Check if it contains any FORBIDDEN_WORDS (case-insensitive)
        // TODO: Return failure("Le chant du skald ne doit pas évoquer les nains ni les elfes ! C'est une saga viking pure.") if a forbidden word is found
        // TODO: Return success() otherwise
        return success();
    }
}
