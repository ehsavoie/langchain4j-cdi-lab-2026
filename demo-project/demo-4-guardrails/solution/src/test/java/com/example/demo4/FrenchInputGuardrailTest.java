package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.GuardrailResult;
import dev.langchain4j.guardrail.InputGuardrailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FrenchInputGuardrailTest {

    private FrenchInputGuardrail guardrail;

    @BeforeEach
    void setUp() {
        guardrail = new FrenchInputGuardrail();
        guardrail.init();
    }

    @Test
    void frenchRequestIsAccepted() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Chante les exploits d'Erik le Rouge, grand guerrier du Nord. Raconte ses batailles épiques et ses voyages vers le Vinland."));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
        assertTrue(result.failures().isEmpty());
    }

    @Test
    void englishRequestIsBlocked() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Sing me a song about the great Viking warrior Erik the Red and his epic battles across the northern seas."));

        assertEquals(GuardrailResult.Result.FAILURE, result.result());
        assertFalse(result.failures().isEmpty());
    }

    @Test
    void failureMessageIsInFrench() {
        InputGuardrailResult result = guardrail.validate(
                UserMessage.from("Can you sing about the great Viking warrior and his legendary battles across the northern seas?"));

        assertFalse(result.failures().isEmpty());
        String message = result.failures().get(0).message();
        assertTrue(message.contains("français"), "Failure message should be in French: " + message);
    }

    @Test
    void shortOrAmbiguousTextIsAccepted() {
        // Tika cannot be confident about very short texts → benefit of the doubt
        InputGuardrailResult result = guardrail.validate(UserMessage.from("Odin"));

        assertEquals(GuardrailResult.Result.SUCCESS, result.result());
    }
}
