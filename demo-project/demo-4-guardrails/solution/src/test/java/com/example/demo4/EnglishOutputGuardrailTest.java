package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnglishOutputGuardrailTest {

    private EnglishOutputGuardrail guardrail;

    @BeforeEach
    void setUp() {
        guardrail = new EnglishOutputGuardrail();
        guardrail.init();
    }

    @Test
    void englishSongIsAccepted() {
        String englishSong = """
                The great warrior of the north sailed his ship across the sea with undying courage.
                With sword and shield he fought, no enemy could stand before his mighty blade.
                Hail the sons of Odin, mighty warriors of old who never feared death in battle!
                Their longships cut through icy waves as they sought glory in distant foreign lands.
                """;

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(englishSong));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @Test
    void frenchSongIsBlocked() {
        String frenchSong = """
                Les guerriers du Nord s'élancent dans la tempête, portés par la fureur d'Odin !
                Leurs drakkars fendent les vagues glacées, vers des terres inconnues et lointaines.
                Refrain : Valhalla nous attend, gloire aux fils du Nord invincibles !
                Avec leurs haches et leurs boucliers, ils affrontent les ennemis sans peur ni regret.
                """;

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(frenchSong));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void emptySongIsBlocked() {
        OutputGuardrailResult result = guardrail.validate(AiMessage.from(""));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void failureMessageMentionsEnglish() {
        String frenchSong = "Les guerriers du Nord s'élancent dans la tempête portés par la fureur d'Odin vers des terres inconnues et lointaines.";

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(frenchSong));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("English"), "Failure message should mention English: " + message);
    }
}
