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
    void requeteNordiqueNormaleEstAcceptee() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Chante les exploits d'Erik le Rouge et ses guerriers vikings"));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"nain", "nains"})
    void requeteMentionnantDesNainsEstBloquee(String motInterdit) {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Un chant avec des " + motInterdit + " forgeurs"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"elfe", "elfes"})
    void requeteMentionnantDesElfesEstBloquee(String motInterdit) {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Un chant sur les " + motInterdit + " des forêts"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void verificationInsensibleALaCasse() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Chante les NAINS et les ELFES"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void motInterditDansUnePhraseEstBloque() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Je veux un chant sur les nains !"));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
    }

    @Test
    void messageDerreurEstEnFrancais() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Un chant sur les elfes"));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("nains") || message.contains("elfes"),
                "Le message d'erreur doit mentionner les races fantastiques : " + message);
    }
}
