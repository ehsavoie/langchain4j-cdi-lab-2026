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

    @Tool("Lists all available Viking expeditions with their remaining warrior slots")
    public String listExpeditions() {
        List<Expedition> all = repository.listAll();
        return all.stream()
                .map(e -> e.getId() + " | " + e.toString())
                .collect(Collectors.joining("\n"));
    }

    @Tool("Enrolls a warrior in a Viking expedition. Use the identifier returned by listExpeditions.")
    public String enrollWarrior(
            @P("Identifier (e.g. raid-angleterre) or partial destination of the expedition") String expeditionId,
            @P("Warrior's first name") String firstName,
            @P("Warrior's last name") String lastName) {
        Expedition expedition = repository.findById(expeditionId);
        if (expedition == null) {
            expedition = repository.findByDestination(expeditionId);
        }
        if (expedition == null) {
            return "Expedition '" + expeditionId + "' not found. Use listExpeditions to see available expeditions.";
        }
        String fullName = firstName + " " + lastName;
        if (expedition.isEnrolled(fullName)) {
            return fullName + " is already enrolled for " + expedition.getDestination();
        }
        if (expedition.getRemainingSlots() <= 0) {
            return "Sorry, the expedition " + expedition.getDestination() + " is full!";
        }
        expedition.enroll(fullName);
        return "Enrollment confirmed: " + fullName + " for '" + expedition.getDestination()
                + "'. Remaining slots: " + expedition.getRemainingSlots() + "/" + expedition.getWarriorSlots();
    }

    @Tool("Cancels a warrior's enrollment in an expedition. Use the identifier returned by listExpeditions.")
    public String cancelEnrollment(
            @P("Identifier (e.g. raid-angleterre) or partial destination of the expedition") String expeditionId,
            @P("Warrior's first name") String firstName,
            @P("Warrior's last name") String lastName) {
        Expedition expedition = repository.findById(expeditionId);
        if (expedition == null) {
            expedition = repository.findByDestination(expeditionId);
        }
        if (expedition == null) {
            return "Expedition '" + expeditionId + "' not found.";
        }
        String fullName = firstName + " " + lastName;
        if (expedition.cancelEnrollment(fullName)) {
            return "Enrollment cancelled for " + fullName + " from '" + expedition.getDestination()
                    + "'. Remaining slots: " + expedition.getRemainingSlots() + "/" + expedition.getWarriorSlots();
        }
        return fullName + " is not enrolled for " + expedition.getDestination();
    }

    @Tool("Returns the number of remaining warrior slots for an expedition. Use the identifier returned by listExpeditions.")
    public String remainingSlots(@P("Identifier (e.g. raid-angleterre) or partial destination of the expedition") String expeditionId) {
        Expedition expedition = repository.findById(expeditionId);
        if (expedition == null) {
            expedition = repository.findByDestination(expeditionId);
        }
        if (expedition == null) {
            return "Expedition '" + expeditionId + "' not found.";
        }
        return expedition.getDestination() + ": " + expedition.getRemainingSlots() + " warrior slots remaining out of " + expedition.getWarriorSlots();
    }

    @Tool("Lists all expedition enrollments for a warrior")
    public String myEnrollments(
            @P("Warrior's first name") String firstName,
            @P("Warrior's last name") String lastName) {
        String fullName = firstName + " " + lastName;
        List<Expedition> enrollments = repository.findEnrollments(fullName);
        if (enrollments.isEmpty()) {
            return fullName + " is not enrolled in any expedition.";
        }
        return fullName + " is enrolled for:\n" + enrollments.stream()
                .map(e -> "- " + e.toString())
                .collect(Collectors.joining("\n"));
    }
}
