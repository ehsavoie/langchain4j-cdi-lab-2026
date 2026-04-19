package com.example.demo5.orchestrator;

// TODO: Importer les annotations LangChain4j Agentic nécessaires
// import dev.langchain4j.agentic.Agent;
// import dev.langchain4j.agentic.scope.AgenticScopeAccess;
// import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
// import dev.langchain4j.service.UserMessage;
// import dev.langchain4j.service.V;

/**
 * Interfaces des agents utilisés par l'orchestrateur.
 * Chaque interface représente un agent dans le pipeline agentic.
 *
 * TODO: À compléter :
 * 1. Définir A2ACreativeWriter avec @Agent(description, outputKey="story")
 * 2. Définir A2AStyleScorer avec @Agent(description, outputKey="score")
 * 3. Définir StyleEditor avec @UserMessage et @Agent(description, outputKey="story")
 * 4. Définir StyleReviewLoop avec @Agent
 * 5. Définir StyledWriter qui extends AgenticScopeAccess avec @Agent
 */
public class Agents {

    // TODO: Interface pour l'agent A2A Creative Writer
    // @Agent(description = "Generate a Norse saga based on the given topic", outputKey = "story")
    // String generateStory(@V("topic") String topic);
    public interface A2ACreativeWriter {
        String generateStory(String topic);
    }

    // TODO: Interface pour l'agent A2A Style Scorer
    // @Agent(description = "Score a saga based on how well it captures a given style", outputKey = "score")
    // double scoreStyle(@V("story") String story, @V("style") String style);
    public interface A2AStyleScorer {
        double scoreStyle(String story, String style);
    }

    // TODO: Interface pour l'agent local StyleEditor
    // Ajouter @UserMessage avec le prompt d'édition de style :
    // """
    // You are a master Norse skald who shapes and tempers sagas like a smith forges steel.
    // Rewrite the following saga to better honor and embody the {{style}} style.
    // Return only the saga and nothing else.
    // The saga is "{{story}}".
    // """
    // Ajouter @Agent(description = "Reforge a saga to better capture a given style", outputKey = "story")
    public interface StyleEditor {
        String editStory(String story, String style);
    }

    // TODO: Interface pour la boucle de révision de style
    // @Agent("Judge the saga by the standards of the specified style, as a Viking elder would at the Thing")
    public interface StyleReviewLoop {
        String scoreAndReview(String story, String style);
    }

    // TODO: Interface principale pour l'orchestration complète
    // Doit étendre AgenticScopeAccess pour accéder au scope agentic
    // @Agent
    // ResultWithAgenticScope<String> writeStoryWithStyle(@V("topic") String topic, @V("style") String style);
    public interface StyledWriter {
        String writeStoryWithStyle(String topic, String style);
    }
}
