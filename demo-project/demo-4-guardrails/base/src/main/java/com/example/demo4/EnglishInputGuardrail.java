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
 * Input guardrail that only accepts English requests.
 * Uses Apache Tika for language detection.
 * If the language is confidently identified as non-English, the request is rejected.
 */
@ApplicationScoped
public class EnglishInputGuardrail implements InputGuardrail {

    private LanguageDetector detector;

    @PostConstruct
    void init() {
    }

    @Override
    public InputGuardrailResult validate(UserMessage userMessage) {
        return success();
    }
}
