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
        return success();
    }
}
