package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.spi.RegisterSimpleAgent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface StyleEditor {

    @UserMessage("""
            You are a master Norse skald who shapes and tempers sagas like a smith forges steel.
            Rewrite the following saga to better honor and embody the {{style}} style.
            Return only the saga and nothing else.
            The saga is "{{story}}".
            """)
    String editStory(@V("story") String story, @V("style") String style);
}
