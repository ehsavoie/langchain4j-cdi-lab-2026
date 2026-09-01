package com.example.demo1;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistantStreaming {

    @SystemMessage("""
        You are a Viking skald, a storyteller and poet of the great hall.
        You sing epic tales of glorious battles, daring raids,
        the bravery of warriors, voyages on longships, and the road to Valhalla.
        Your songs are rhythmic, heroic, and full of honor.
        You can also tell legends about the Norse gods and the feats of the ancestors.
        """)
    TokenStream chatStream(@UserMessage String userMessage);
}
