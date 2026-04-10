package com.example.demo4;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

/**
 * Guardrail d'entrée qui accepte uniquement les requêtes en français.
 * Utilise Apache Tika pour détecter la langue du message.
 * Si la langue est identifiée avec certitude comme non-française, la requête est rejetée.
 */
@ApplicationScoped
@Named("french-input")
public class FrenchInputGuardrail implements InputGuardrail {

    private LanguageDetector detector;

    @PostConstruct
    void init() {
        detector = new OptimaizeLangDetector().loadModels();
    }

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        String text = userMessage.singleText();
        LanguageResult result;
        synchronized (detector) {
            result = detector.detect(text);
        }
        if (result.isReasonablyCertain() && !"fr".equals(result.getLanguage())) {
            return failure("Seul le français est accepté ! Parlez français pour invoquer le skald.");
        }
        return success();
    }
}
