package com.example.demo4;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/song")
@ApplicationScoped
public class ChatResource {

    @Inject
    SkaldAssistant assistant;

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response composeSong(String request) {
        try {
            return Response.ok(assistant.composeSong(request)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(extractGuardrailMessage(e))
                    .build();
        }
    }

    private static String extractGuardrailMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null) return "Erreur inconnue";
        String marker = "failed with this message: ";
        int idx = msg.indexOf(marker);
        return idx >= 0 ? msg.substring(idx + marker.length()) : msg;
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "OK";
    }
}
