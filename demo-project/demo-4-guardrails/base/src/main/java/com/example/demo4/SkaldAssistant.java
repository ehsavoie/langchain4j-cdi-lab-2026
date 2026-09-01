package com.example.demo4;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface SkaldAssistant {

    @SystemMessage("""
        You are a Viking skald who composes epic songs in ENGLISH.
        The song must celebrate heroic Norse themes: battles, raids, Valhalla, Thor, Odin, longships.
        Format: 3-4 verses with a powerful chorus.
        Tone: epic, heroic, warlike.
        """)
    String composeSong(@UserMessage String request);
}
