# AGENT.md — Contexte pour Claude Code

> Ce fichier permet à un agent IA (Claude Code) de comprendre le projet et de contribuer efficacement.
> Intervenants : **Yann Blazart** & **Emmanuel Hugonnet** — Devoxx France 2026.

## Projet

Présentation Devoxx France 2026 sur **LangChain4j-CDI** : intégration de LangChain4j dans Jakarta EE / MicroProfile via CDI.
Le dépôt contient des **slides Reveal.js** et un **projet Maven multi-modules** avec 3 démos progressives.

Licence : Apache 2.0.

## Tech Stack

- **Java 21**, **Maven 3.8+**
- **Jakarta EE 10** + **MicroProfile 6.1**
- **WildFly 39** (provisioned via Galleon in the wildfly-maven-plugin)
- **LangChain4j 1.11.0** + **LangChain4j-CDI 1.0.0**
- **Ollama** locally with model `ministral-3:3b`
- **Reveal.js 5.1.0** for slides (CDN, no npm)

## Structure des Répertoires

```
.
├── AGENT.md                 <- Ce fichier
├── README.md                <- Instructions de lancement des slides
├── LICENSE                  <- Apache 2.0
├── slides/
│   ├── index.html           <- Présentation Reveal.js (tout-en-un)
│   └── (ouvrir index.html directement dans le navigateur)
└── demo-project/
    ├── pom.xml              <- POM parent (versions centralisées)
    ├── README.md            <- Stratégie globale des démos
    ├── demo-1-ai-agent/     <- Agent IA injectable (@RegisterAIService)
    │   ├── base/            <- Squelette avec TODOs pour le live coding
    │   └── solution/        <- Référence complète
    ├── demo-2-ft-telemetry/ <- Memory + Tools + Fault Tolerance + Telemetry
    │   ├── base/            <- Squelette (FT en TODOs, Tools/Memory fonctionnels)
    │   └── solution/        <- Référence complète avec FT
    └── demo-3-mcp/          <- Intégration MCP (Model Context Protocol)
        ├── mcp-server/      <- Serveur MCP standalone (JAR, JSON-RPC stdio)
        ├── base/            <- Squelette
        └── solution/        <- Référence complète
```

## Convention base / solution

Chaque démo possède deux modules Maven :
- **base/** : squelette avec marqueurs `// TODO STEP N` pour le live coding sur scène
- **solution/** : code complet, sert de filet de sécurité pendant la présentation

Les classes métier partagées (modèles, repositories, tools) sont **identiques** dans base et solution.
Seules les annotations/config faisant l'objet du live coding diffèrent (TODOs dans base, complet dans solution).

**Règle** : toute modification d'une classe partagée doit être faite dans les deux modules.

## Lancer les Démos

```bash
# Prérequis : Ollama en cours d'exécution avec ministral-3:3b
ollama pull ministral-3:3b
ollama serve

# Demo 1
cd demo-project/demo-1-ai-agent/solution
mvn clean wildfly:dev
# -> http://localhost:8080/demo-1/

# Demo 2
cd demo-project/demo-2-ft-telemetry/solution
mvn clean wildfly:dev
# -> http://localhost:8080/demo-2/

# Demo 3 (compiler d'abord le serveur MCP)
cd demo-project/demo-3-mcp/mcp-server
mvn clean package
cd ../solution
mvn clean wildfly:dev
# -> http://localhost:8080/demo-3/
```

Le context-root est défini par `<name>` dans le `wildfly-maven-plugin` de chaque POM.

## Slides

Ouvrir `slides/index.html` directement dans le navigateur.
Touche S = Vue présentateur (notes), F = plein écran, O = vue d'ensemble.

Les slides constituent un fichier unique `slides/index.html`. Les notes pour le présentateur sont dans les balises `<aside class="notes">`.

Navigation : slides horizontales = sections principales, slides verticales = sous-sections (ex. `/0/3` = section 0, sous-slide 3).

## Architecture LangChain4j-CDI (à connaître pour modifier le code)

### Pattern de Configuration

Les composants LLM sont configurés via MicroProfile Config avec le préfixe `dev.langchain4j.cdi.plugin.<name>` :

```properties
# Déclarer la classe du composant
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
# Configurer ses propriétés (préfixe .config.)
dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

**Important** : le préfixe `.config.` est obligatoire pour les propriétés de builder. Sans lui, la propriété est ignorée.

### @RegisterAIService

```java
@RegisterAIService(
    chatModelName = "my-model",              // references MicroProfile config
    chatMemoryProviderName = "my-memory",    // CDI @Named bean (optional)
    tools = BookingTools.class               // tool classes (optional)
)
public interface ChatAssistant {
    @SystemMessage("...")
    String chat(@MemoryId String sessionId, @UserMessage String message);
}
```

### ChatMemoryProvider vs ChatMemory

- `@MemoryId` nécessite un **ChatMemoryProvider** (pas un ChatMemory)
- Le provider doit être un bean CDI `@Named` implémentant `ChatMemoryProvider`
- **Critique** : utiliser `ConcurrentHashMap.computeIfAbsent()` pour mettre en cache les mémoires par session, sinon une nouvelle mémoire est créée à chaque appel

```java
@ApplicationScoped
@Named("my-memory")
public class ChatMemoryProviderBean implements ChatMemoryProvider {
    private final Map<Object, ChatMemory> memories = new ConcurrentHashMap<>();

    @Override
    public ChatMemory get(Object memoryId) {
        return memories.computeIfAbsent(memoryId, id ->
            MessageWindowChatMemory.builder().id(id).maxMessages(20).build());
    }
}
```

### Tools (function calling)

Les tools sont des beans CDI `@ApplicationScoped` avec des méthodes annotées `@Tool`.
Ils peuvent injecter d'autres beans via `@Inject`.

```java
@ApplicationScoped
public class ExpeditionTools {
    @Inject ExpeditionRepository repository;

    @Tool("Enroll a warrior in a Viking expedition")
    public String enrollWarrior(
        @P("Expedition ID") String expeditionId,
        @P("Warrior first name") String firstName,
        @P("Warrior last name") String lastName) { ... }
}
```

Les descriptions `@Tool` et `@P` sont envoyées au LLM : elles doivent être claires et précises car le modèle les utilise pour décider quand/comment appeler le tool.

### SPI et Résolution

LangChain4j-CDI utilise un **SPI** (`LLMConfig`) découvert via `ServiceLoader`.
L'implémentation **MicroProfile Config** est fournie par l'artefact `langchain4j-cdi-config` (groupId: `dev.langchain4j.cdi.mp`).

### Hiérarchie ChatMessage

`ChatMessage` est une interface sans méthode `text()`. Sous-types :
- `SystemMessage` -> `.text()`
- `UserMessage` -> `.singleText()`
- `AiMessage` -> `.text()` (peut être null si appels de tools)
- `ToolExecutionResultMessage` -> `.toolName()` + `.text()`

## Demo 2 — Détails Spécifiques

La démo 2 est la plus riche. Elle contient :

### RAG (Retrieval Augmented Generation)

- `KnowledgeBaseProvider.java` : producteur CDI `@ApplicationScoped` créant un `ContentRetriever`
  - Utilise `OllamaEmbeddingModel` (qwen2.5:7b) pour les embeddings
  - Stocke dans un `InMemoryEmbeddingStore<TextSegment>`
  - Ingère les expéditions via `Expedition.toRagDocument()`
  - Produit un `@Named("my-rag") ContentRetriever` de type `EmbeddingStoreContentRetriever`
- `ChatAssistant.java` : référence le ContentRetriever via `contentRetrieverName = "my-rag"` dans `@RegisterAIService`
- Le RAG est fonctionnel dans base ET solution (ce n'est pas un TODO de live coding)

### API d'Expédition (mock en mémoire)

- `Expedition.java` : modèle (id, destination, departureDate, warriorSlots, enrollments)
- `ExpeditionRepository.java` : `@ApplicationScoped`, 3 expéditions pré-remplies :
  - `iceland-raid` : Iceland Raid, 5 warrior slots (petit pour tester le scénario "complet")
  - `england-conquest` : England Conquest, 30 warrior slots
  - `north-sea-exploration` : North Sea Exploration, 200 warrior slots

### Mémoire par Onglet

Chaque onglet de navigateur génère un `SESSION_ID` via `crypto.randomUUID()`, envoyé dans l'en-tête `X-Session-Id`. Deux onglets = deux conversations indépendantes.

Un bouton "Memory" dans l'IU ouvre un endpoint de debug : `GET /api/chat/memory?sessionId=xxx`.

### Règles Métier (dans @SystemMessage)

- L'utilisateur doit fournir prénom ET nom
- Il ne peut enregistrer que lui-même
- L'IA gère les IDs d'expédition en interne (l'utilisateur n'a pas besoin de les connaître)
- Les tools acceptent un ID exact ou une destination partielle (fallback `findByDestination`)

### Fault Tolerance (sujet de live coding)

Dans **base/** les annotations FT sont en TODOs. Dans **solution/** :
- `@Retry(maxRetries = 3, delay = 1000)`
- `@Timeout(value = 30, unit = ChronoUnit.SECONDS)`
- `@Fallback(fallbackMethod = "chatFallback")`
- `@CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)`

### Layers Galleon (Provisioning WildFly)

Le POM de demo-2 nécessite des layers spécifiques :
```xml
<layers>
    <layer>jaxrs-server</layer>
    <layer>microprofile-fault-tolerance</layer>
    <layer>microprofile-telemetry</layer>
</layers>
```

Sans ceux-ci : `ClassNotFoundException: org.eclipse.microprofile.faulttolerance.Retry`.

## IU de Chat

Chaque démo possède un `index.html` dans `src/main/webapp/` avec :
- Interface de chat minimaliste (style HTMX, fetch API)
- Auto-scroll fluide (`scrollIntoView({ behavior: 'smooth', block: 'end' })`)
- Animation du bouton (3 points rebondissants) en attendant la réponse
- Demo-2 uniquement : ID de session par onglet + bouton de debug Memory

**Règle** : les 6 fichiers `index.html` (3 démos x 2 modules) doivent rester cohérents pour les éléments partagés (style, animation, scroll).

## Pièges Connus

| Problème | Cause | Solution |
|---------|-------|----------|
| `ClassNotFoundException: ...faulttolerance.Retry` | Layers Galleon manquantes | Ajouter `microprofile-fault-tolerance` dans `<layers>` |
| `IllegalConfigurationException: ...ChatMemoryProvider` | MicroProfile Config crée un ChatMemory, pas un Provider | Utiliser un bean CDI `ChatMemoryProviderBean` |
| La mémoire ne persiste pas entre les messages | `get()` crée une nouvelle instance à chaque appel | Utiliser `computeIfAbsent()` dans le provider |
| `ChatMessage.text()` ne compile pas | `ChatMessage` est une interface sans `text()` | Pattern-match sur les sous-types (SystemMessage, UserMessage, etc.) |
| context-root incorrect (404) | Pas de `<name>` dans wildfly-maven-plugin | Ajouter `<name>demo-N</name>` dans la config du plugin |
| Propriétés ignorées | Préfixe `.config.` manquant | `dev.langchain4j.cdi.plugin.X.config.prop=val` |
| L'IA n'affiche pas les IDs d'expédition | Comportement normal du LLM | Le @SystemMessage dit à l'IA de gérer les IDs en interne |

## Conventions de Code

- Package : `com.example.demoN` (N = 1, 2 ou 3)
- GroupId POM parent : `com.example`
- GroupId LangChain4j-CDI core : `dev.langchain4j.cdi`
- GroupId LangChain4j-CDI MicroProfile : `dev.langchain4j.cdi.mp`
- Langue : Anglais pour le code, commentaires, @SystemMessage, et IUs
- Tous les modules ont un `beans.xml` dans `WEB-INF/` (requis pour la découverte CDI)
- Chaque démo a un `JaxRsActivator.java` avec `@ApplicationPath("api")`

## Commandes Utiles

```bash
# Vérifier qu'Ollama est en cours d'exécution
curl http://localhost:11434/api/tags

# Lancer les slides : ouvrir slides/index.html dans le navigateur

# Lancer une démo (remplacer N et le module)
cd demo-project/demo-N-xxx/solution && mvn clean wildfly:dev

# Tester un endpoint de chat
curl -X POST -H "Content-Type: text/plain" -d "Hello" http://localhost:8080/demo-1/api/chat

# Tester avec un ID de session (demo-2)
curl -X POST -H "Content-Type: text/plain" -H "X-Session-Id: test-123" \
  -d "What expeditions are available?" http://localhost:8080/demo-2/api/chat

# Debugger la mémoire (demo-2)
curl "http://localhost:8080/demo-2/api/chat/memory?sessionId=test-123"
```
