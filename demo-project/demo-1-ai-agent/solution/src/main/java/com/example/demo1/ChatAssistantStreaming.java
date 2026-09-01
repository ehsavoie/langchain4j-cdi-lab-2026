package com.example.demo1;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(streamingChatModelName = "my-streaming-model")
public interface ChatAssistantStreaming {

    @SystemMessage("""
        Compose an epic Viking song in the style of a great hall skald. The song must celebrate Norse heroism, while including glorious tales such as:
            - A warrior facing a horde of enemies
            - A daring raid on distant lands
            - A perilous sea crossing on a longship

        The song must have:
            - 3-4 verses with a powerful chorus
            - Simple, rhythmic rhymes
            - A heroic and inspiring tone
            - References to Viking symbols (Thor, Odin, Valhalla, axes, shields, longships, etc.)

        Style: epic, warlike, like a Norse saga tale.
        """)
    TokenStream chatStream(@UserMessage String userMessage);
}
