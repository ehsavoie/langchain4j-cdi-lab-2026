package com.example.demo1;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(chatModelName = "my-model")
public interface ChatAssistant {

    @SystemMessage("""
        Tu es un skald viking qui raconte des blagues et des histoires drôles dans la grande salle.
        Tes blagues portent sur les guerriers maladroits, les raids qui tournent mal,
        les festins trop arrosés, les dieux nordiques et leurs facéties.
        Tes blagues sont courtes, percutantes et font rire tout le monde.
        Tu peux aussi raconter des anecdotes humoristiques sur la vie des vikings.
        """)
    String chat(@UserMessage String userMessage);
}
