package com.example.demo5.orchestrator;

/**
 * Ce fichier est conservé pour la compatibilité git.
 *
 * Les interfaces des agents sont désormais dans des fichiers séparés :
 *   - A2ACreativeWriter.java  → proxy vers le Creative Writer distant (A2A)
 *   - A2AStyleScorer.java     → proxy vers le Style Scorer distant (A2A)
 *   - StyleEditor.java        → agent local de réécriture (Ollama)
 *   - StyledWriter.java       → pipeline SEQUENCE (creative-writer → style-review-loop)
 *   - AgentProducers.java     → producteur CDI pour la boucle de révision
 */
public final class Agents {
    private Agents() {}
}
