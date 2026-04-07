package com.example.demo3;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for the Craps game at "The Golden Ace Casino".
 *
 * <p>Delegates all game logic to {@link CasinoDealerAI}. Three endpoints are exposed
 * under {@code /api/game}:
 * <ul>
 *   <li>{@code GET  /start} — greet the player and start a new session.</li>
 *   <li>{@code POST /play}  — send a player action (plain text) and receive the dealer's response.</li>
 *   <li>{@code GET  /health} — liveness check.</li>
 * </ul>
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    @Inject
    CasinoDealerAI gameMaster;

    /**
     * Send a player action to the dealer and receive the game response.
     *
     * <p>Example: {@code POST /api/game/play} with body {@code Roll the dice}
     *
     * <p>The dealer will invoke the MCP {@code roll} tool to roll 2d6, then
     * announce the outcome (natural, craps, point set, hit, or seven-out).
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        return gameMaster.play(playerAction);
    }

    /**
     * Greet the player and start a new game session.
     */
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start() {
        return gameMaster.play("Hello! I'm here to play Craps.");
    }

    /**
     * Health check endpoint.
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "The Golden Ace Casino OK - Lucky Jack is ready for a game of Craps!";
    }
}
