package com.example.demo6.writer;

import org.a2aproject.sdk.server.agentexecution.AgentExecutor;
import org.a2aproject.sdk.server.agentexecution.RequestContext;
import org.a2aproject.sdk.server.tasks.AgentEmitter;
import org.a2aproject.sdk.spec.A2AError;
import org.a2aproject.sdk.spec.Message;
import org.a2aproject.sdk.spec.Part;
import org.a2aproject.sdk.spec.Task;
import org.a2aproject.sdk.spec.TaskNotCancelableError;
import org.a2aproject.sdk.spec.TaskState;
import org.a2aproject.sdk.spec.TextPart;
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
        public void execute(RequestContext context, AgentEmitter emitter) throws A2AError {
            emitter.startWork();

            String userMessage = extractTextFromMessage(context.getMessage());
            String response = creativeWriter.generateStory(userMessage);

            TextPart responsePart = new TextPart(response);
            List<Part<?>> parts = List.of(responsePart);
            emitter.addArtifact(parts, null, null, null);
            emitter.complete();
        }

        @Override
        public void cancel(RequestContext context, AgentEmitter emitter) throws A2AError {
            Task task = context.getTask();
            if (task.status().state() == TaskState.TASK_STATE_CANCELED
                    || task.status().state() == TaskState.TASK_STATE_COMPLETED) {
                throw new TaskNotCancelableError();
            }
            emitter.cancel();
        }

        private String extractTextFromMessage(Message message) {
            StringBuilder sb = new StringBuilder();
            if (message.parts() != null) {
                for (Part part : message.parts()) {
                    if (part instanceof TextPart tp) {
                        sb.append(tp.text());
                    }
                }
            }
            return sb.toString();
        }
    }
}
