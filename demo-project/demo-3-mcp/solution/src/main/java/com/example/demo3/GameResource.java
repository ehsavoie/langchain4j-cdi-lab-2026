package com.example.demo3;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for Hnefatafl at the Grand Thing.
 *
 * <p>Delegates all game logic to {@link HnefataflJarlAI}. Three endpoints are exposed
 * under {@code /api/game}:
 * <ul>
 *   <li>{@code GET  /start} — welcome the warrior and start a new session.</li>
 *   <li>{@code POST /play}  — send a warrior action (plain text) and receive the Jarl's response.</li>
 *   <li>{@code GET  /health} — health check.</li>
 * </ul>
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    @Inject
    HnefataflJarlAI gameMaster;

    /**
     * Sends a warrior action to the Jarl and receives the game response.
     *
     * <p>Example: {@code POST /api/game/play} with body {@code Cast the runes}
     *
     * <p>The Jarl will invoke the MCP tool {@code roll} to cast 2 rune stones, then
     * announce the result (Odin's Favour, Curse, marked rune, rune hit, or Ragnarök).
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        return gameMaster.play(playerAction);
    }

    /**
     * Welcomes the warrior and starts a new game session.
     */
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start() {
        return gameMaster.play("Hail! I am ready to play Hnefatafl.");
    }

    /**
     * Health check endpoint.
     */
    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "The Grand Thing is open - Ragnar the Skald is ready for Hnefatafl!";
    }
}
