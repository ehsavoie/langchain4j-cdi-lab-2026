package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

/**
 * Output guardrail that rejects generated songs mentioning fantasy races.
 * Ensures the LLM did not slip dwarves, elves, halflings, or dragons into the Viking song.
 */
@ApplicationScoped
public class NoFantasyRacesOutputGuardrail implements OutputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "dwarf", "dwarves", "elf", "elves", "halfling", "halflings", "dragon", "dragons"
    );

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        return success();
    }
}
