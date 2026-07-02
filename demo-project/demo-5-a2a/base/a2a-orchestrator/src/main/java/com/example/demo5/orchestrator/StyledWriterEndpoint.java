package com.example.demo5.orchestrator;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/styled-story")
public class StyledWriterEndpoint {

    @Inject
    OrchestratorService orchestratorService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response writeStyledStory(
            @QueryParam("topic") String topic,
            @QueryParam("style") String style) {

        if (topic == null || topic.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"topic parameter is required\"}")
                    .build();
        }
        if (style == null || style.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"style parameter is required\"}")
                    .build();
        }

        // TODO - Étape 4d : Mettre à jour l'appel pour extraire la story ET le score depuis le scope.
        // Remplacer la ligne ci-dessous par :
        //
        //   ResultWithAgenticScope<String> result = orchestratorService.writeStyledStory(topic, style);
        //   String story = result.result();
        //   AgenticScope scope = result.agenticScope();
        //   double score = scope.readState("score", 0.0);
        //
        // Ajouter les imports : AgenticScope, ResultWithAgenticScope
        // Ajouter "score" dans le JSON et dans le .formatted(...) :
        //   "score": %s,
        //   .formatted(jsonString(story), score, jsonString(topic), jsonString(style))
        String story = orchestratorService.writeStyledStory(topic, style);

        String json = """
                {
                  "story": %s,
                  "topic": %s,
                  "style": %s
                }
                """.formatted(
                jsonString(story),
                jsonString(topic),
                jsonString(style));

        return Response.ok(json).build();
    }

    private static String jsonString(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t") + "\"";
    }
}
