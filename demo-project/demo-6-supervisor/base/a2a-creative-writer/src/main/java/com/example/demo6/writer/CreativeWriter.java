package com.example.demo6.writer;

import jakarta.enterprise.context.ApplicationScoped;

// TODO 1 : Ajouter les imports :
//   import dev.langchain4j.cdi.spi.RegisterAIService;
//   import dev.langchain4j.service.UserMessage;
//   import dev.langchain4j.service.V;

// TODO 2 : Annoter l'interface avec @RegisterAIService pour la rendre injectable via CDI :
//   @RegisterAIService(chatModelName = "ollama", scope = ApplicationScoped.class)
public interface CreativeWriter {

    // TODO 3 : Annoter la méthode avec @UserMessage :
    //   @UserMessage("""
    //           You are a Norse skald, a Viking bard who crafts mighty sagas of glory, battle, and adventure.
    //           Forge a short saga no more than 3 sentences around the given topic, in the spirit of the Viking age.
    //           Return only the saga in English and nothing else.
    //           The topic is {{topic}}.
    //           """)
    // TODO 4 : Ajouter @V("topic") devant le paramètre
    String generateStory(String topic);
}
