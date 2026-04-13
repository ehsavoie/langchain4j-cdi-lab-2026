package com.example.demo3;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * Point d'entrée REST pour le Hnefatafl au Grand Thing.
 *
 * <p>Délègue toute la logique de jeu à {@link HnefataflJarlAI}. Trois endpoints sont exposés
 * sous {@code /api/game} :
 * <ul>
 *   <li>{@code GET  /start} — accueillir le guerrier et démarrer une nouvelle session.</li>
 *   <li>{@code POST /play}  — envoyer une action du guerrier (texte brut) et recevoir la réponse du Jarl.</li>
 *   <li>{@code GET  /health} — vérification de disponibilité.</li>
 * </ul>
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    @Inject
    HnefataflJarlAI gameMaster;

    /**
     * Envoie une action du guerrier au Jarl et reçoit la réponse de jeu.
     *
     * <p>Exemple : {@code POST /api/game/play} avec le corps {@code Lance les runes}
     *
     * <p>Le Jarl invoquera l'outil MCP {@code roll} pour lancer 2 pierres runiques, puis
     * annoncera le résultat (Faveur d'Odin, Malédiction, rune marquée, atteinte ou Ragnarök).
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        return gameMaster.play(playerAction);
    }

    /**
     * Accueille le guerrier et démarre une nouvelle session de jeu.
     */
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start() {
        return gameMaster.play("Salve ! Je suis prêt à jouer au Hnefatafl.");
    }

    /**
     * Endpoint de vérification de disponibilité.
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "Le Grand Thing est ouvert - Ragnar le Skald est prêt pour un Hnefatafl !";
    }
}
