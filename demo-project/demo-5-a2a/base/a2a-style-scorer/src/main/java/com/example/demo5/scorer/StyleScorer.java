package com.example.demo5.scorer;

import jakarta.enterprise.context.ApplicationScoped;

// TODO 1 : Ajouter les imports :
//   import dev.langchain4j.cdi.spi.RegisterAIService;
//   import dev.langchain4j.service.UserMessage;
//   import dev.langchain4j.service.V;

// TODO 2 : Annoter l'interface avec @RegisterAIService pour la rendre injectable via CDI :
//   @RegisterAIService(chatModelName = "ollama", scope = ApplicationScoped.class)
public interface StyleScorer {

    // TODO 3 : Annoter la méthode avec @UserMessage :
    //   @UserMessage("""
    //           You are a seasoned Norse skald elder who judges sagas by the fire of a longhouse.
    //           Give a score between 0.0 and 1.0 for the following saga based on how well it captures the '{{style}}' style.
    //           Return only the score and nothing else.
    //
    //           The saga is: "{{story}}"
    //           """)
    // TODO 4 : Ajouter @V("story") et @V("style") devant les paramètres
    double scoreStyle(String story, String style);
}
