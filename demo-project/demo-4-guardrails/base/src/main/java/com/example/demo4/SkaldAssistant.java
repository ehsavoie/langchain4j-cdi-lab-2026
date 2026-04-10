package com.example.demo4;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

// TODO: Add @RegisterAIService(chatModelName = "my-model",
//         inputGuardrailNames  = {"french-input",  "fantasy-input"},
//         outputGuardrailNames = {"french-output", "fantasy-output"})
public interface SkaldAssistant {

    @SystemMessage("""
        Tu es un skald viking qui compose des chants épiques en FRANÇAIS.
        Le chant doit célébrer les thèmes nordiques héroïques : batailles, raids, Valhalla, Thor, Odin, drakkars.
        Format : 3-4 couplets avec un refrain puissant.
        Ton : épique, héroïque, guerrier.
        """)
    String composeSong(@UserMessage String request);
}
