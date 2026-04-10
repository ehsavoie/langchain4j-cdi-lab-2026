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
    void chantVikingNormalEstAccepte() {
        String chant = """
                Ô guerriers du Nord, levez vos haches vers le ciel !
                Thor vous guide, Odin vous protège dans la bataille.
                Refrain : Valhalla vous attend, fils des mers glacées !
                """;

        OutputGuardrailResult result = guardrail.validate(AiMessage.from(chant));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"nain", "nains"})
    void chantMentionnantDesNainsEstBloque(String motInterdit) {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("Les " + motInterdit + " forgèrent les épées des guerriers vikings"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"elfe", "elfes"})
    void chantMentionnantDesElfesEstBloque(String motInterdit) {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("Les guerriers combattirent les " + motInterdit + " de la forêt"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void verificationInsensibleALaCasse() {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("Les ELFES et les NAINS s'allièrent contre les Vikings"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void motInterditDansUnePhraseEstBloque() {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("Un nain surgit de la montagne sombre"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void messageDerreurEstEnFrancais() {
        OutputGuardrailResult result = guardrail.validate(
                AiMessage.from("Les elfes chantaient avec les guerriers"));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("nains") || message.contains("elfes"),
                "Le message d'erreur doit mentionner les races fantastiques : " + message);
    }
}
