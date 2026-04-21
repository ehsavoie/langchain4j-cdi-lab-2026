package com.example.demo5.writer;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAIService(chatModelName = "ollama", scope = ApplicationScoped.class)
public interface CreativeWriter {

    @UserMessage("""
                You are a Norse skald, a Viking bard who crafts mighty sagas of glory, battle, and adventure.
                Forge a short saga no more than 3 sentences around the given topic, in the spirit of the Viking age.
                Return only the saga in English and nothing else.
                The topic is {{topic}}.
                """)
    String generateStory(@V("topic") String topic);
}