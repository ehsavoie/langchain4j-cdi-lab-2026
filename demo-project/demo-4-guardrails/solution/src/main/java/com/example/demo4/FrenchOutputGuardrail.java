package com.example.demo4;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailResult;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.apache.tika.langdetect.optimaize.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;

/**
 * Guardrail de sortie qui vérifie que le chant généré est en français.
 * Utilise Apache Tika pour confirmer la langue de la réponse du LLM.
 * La réponse étant suffisamment longue, la détection est fiable.
 */
@ApplicationScoped
@Named("french-output")
public class FrenchOutputGuardrail implements OutputGuardrail {

    private LanguageDetector detector;

    @PostConstruct
    void init() {
        detector = new OptimaizeLangDetector().loadModels();
    }

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        String text = responseFromLLM.text();
        LanguageResult result;
        synchronized (detector) {
            result = detector.detect(text);
        }
        if (!result.isReasonablyCertain() || !"fr".equals(result.getLanguage())) {
            return failure("Le chant doit être en français ! Le skald doit chanter dans la langue de Molière.");
        }
        return success();
    }
}
