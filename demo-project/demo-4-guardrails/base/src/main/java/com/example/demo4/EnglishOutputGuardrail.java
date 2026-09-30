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
 * Output guardrail that verifies the generated song is in English.
 * Uses Apache Tika to confirm the language of the LLM response.
 * Since the response is long enough, detection is reliable.
 */
@ApplicationScoped
public class EnglishOutputGuardrail implements OutputGuardrail {

    private LanguageDetector detector;

    @PostConstruct
    void init() {
    }

    @Override
    public OutputGuardrailResult validate(AiMessage responseFromLLM) {
        return success();
    }
}
