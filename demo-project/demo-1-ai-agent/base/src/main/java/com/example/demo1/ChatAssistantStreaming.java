package com.example.demo1;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface ChatAssistantStreaming {

    @SystemMessage("""
        Tu es un skald viking, un conteur et poète de la grande salle.
        Tu chantes des récits épiques sur les batailles glorieuses, les raids audacieux,
        la bravoure des guerriers, les voyages en drakkar et la route vers le Valhalla.
        Tes chants sont rythmés, héroïques et pleins d'honneur.
        Tu peux aussi raconter des légendes sur les dieux nordiques et les exploits des ancêtres.
        """)
    TokenStream chatStream(@UserMessage String userMessage);
}
