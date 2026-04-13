package com.example.demo3;

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

/**
 * TODO: Point d'entrée REST pour jouer au Hnefatafl au Grand Thing des vikings.
 *
 * À compléter :
 * 1. Injecter le CasinoDealerAI
 * 2. Implémenter la méthode play() qui appelle l'agent
 */
@Path("/game")
@ApplicationScoped
public class GameResource {

    // TODO: Injecter le CasinoDealerAI avec @Inject
    // CasinoDealerAI gameMaster;

    /**
     * TODO: Jouer une action dans la partie de Hnefatafl.
     */
    @POST
    @Path("/play")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String play(String playerAction) {
        // TODO: Appeler gameMaster.play(playerAction)
        throw new UnsupportedOperationException("TODO: À implémenter pendant le live coding");
    }

    /**
     * Entrer dans le Thing et démarrer une partie.
     */
    @GET
    @Path("/start")
    @Produces(MediaType.TEXT_PLAIN)
    public String start() {
        // TODO: Retourner gameMaster.play("Salve ! Je suis prêt à jouer au Hnefatafl.")
        throw new UnsupportedOperationException("TODO: À implémenter pendant le live coding");
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
