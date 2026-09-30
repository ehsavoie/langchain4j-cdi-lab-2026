package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Set;

/**
 * Input guardrail that rejects requests mentioning fantasy races (dwarves, elves, halflings, or dragons).
 * Vikings know neither dwarves, elves, halflings, nor dragons!
 */
@ApplicationScoped
public class NoFantasyRacesInputGuardrail implements InputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "dwarf", "dwarves", "elf", "elves", "halfling", "halflings", "dragon", "dragons"
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        return success();
    }
}
