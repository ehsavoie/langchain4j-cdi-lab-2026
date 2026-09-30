package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class NoFantasyRacesOutputGuardrailTest {

    private NoFantasyRacesOutputGuardrail guardrail;

    @BeforeEach
    void setUp() {
        guardrail = new NoFantasyRacesOutputGuardrail();
    }

    @Test
    void normalVikingSongIsAccepted() {
        String song = """
                O warriors of the North, raise your axes to the sky!
                Thor guides you, Odin protects you in battle.
                Chorus: Valhalla awaits you, sons of the frozen seas!
                """;

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(song));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"dwarf", "dwarves", "halfling", "halflings", "dragon", "dragons"})
    void songMentioningFantasyCreaturesIsBlocked(String forbiddenWord) {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("The " + forbiddenWord + " forged the swords of the Viking warriors"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"elf", "elves"})
    void songMentioningElvesIsBlocked(String forbiddenWord) {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("The warriors fought the " + forbiddenWord + " of the forest"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void checkIsCaseInsensitive() {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("The DRAGONS and the HALFLINGS allied against the Vikings"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void forbiddenWordInSentenceIsBlocked() {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("A dwarf emerged from the dark mountain"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void errorMessageMentionsFantasyRaces() {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("The elves sang with the warriors"));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("dwarves") || message.contains("elves") || message.contains("halflings") || message.contains("dragons"),
                "Error message should mention fantasy races: " + message);
    }
}
