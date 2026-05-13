package com.example.demo5.orchestrator;

// TODO: Importer les annotations nécessaires
// import dev.langchain4j.cdi.spi.RegisterAgent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * Agent local StyleEditor — réécrit une saga pour mieux correspondre à un style.
 *
 * TODO: Annoter l'interface avec @RegisterAgent :
 *   name = "style-editor"
 *   description = "Reforge a saga to better capture a given style"
 *   outputKey = "story"
 *   chatModelName = "ollama"
 *
 * LangChain4j-CDI crée un bean CDI @Named("style-editor") alimenté par Ollama.
 */
public interface StyleEditor {

    @UserMessage("""
            You are a master Norse skald who shapes and tempers sagas like a smith forges steel.
            Rewrite the following saga to better honor and embody the {{style}} style.
            Return only the saga and nothing else.
            The saga is "{{story}}".
            """)
    String editStory(@V("story") String story, @V("style") String style);
}
