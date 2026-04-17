package com.example.demo5.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
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

        ResultWithAgenticScope<String> result = orchestratorService.writeStyledStory(topic, style);
        String story = result.result();
        AgenticScope scope = result.agenticScope();
        double score = scope.readState("score", 0.0);

        String json = """
                {
                  "story": %s,
                  "score": %s,
                  "topic": %s,
                  "style": %s
                }
                """.formatted(
                jsonString(story),
                score,
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
