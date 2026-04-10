package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrenchOutputGuardrailTest {

    private FrenchOutputGuardrail guardrail;

    @BeforeEach
    void setUp() {
        guardrail = new FrenchOutputGuardrail();
        guardrail.init();
    }

    @Test
    void frenchSongIsAccepted() {
        String frenchSong = """
                Les guerriers du Nord s'élancent dans la tempête, portés par la fureur d'Odin !
                Leurs drakkars fendent les vagues glacées, vers des terres inconnues et lointaines.
                Refrain : Valhalla nous attend, gloire aux fils du Nord invincibles !
                Avec leurs haches et leurs boucliers, ils affrontent les ennemis sans peur ni regret.
                """;

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(frenchSong));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @Test
    void englishSongIsBlocked() {
        String englishSong = """
                The great warrior of the north sailed his ship across the sea with undying courage.
                With sword and shield he fought, no enemy could stand before his mighty blade.
                Hail the sons of Odin, mighty warriors of old who never feared death in battle!
                Their longships cut through icy waves as they sought glory in distant foreign lands.
                """;

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(englishSong));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void emptySongIsBlocked() {
        // Empty text → Tika cannot be confident → not reasonably confident → failure
        OutputGuardrailResult result = guardrail.validate(AiMessage.from(""));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void failureMessageMentionsFrench() {
        String englishSong = "The great warrior sails the northern sea in search of glory and victory over his enemies across distant lands.";

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(englishSong));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("français"), "Failure message should mention French: " + message);
    }
}
