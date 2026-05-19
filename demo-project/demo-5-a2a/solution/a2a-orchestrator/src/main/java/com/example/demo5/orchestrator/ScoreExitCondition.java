package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScope;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

@ApplicationScoped
@Named("scoreExitCondition")
public class ScoreExitCondition implements Predicate<AgenticScope> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreExitCondition.class);

    @Override
    public boolean test(AgenticScope scope) {
        Object raw = scope.readState("score");
        LOGGER.info("The score of the story " + scope.readState("story") + " is " + raw);
        if (raw == null) {
            return false;
        }
        if (raw instanceof Number n) {
            return n.doubleValue() >= 0.8;
        }
        try {
            return Double.parseDouble(raw.toString()) >= 0.8;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
