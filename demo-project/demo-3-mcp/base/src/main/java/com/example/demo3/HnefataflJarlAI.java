package com.example.demo3;

/**
 * Agent IA qui anime un jeu de Hnefatafl au Grand Thing des guerriers du Nord.
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
public interface HnefataflJarlAI {

    String play(String playerAction);
}
