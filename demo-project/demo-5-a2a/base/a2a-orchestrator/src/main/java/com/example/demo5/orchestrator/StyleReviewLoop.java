package com.example.demo5.orchestrator;

import dev.langchain4j.cdi.spi.RegisterLoopAgent;
import dev.langchain4j.service.V;

public interface StyleReviewLoop {

    String refineStory(@V("story") String story, @V("style") String style);
}
