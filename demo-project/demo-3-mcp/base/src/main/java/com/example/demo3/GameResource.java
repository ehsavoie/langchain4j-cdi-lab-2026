package com.example.demo3;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * TODO: REST endpoint for playing Craps in the Vegas casino.
 *
 * To complete:
 * 1. Inject the CasinoDealerAI
 * 2. Implement the play() method that calls the agent
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    // TODO: Inject the CasinoDealerAI with @Inject
    // CasinoDealerAI gameMaster;

    /**
     * TODO: Play an action in the Craps game.
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        // TODO: Call gameMaster.play(playerAction)
        throw new UnsupportedOperationException("TODO: To implement during live coding");
    }

    /**
     * Enter the casino and start a game.
     */
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start() {
        // TODO: Return gameMaster.play("Hello! I'm here to play Craps.")
        throw new UnsupportedOperationException("TODO: To implement during live coding");
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
