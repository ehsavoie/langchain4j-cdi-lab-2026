package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.Set;

/**
 * Input guardrail that rejects requests mentioning fantasy races (dwarves, elves, halflings, or dragons).
 * Vikings know neither dwarves, elves, halflings, nor dragons!
 */
@ApplicationScoped
@Named("fantasy-input")
public class NoFantasyRacesInputGuardrail implements InputGuardrail {

    private static final Set<String> FORBIDDEN_WORDS = Set.of(
            "dwarf", "dwarves", "elf", "elves", "halfling", "halflings", "dragon", "dragons"
    );

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText().toLowerCase();

        for (String word : FORBIDDEN_WORDS) {
            if (text.contains(word)) {
                return failure("Vikings know neither dwarves, elves, halflings, nor dragons! Keep your request purely Norse.");
            }
        }
        return success();
    }
}
