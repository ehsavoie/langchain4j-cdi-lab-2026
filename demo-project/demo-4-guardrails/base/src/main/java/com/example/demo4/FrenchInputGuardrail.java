package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

/**
 * Guardrail d'entrée qui accepte uniquement les requêtes en français.
 * Utilise Apache Tika pour détecter la langue du message.
 * Si la langue est identifiée avec certitude comme non-française, la requête est rejetée.
 */
@ApplicationScoped
public class FrenchInputGuardrail implements InputGuardrail {

    private LanguageDetector detector;

    @PostConstruct
    void init() {
        // TODO: Initialize the detector: new OptimaizeLangDetector().loadModels()
    }

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        // TODO: Get the user message text with userMessage.singleText()
        // TODO: Detect the language with detector.detect(text) (synchronized on detector)
        // TODO: Return failure("...") if result.isReasonablyCertain() AND language is not "fr"
        // TODO: Return success() otherwise (French or too short to detect)
        return success();
    }
}
