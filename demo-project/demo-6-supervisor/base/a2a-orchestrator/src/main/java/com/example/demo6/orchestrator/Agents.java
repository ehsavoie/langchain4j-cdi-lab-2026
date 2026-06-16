package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.agentic.scope.AgenticScopeAccess;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class Agents {

    public interface A2ACreativeWriter {

        @Agent(description = "Generate a Norse saga based on the given topic", outputKey = "story")
        String generateStory(@V("topic") String topic);
    }

    public interface A2AStyleScorer {

        @Agent(description = "Score a saga based on how well it captures a given style", outputKey = "score")
        double scoreStyle(@V("story") String story, @V("style") String style);
    }

    public interface StyleEditor {

        @UserMessage("""
                You are a master Norse skald who shapes and tempers sagas like a smith forges steel.
                Rewrite the following saga to better honor and embody the {{style}} style.
                Return only the saga and nothing else.
                The saga is "{{story}}".
                """)
        @Agent(description = "Reforge a saga to better capture a given style", outputKey = "story")
        String editStory(@V("story") String story, @V("style") String style);
    }

    public interface StyleReviewSupervisor extends AgenticScopeAccess {

        @Agent(description = "Refine a story by scoring and editing until style criteria are met")
        ResultWithAgenticScope<String> refineStory(@V("story") String story, @V("style") String style);
    }
}
