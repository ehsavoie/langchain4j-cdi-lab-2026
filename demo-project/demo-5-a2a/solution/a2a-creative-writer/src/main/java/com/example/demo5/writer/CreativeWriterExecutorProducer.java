package com.example.demo5.writer;

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

        public CreativeWriterExecutor(CreativeWriter CreativeWriter) {
            this.creativeWriter = CreativeWriter;
        }

        @Override
        public void execute(RequestContext context, AgentEmitter emitter) throws A2AError {
            emitter.startWork();

            // extract the text from the message
            String userMessage = extractTextFromMessage(context.getMessage());

            // call the creative writer agent with the user's message
            String response = creativeWriter.generateStory(userMessage);

            System.out.println("CreativeWriterExecutor: Generated response: " + response);

            // create the response part
            TextPart responsePart = new TextPart(response);
            List<Part<?>> parts = List.of(responsePart);

            // add the response as an artifact and complete the task
            emitter.addArtifact(parts, null, null, null);
            emitter.complete();
        }

        @Override
        public void cancel(RequestContext context, AgentEmitter emitter) throws A2AError {
            Task task = context.getTask();

            if (task.status().state() == TaskState.TASK_STATE_CANCELED) {
                // task already cancelled
                throw new TaskNotCancelableError();
            }

            if (task.status().state() == TaskState.TASK_STATE_COMPLETED) {
                // task already completed
                throw new TaskNotCancelableError();
            }

            // cancel the task
            emitter.cancel();
        }

        private String extractTextFromMessage(Message message) {
            StringBuilder textBuilder = new StringBuilder();
            if (message.parts() != null) {
                for (Part part : message.parts()) {
                    if (part instanceof TextPart textPart) {
                        textBuilder.append(textPart.text());
                    }
                }
            }
            return textBuilder.toString();
        }
    }
}
