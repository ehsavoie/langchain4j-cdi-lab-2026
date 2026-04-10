package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.Set;

/**
 * Guardrail de sortie qui rejette les chants générés mentionnant des races fantastiques.
 * Garantit que le LLM n'a pas glissé des nains ou des elfes dans le chant viking.
 */
@ApplicationScoped
@Named(("fantasy-output"))
public class NoFantasyRacesOutputGuardrail implements OutputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "nain", "nains", "elfe", "elf", "elfes"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String text = responseFromLLM.text().toLowerCase();

        for (String word : FORBIDDEN_WORDS) {
            if (text.contains(word)) {
                return failure("Le chant du skald ne doit pas évoquer les nains ni les elfes ! C'est une saga viking pure.");
            }
        }
        return success();
    }
}
