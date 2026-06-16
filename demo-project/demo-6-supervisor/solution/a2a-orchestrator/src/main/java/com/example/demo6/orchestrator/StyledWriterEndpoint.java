package com.example.demo6.orchestrator;

import dev.langchain4j.agentic.scope.AgenticScope;
import dev.langchain4j.agentic.scope.ResultWithAgenticScope;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/styled-story")
public class StyledWriterEndpoint {

    private static final Logger LOGGER = Logger.getLogger(StyledWriterEndpoint.class.getName());

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
        AgenticScope scope = result.agenticScope();
        String story = scope.readState("story", "");
        double score = scope.readState("score", 0.0);

        LOGGER.info("=== [demo-6] Endpoint: story = '" + story + "'");
        LOGGER.info("=== [demo-6] Endpoint: score = " + score);

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
