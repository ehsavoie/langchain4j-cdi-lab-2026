package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

/**
 * Guardrail de sortie qui vérifie que le chant généré est en français.
 * Utilise Apache Tika pour confirmer la langue de la réponse du LLM.
 * La réponse étant suffisamment longue, la détection est fiable.
 */
@ApplicationScoped
public class FrenchOutputGuardrail implements OutputGuardrail {

    private LanguageDetector detector;

    @PostConstruct
    void init() {
        // TODO: Initialize the detector: new OptimaizeLangDetector().loadModels()
    }

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        // TODO: Get the AI response text with responseFromLLM.text()
        // TODO: Detect the language with detector.detect(text) (synchronized on detector)
        // TODO: Return failure("...") if NOT result.isReasonablyCertain() OR language is not "fr"
        // TODO: Return success() otherwise
        return success();
    }
}
