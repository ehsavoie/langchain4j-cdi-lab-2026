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

/**
 * Producteur CDI de l'AgentExecutor A2A pour le Style Scorer.
 * L'executor reçoit les requêtes A2A et délègue au service IA StyleScorer.
 *
 * TODO: À compléter :
 * 1. Implémenter la méthode execute() de l'AgentExecutor
 * 2. Extraire les arguments (story, style) du message A2A entrant
 * 3. Appeler styleScorer.scoreStyle() avec les arguments extraits
 * 4. Créer un TextPart avec le score et l'ajouter comme artifact
 */
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
        public void execute(RequestContext context, EventQueue eventQueue) throws JSONRPCError {
            TaskUpdater updater = new TaskUpdater(context, eventQueue);

            // TODO ÉTAPE 1 : Marquer la tâche comme soumise et commencer le travail
            // if (context.getTask() == null) {
            //     updater.submit();
            // }
            // updater.startWork();

            // TODO ÉTAPE 2 : Extraire les arguments (story et style) du message A2A
            // List<String> args = extractArguments(context.getMessage());

            // TODO ÉTAPE 3 : Appeler le service IA StyleScorer
            // String response = "" + styleScorer.scoreStyle(args.get(0), args.get(1));

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
