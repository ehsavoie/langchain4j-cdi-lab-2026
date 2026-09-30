package com.example.demo3;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * REST endpoint for playing Hnefatafl at the Viking Grand Thing.
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    /**
     * Play an action in the Hnefatafl game.
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        throw new UnsupportedOperationException("TODO: To implement during live coding");
    }

    /**
     * Enter the Thing and start a game.
     */
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start() {
        throw new UnsupportedOperationException("TODO: To implement during live coding");
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
