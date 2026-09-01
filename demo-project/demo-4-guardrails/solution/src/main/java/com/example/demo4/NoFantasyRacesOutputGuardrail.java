package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.Set;

/**
 * Output guardrail that rejects generated songs mentioning fantasy races.
 * Ensures the LLM did not slip dwarves, elves, halflings, or dragons into the Viking song.
 */
@ApplicationScoped
@Named("fantasy-output")
public class NoFantasyRacesOutputGuardrail implements OutputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "dwarf", "dwarves", "elf", "elves", "halfling", "halflings", "dragon", "dragons"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String text = responseFromLLM.text().toLowerCase();

        for (String word : FORBIDDEN_WORDS) {
            if (text.contains(word)) {
                return failure("The skald's song must not mention dwarves, elves, halflings, or dragons! This is a pure Viking saga.");
            }
        }
        return success();
    }
}
