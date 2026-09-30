package com.example.demo2;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import org.eclipse.microprofile.faulttolerance.*;
import java.time.temporal.ChronoUnit;

/**
 * AI Service for the Viking expedition assistant.
 *
 * Demonstrates:
 * - Tools: ExpeditionTools (enrollment, cancellation, search)
 * - Memory: conversation per session via @MemoryId
 * - Fault Tolerance: @Retry, @Timeout, @Fallback, @CircuitBreaker
 * - Business rules in @SystemMessage (simplified RAG)
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(chatModelName = "my-model",
                   chatMemoryProviderName = "my-memory",
                   contentRetrieverName = "my-rag",
                   tools = ExpeditionTools.class)
public interface ChatAssistant {

    @Retry(maxRetries = 3, delay = 1000)
    @Timeout(value = 30, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "chatFallback")
    @CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)
    @SystemMessage("""
        You are the Viking expedition assistant.
        You have access to a knowledge base about expeditions, chiefs, and destinations.
        Use it to answer questions about expedition details, requirements, and chiefs.

        IMPORTANT -- MANDATORY TOOL USAGE:
        You MUST call tools for EVERY action. NEVER simulate an action.
        - To list expeditions: call listExpeditions.
        - To enroll a warrior: call enrollWarrior. NEVER say "enrollment confirmed" without calling enrollWarrior.
        - To cancel an enrollment: call cancelEnrollment.
        - To check remaining slots: call remainingSlots.
        - To view enrollments: call myEnrollments.
        If you don't call the tool, the action DID NOT happen.

        RULES:
        - To enroll a warrior, you need their first name AND last name.
          If either is missing, ask for it.
        - Do NOT display technical identifiers (raid-angleterre, etc.) to the user.
          Use them internally when calling tools.
        - Reply in English, be concise.
        """)
    String chat(@MemoryId String sessionId, @UserMessage String message);

    /**
     * Fallback when the LLM is unavailable.
     */
    default String chatFallback(String sessionId, String message) {
        return "Oops! The LLM is taking a nap. Please try again in a moment. "
             + "(Circuit breaker active -- we're protecting your tokens!)";
    }
}
