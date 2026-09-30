package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import dev.langchain4j.guardrail.InputGuardrailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class NoFantasyRacesInputGuardrailTest {

    private NoFantasyRacesInputGuardrail guardrail;

    @BeforeEach
    void setUp() {
        guardrail = new NoFantasyRacesInputGuardrail();
    }

    @Test
    void normalNorseRequestIsAccepted() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Sing the exploits of Erik the Red and his Viking warriors"));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"dwarf", "dwarves", "halfling", "halflings", "dragon", "dragons"})
    void requestMentioningFantasyCreaturesIsBlocked(String forbiddenWord) {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("A song with " + forbiddenWord + " smiths"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"elf", "elves"})
    void requestMentioningElvesIsBlocked(String forbiddenWord) {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("A song about the " + forbiddenWord + " of the forests"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void checkIsCaseInsensitive() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Sing about DWARVES and DRAGONS"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void forbiddenWordInSentenceIsBlocked() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("I want a song about dwarves!"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void errorMessageMentionsFantasyRaces() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("A song about elves"));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("dwarves") || message.contains("elves") || message.contains("halflings") || message.contains("dragons"),
                "Error message should mention fantasy races: " + message);
    }
}
