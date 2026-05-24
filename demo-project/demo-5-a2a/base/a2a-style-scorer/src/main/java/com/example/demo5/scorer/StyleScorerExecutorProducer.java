package com.example.demo5.scorer;

import io.a2a.server.agentexecution.AgentExecutor;
import io.a2a.server.agentexecution.RequestContext;
import io.a2a.server.events.EventQueue;
import io.a2a.server.tasks.TaskUpdater;
import io.a2a.spec.JSONRPCError;
import io.a2a.spec.Message;
import io.a2a.spec.Part;
import io.a2a.spec.Task;
import io.a2a.spec.TaskNotCancelableError;
import io.a2a.spec.TaskState;
import io.a2a.spec.TextPart;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class StyleScorerExecutorProducer {

    @Inject
    StyleScorer styleScorerAgent;

    @Produces
    public AgentExecutor agentExecutor() {
        return new StyleScorerExecutor(styleScorerAgent);
    }

    private static class StyleScorerExecutor implements AgentExecutor {

        private final StyleScorer styleScorer;

        public StyleScorerExecutor(StyleScorer styleScorer) {
            this.styleScorer = styleScorer;
        }

        @Override
        public void execute(RequestContext context, EventQueue eventQueue) throws JSONRPCError {
            TaskUpdater updater = new TaskUpdater(context, eventQueue);
            updater.startWork();

            List<String> args = extractArguments(context.getMessage());
            String response = "" + styleScorer.scoreStyle(args.get(0), args.get(1));

            TextPart responsePart = new TextPart(response, null);
            List<Part<?>> parts = List.of(responsePart);
            updater.addArtifact(parts, null, null, null);
            updater.complete();
        }

        @Override
        public void cancel(RequestContext context, EventQueue eventQueue) throws JSONRPCError {
            Task task = context.getTask();
            if (task.getStatus().state() == TaskState.CANCELED
                    || task.getStatus().state() == TaskState.COMPLETED) {
                throw new TaskNotCancelableError();
            }
            new TaskUpdater(context, eventQueue).cancel();
        }

        private List<String> extractArguments(Message message) {
            if (message.getParts() != null) {
                return message.getParts().stream()
                        .filter(TextPart.class::isInstance)
                        .map(TextPart.class::cast)
                        .map(TextPart::getText)
                        .toList();
            }
            return List.of();
        }
    }
}
