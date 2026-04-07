package com.example.demo2;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * In-memory repository for Viking expeditions.
 * Loads expedition data from viking-expeditions.json on the classpath.
 */
@ApplicationScoped
public class ExpeditionRepository {

    private static final Logger LOG = Logger.getLogger(ExpeditionRepository.class.getName());

    private final Map<String, Expedition> expeditions = new LinkedHashMap<>();

    @PostConstruct
    void init() {
        try (InputStream is = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("viking-expeditions.json");
             JsonReader reader = Json.createReader(is)) {
            JsonArray array = reader.readArray();
            for (JsonObject obj : array.getValuesAs(JsonObject.class)) {
                add(new Expedition(
                        obj.getString("id"),
                        obj.getString("destination"),
                        LocalDate.parse(obj.getString("departureDate")),
                        obj.getInt("warriorSlots"),
                        obj.getString("chief", null),
                        obj.getString("description", null),
                        obj.getString("requirements", null)));
            }
            LOG.info("Loaded " + expeditions.size() + " expeditions from viking-expeditions.json");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to load viking-expeditions.json", e);
        }
    }

    private void add(Expedition e) {
        expeditions.put(e.getId(), e);
    }

    public List<Expedition> listAll() {
        return new ArrayList<>(expeditions.values());
    }

    public Expedition findById(String id) {
        return expeditions.get(id);
    }

    public Expedition findByDestination(String destinationFragment) {
        return expeditions.values().stream()
                .filter(e -> e.getDestination().toLowerCase().contains(destinationFragment.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns all expeditions a warrior is enrolled in.
     */
    public List<Expedition> findEnrollments(String fullName) {
        return expeditions.values().stream()
                .filter(e -> e.isEnrolled(fullName))
                .collect(Collectors.toList());
    }
}
