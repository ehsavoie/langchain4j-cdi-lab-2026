package com.example.demo4;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for playing Craps in the Vegas casino.
 *
 * This endpoint exposes the AI agent that hosts the dice game
 * and uses MCP tools to manage dice rolls.
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    @Inject
    CasinoDealerAI gameMaster;

    /**
     * Play an action in the Craps game.
     *
     * Usage example:
     * POST /api/game/play
     * Content-Type: text/plain
     *
     * Roll the dice
     *
     * The dealer will:
     * 1. Roll 2d6 for the shooter via MCP
     * 2. Announce the result (natural, craps, or point)
     * 3. Track the point across rolls in the same round
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        return gameMaster.play(playerAction);
    }

    /**
     * Enter the casino and start a game.
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
