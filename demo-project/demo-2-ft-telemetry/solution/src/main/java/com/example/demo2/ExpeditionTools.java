package com.example.demo2;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CDI Bean providing expedition tools for the AI assistant.
 * Connected to the ExpeditionRepository (in-memory) for realistic results.
 */
@ApplicationScoped
public class ExpeditionTools {

    @Inject
    ExpeditionRepository repository;

    @Tool("Liste toutes les expéditions vikings disponibles avec leurs places de guerrier restantes")
    public String listExpeditions() {
        List<Expedition> all = repository.listAll();
        return all.stream()
                .map(e -> e.getId() + " | " + e.toString())
                .collect(Collectors.joining("\n"));
    }

    @Tool("Inscrit un guerrier à une expédition viking. Utilise l'identifiant retourné par listExpeditions.")
    public String enrollWarrior(
            @P("Identifiant (ex: raid-angleterre) ou destination partielle de l'expédition") String expeditionId,
            @P("Prénom du guerrier") String firstName,
            @P("Nom de famille du guerrier") String lastName) {
        Expedition expedition = repository.findById(expeditionId);
        if (expedition == null) {
            expedition = repository.findByDestination(expeditionId);
        }
        if (expedition == null) {
            return "Expédition '" + expeditionId + "' introuvable. Utilisez listExpeditions pour voir les expéditions disponibles.";
        }
        String fullName = firstName + " " + lastName;
        if (expedition.isEnrolled(fullName)) {
            return fullName + " est déjà inscrit pour " + expedition.getDestination();
        }
        if (expedition.getRemainingSlots() <= 0) {
            return "Désolé, l'expédition " + expedition.getDestination() + " est complète!";
        }
        expedition.enroll(fullName);
        return "Inscription confirmée: " + fullName + " pour '" + expedition.getDestination()
                + "'. Places restantes: " + expedition.getRemainingSlots() + "/" + expedition.getWarriorSlots();
    }

    @Tool("Annule l'inscription d'un guerrier à une expédition. Utilise l'identifiant retourné par listExpeditions.")
    public String cancelEnrollment(
            @P("Identifiant (ex: raid-angleterre) ou destination partielle de l'expédition") String expeditionId,
            @P("Prénom du guerrier") String firstName,
            @P("Nom de famille du guerrier") String lastName) {
        Expedition expedition = repository.findById(expeditionId);
        if (expedition == null) {
            expedition = repository.findByDestination(expeditionId);
        }
        if (expedition == null) {
            return "Expédition '" + expeditionId + "' introuvable.";
        }
        String fullName = firstName + " " + lastName;
        if (expedition.cancelEnrollment(fullName)) {
            return "Inscription annulée pour " + fullName + " de '" + expedition.getDestination()
                    + "'. Places restantes: " + expedition.getRemainingSlots() + "/" + expedition.getWarriorSlots();
        }
        return fullName + " n'est pas inscrit pour " + expedition.getDestination();
    }

    @Tool("Retourne le nombre de places de guerrier restantes pour une expédition. Utilise l'identifiant retourné par listExpeditions.")
    public String remainingSlots(@P("Identifiant (ex: raid-angleterre) ou destination partielle de l'expédition") String expeditionId) {
        Expedition expedition = repository.findById(expeditionId);
        if (expedition == null) {
            expedition = repository.findByDestination(expeditionId);
        }
        if (expedition == null) {
            return "Expédition '" + expeditionId + "' introuvable.";
        }
        return expedition.getDestination() + ": " + expedition.getRemainingSlots() + " places de guerrier restantes sur " + expedition.getWarriorSlots();
    }

    @Tool("Liste toutes les inscriptions d'un guerrier aux expéditions")
    public String myEnrollments(
            @P("Prénom du guerrier") String firstName,
            @P("Nom de famille du guerrier") String lastName) {
        String fullName = firstName + " " + lastName;
        List<Expedition> enrollments = repository.findEnrollments(fullName);
        if (enrollments.isEmpty()) {
            return fullName + " n'est inscrit à aucune expédition.";
        }
        return fullName + " est inscrit pour:\n" + enrollments.stream()
                .map(e -> "- " + e.toString())
                .collect(Collectors.joining("\n"));
    }
}
