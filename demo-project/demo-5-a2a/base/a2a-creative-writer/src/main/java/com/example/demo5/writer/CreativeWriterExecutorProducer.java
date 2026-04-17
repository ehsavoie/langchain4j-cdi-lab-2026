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

/**
 * Producteur CDI de l'AgentExecutor A2A pour le Creative Writer.
 * L'executor reçoit les requêtes A2A et délègue au service IA CreativeWriter.
 *
 * TODO: À compléter :
 * 1. Implémenter la méthode execute() de l'AgentExecutor
 * 2. Extraire le texte du message A2A entrant
 * 3. Appeler creativeWriter.generateStory() avec le texte extrait
 * 4. Créer un TextPart avec la réponse et l'ajouter comme artifact
 */
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
        public void execute(RequestContext context, EventQueue eventQueue) throws JSONRPCError {
            TaskUpdater updater = new TaskUpdater(context, eventQueue);

            // TODO ÉTAPE 1 : Marquer la tâche comme soumise et commencer le travail
            // if (context.getTask() == null) {
            //     updater.submit();
            // }
            // updater.startWork();

            // TODO ÉTAPE 2 : Extraire le texte du message A2A entrant
            // String userMessage = extractTextFromMessage(context.getMessage());

            // TODO ÉTAPE 3 : Appeler le service IA CreativeWriter
            // String response = creativeWriter.generateStory(userMessage);

            // TODO ÉTAPE 4 : Créer la réponse et compléter la tâche
            // TextPart responsePart = new TextPart(response, null);
            // List<Part<?>> parts = List.of(responsePart);
            // updater.addArtifact(parts, null, null, null);
            // updater.complete();
        }

        @Override
        public void cancel(RequestContext context, EventQueue eventQueue) throws JSONRPCError {
            Task task = context.getTask();

            if (task.getStatus().state() == TaskState.CANCELED) {
                throw new TaskNotCancelableError();
            }

            if (task.getStatus().state() == TaskState.COMPLETED) {
                throw new TaskNotCancelableError();
            }

            TaskUpdater updater = new TaskUpdater(context, eventQueue);
            updater.cancel();
        }

        private String extractTextFromMessage(Message message) {
            StringBuilder textBuilder = new StringBuilder();
            if (message.getParts() != null) {
                for (Part part : message.getParts()) {
                    if (part instanceof TextPart textPart) {
                        textBuilder.append(textPart.getText());
                    }
                }
            }
            return textBuilder.toString();
        }
    }
}
