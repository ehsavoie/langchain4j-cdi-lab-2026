package com.example.demo2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Viking expedition with its enrolled warriors.
 * Stored in memory for the demo.
 */
public class Expedition {

    private final String id;
    private final String destination;
    private final LocalDate departureDate;
    private final int warriorSlots;
    private final String chief;
    private final String description;
    private final String requirements;
    private final List<String> enrollments = new ArrayList<>();

    public Expedition(String id, String destination, LocalDate departureDate, int warriorSlots) {
        this(id, destination, departureDate, warriorSlots, null, null, null);
    }

    public Expedition(String id, String destination, LocalDate departureDate, int warriorSlots,
                      String chief, String description, String requirements) {
        this.id = id;
        this.destination = destination;
        this.departureDate = departureDate;
        this.warriorSlots = warriorSlots;
        this.chief = chief;
        this.description = description;
        this.requirements = requirements;
    }

    public String getId() { return id; }
    public String getDestination() { return destination; }
    public LocalDate getDepartureDate() { return departureDate; }
    public int getWarriorSlots() { return warriorSlots; }
    public String getChief() { return chief; }
    public String getDescription() { return description; }
    public String getRequirements() { return requirements; }

    public int getRemainingSlots() {
        return warriorSlots - enrollments.size();
    }

    public List<String> getEnrollments() {
        return Collections.unmodifiableList(enrollments);
    }

    public boolean enroll(String fullName) {
        if (enrollments.size() >= warriorSlots) return false;
        if (enrollments.contains(fullName)) return false;
        enrollments.add(fullName);
        return true;
    }

    public boolean cancelEnrollment(String fullName) {
        return enrollments.remove(fullName);
    }

    public boolean isEnrolled(String fullName) {
        return enrollments.contains(fullName);
    }

    public String toRagDocument() {
        StringBuilder sb = new StringBuilder();
        sb.append("Expédition: ").append(destination).append("\n");
        sb.append("Départ: ").append(departureDate).append("\n");
        sb.append("Places de guerrier: ").append(warriorSlots).append("\n");
        if (chief != null) sb.append("Chef: ").append(chief).append("\n");
        if (description != null) sb.append("Description: ").append(description).append("\n");
        if (requirements != null) sb.append("Exigences: ").append(requirements).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return departureDate + " -- " + destination + " (" + getRemainingSlots() + "/" + warriorSlots + " places)";
    }
}
