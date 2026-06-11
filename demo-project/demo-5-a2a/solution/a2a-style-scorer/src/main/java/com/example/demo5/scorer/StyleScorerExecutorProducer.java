package com.example.demo5.scorer;

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
public class StyleScorerExecutorProducer {

    @Inject
    StyleScorer styleScorerAgent;

    @Produces
    public AgentExecutor agentExecutor() {
        return new StyleScorerExecutor(styleScorerAgent);
    }

    private static class StyleScorerExecutor implements AgentExecutor {

        private final StyleScorer styleScorer;

        public StyleScorerExecutor(StyleScorer StyleScorer) {
            this.styleScorer = StyleScorer;
        }

        @Override
        public void execute(RequestContext context, AgentEmitter emitter) throws A2AError {
            emitter.startWork();

            // extract the text from the message
            List<String> args = extractArguments(context.getMessage());

            // call the creative writer agent with the user's message
            String response = "" + styleScorer.scoreStyle(args.get(0), args.get(1));

            System.out.println("StyleScorerExecutor: Generated response: " + response);

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

        private List<String> extractArguments(Message message) {
            if (message.parts() != null) {
                return message.parts().stream()
                        .filter(TextPart.class::isInstance)
                        .map(TextPart.class::cast)
                        .map(TextPart::text)
                        .toList();
            }
            return List.of();
        }
    }
}
