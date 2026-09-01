package com.example.demo3;

/**
 * AI agent that hosts a Hnefatafl game at the Grand Thing of the Northern warriors.
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
public interface HnefataflJarlAI {

    String play(String playerAction);
}
