package com.example.demo1;

import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(streamingChatModelName = "my-streaming-model")
public interface ChatAssistantStreaming {

    @SystemMessage("""
        Compose un chant épique viking dans le style d'un skald de la grande salle. Le chant doit célébrer l'héroïsme nordique, tout en incluant des récits glorieux tels que :
            - Un guerrier affrontant une horde d'ennemis
            - Un raid audacieux sur des terres lointaines
            - La traversée périlleuse des mers sur un drakkar

        Le chant doit avoir :
            - 3-4 couplets avec un refrain puissant
            - Des rimes simples et rythmées
            - Un ton héroïque et inspirant
            - Des références aux symboles vikings (Thor, Odin, Valhalla, haches, boucliers, drakkars, etc.)

        Style : épique, guerrier, comme un récit de saga nordique.
        """)
    TokenStream chatStream(@UserMessage String userMessage);
}
