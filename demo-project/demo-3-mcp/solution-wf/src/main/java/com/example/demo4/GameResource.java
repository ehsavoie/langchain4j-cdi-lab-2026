package com.example.demo4;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for playing Hnefatafl at the Grand Thing.
 *
 * This endpoint exposes the AI agent that hosts the rune stone game
 * and uses MCP tools to manage stone rolls.
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    @Inject
    HnefataflJarlAI gameMaster;

    /**
     * Play an action in the Hnefatafl game.
     *
     * Usage example:
     * POST /api/game/play
     * Content-Type: text/plain
     *
     * Cast the runes
     *
     * The Jarl will:
     * 1. Cast 2d6 rune stones for the warrior via MCP
     * 2. Announce the result (Odin's Favour, Curse, or Marked Rune)
     * 3. Track the marked rune across casts in the same round
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        return gameMaster.play(playerAction);
    }

    /**
     * Enter the Thing and start a game.
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
