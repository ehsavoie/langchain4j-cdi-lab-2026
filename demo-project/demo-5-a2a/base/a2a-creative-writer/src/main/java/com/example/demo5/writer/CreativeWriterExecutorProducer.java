package com.example.demo5.writer;

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
public class CreativeWriterExecutorProducer {

    @Inject
    CreativeWriter creativeWriterAgent;

    @Produces
    public AgentExecutor agentExecutor() {
        return new CreativeWriterExecutor(creativeWriterAgent);
    }

    private static class CreativeWriterExecutor implements AgentExecutor {

        private final CreativeWriter creativeWriter;

        public CreativeWriterExecutor(CreativeWriter creativeWriter) {
            this.creativeWriter = creativeWriter;
        }

        @Override
        public void execute(RequestContext context, EventQueue eventQueue) throws JSONRPCError {
            TaskUpdater updater = new TaskUpdater(context, eventQueue);
            updater.startWork();

            String userMessage = extractTextFromMessage(context.getMessage());
            String response = creativeWriter.generateStory(userMessage);

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

        private String extractTextFromMessage(Message message) {
            StringBuilder sb = new StringBuilder();
            if (message.getParts() != null) {
                for (Part part : message.getParts()) {
                    if (part instanceof TextPart tp) {
                        sb.append(tp.getText());
                    }
                }
            }
            return sb.toString();
        }
    }
}
