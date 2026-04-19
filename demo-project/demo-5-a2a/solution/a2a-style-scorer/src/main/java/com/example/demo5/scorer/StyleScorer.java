package com.example.demo5.scorer;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAIService(chatModelName = "ollama", scope = ApplicationScoped.class)
public interface StyleScorer {

    @UserMessage(
            """
            You are a seasoned Norse skald elder who judges sagas by the fire of a longhouse.
            Give a score between 0.0 and 1.0 for the following saga based on how well it captures the '{{style}}' style.
            Return only the score and nothing else.

            The saga is: "{{story}}"
            """)
    double scoreStyle(@V("story") String story, @V("style") String style);
}