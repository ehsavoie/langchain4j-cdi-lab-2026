# AGENT.md — Context for Claude Code

> This file helps an AI agent (Claude Code) understand the project and contribute effectively.
> Speakers: **Yann Blazart** & **Emmanuel Hugonnet** — Devoxx France 2026.

## Project

Devoxx France 2026 presentation on **LangChain4j-CDI**: integrating LangChain4j into Jakarta EE / MicroProfile via CDI.
The repository contains **Reveal.js slides** and a **multi-module Maven project** with 3 progressive demos.

License: Apache 2.0.

## Tech Stack

- **Java 21**, **Maven 3.8+**
- **Jakarta EE 10** + **MicroProfile 6.1**
- **WildFly 39** (provisioned via Galleon in the wildfly-maven-plugin)
- **LangChain4j 1.11.0** + **LangChain4j-CDI 1.0.0**
- **Ollama** locally with model `ministral-3:3b`
- **Reveal.js 5.1.0** for slides (CDN, no npm)

## Directory Structure

```
.
├── AGENT.md                 <- This file
├── README.md                <- Instructions for launching the slides
├── LICENSE                  <- Apache 2.0
├── slides/
│   ├── index.html           <- Reveal.js presentation (all-in-one)
│   └── (open index.html directly in the browser)
└── demo-project/
    ├── pom.xml              <- Parent POM (centralized versions)
    ├── README.md            <- Overall demo strategy
    ├── demo-1-ai-agent/     <- Injectable AI Agent (@RegisterAIService)
    │   ├── base/            <- Skeleton with TODOs for live coding
    │   └── solution/        <- Complete reference
    ├── demo-2-ft-telemetry/ <- Memory + Tools + Fault Tolerance + Telemetry
    │   ├── base/            <- Skeleton (FT as TODOs, Tools/Memory functional)
    │   └── solution/        <- Complete reference with FT
    └── demo-3-mcp/          <- MCP Integration (Model Context Protocol)
        ├── mcp-server/      <- Standalone MCP server (Helidon 4, endpoint /mcp)
        ├── base/            <- Skeleton
        └── solution/        <- Complete reference
```

## base / solution Convention

Each demo has two Maven modules:
- **base/**: skeleton with `// TODO STEP N` markers for live coding on stage
- **solution/**: complete code, serves as a safety net during the presentation

Shared business classes (models, repositories, tools) are **identical** in base and solution.
Only annotations/config targeted for live coding differ (TODOs in base, complete in solution).

**Rule**: any modification to a shared class must be made in both modules.

## Running the Demos

```bash
# Prerequisites: Ollama (in a separate terminal, keep running)
ollama serve
# In another terminal:
ollama pull ministral-3:3b

# Demo 1
cd demo-project/demo-1-ai-agent/solution
mvn clean install
./target/server/bin/standalone.sh          # Linux / macOS
target\server\bin\standalone.bat           # Windows
# -> http://localhost:8080/demo-1/

# Demo 2
cd demo-project/demo-2-ft-telemetry/solution
mvn clean install
./target/server/bin/standalone.sh          # Linux / macOS
target\server\bin\standalone.bat           # Windows
# -> http://localhost:8080/demo-2/

# Demo 3 (start the MCP server first on port 8090)
cd demo-project/demo-3-mcp/mcp-server
mvn clean package
java -jar target/casino-dice-roller.jar
cd ../solution
mvn clean install
./target/server/bin/standalone.sh          # Linux / macOS
target\server\bin\standalone.bat           # Windows
# -> http://localhost:8080/demo-3/
```

The context-root is defined by `<name>` in each POM's `wildfly-maven-plugin`.

## Slides

Open `slides/index.html` directly in the browser.
S key = Speaker view (notes), F = fullscreen, O = overview.

The slides are a single file `slides/index.html`. Speaker notes are in `<aside class="notes">` tags.

Navigation: horizontal slides = main sections, vertical slides = sub-sections (e.g. `/0/3` = section 0, sub-slide 3).

## LangChain4j-CDI Architecture (must know to modify the code)

### Configuration Pattern

LLM components are configured via MicroProfile Config with the prefix `dev.langchain4j.cdi.plugin.<name>`:

```properties
# Declare the component class
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
# Configure its properties (.config. prefix)
dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

**Important**: the `.config.` prefix is mandatory for builder properties. Without it, the property is silently ignored.

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

- `@MemoryId` requires a **ChatMemoryProvider** (not a ChatMemory)
- The provider must be a CDI bean `@Named` implementing `ChatMemoryProvider`
- **Critical**: use `ConcurrentHashMap.computeIfAbsent()` to cache memories per session, otherwise a new memory is created on every call

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

Tools are CDI beans `@ApplicationScoped` with methods annotated `@Tool`.
They can inject other beans via `@Inject`.

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

`@Tool` and `@P` descriptions are sent to the LLM: they must be clear and precise because the model uses them to decide when/how to call the tool.

### SPI and Resolution

LangChain4j-CDI uses an **SPI** (`LLMConfig`) discovered via `ServiceLoader`.
The **MicroProfile Config** implementation is provided by the `langchain4j-cdi-config` artifact (groupId: `dev.langchain4j.cdi.mp`).

### ChatMessage Hierarchy

`ChatMessage` is an interface without a `text()` method. Subtypes:
- `SystemMessage` -> `.text()`
- `UserMessage` -> `.singleText()`
- `AiMessage` -> `.text()` (may be null if tool calls)
- `ToolExecutionResultMessage` -> `.toolName()` + `.text()`

## Demo 2 — Specific Details

Demo 2 is the richest. It contains:

### RAG (Retrieval Augmented Generation)

- `KnowledgeBaseProvider.java`: CDI `@ApplicationScoped` producer creating a `ContentRetriever`
  - Uses `OllamaEmbeddingModel` (qwen2.5:7b) for embeddings
  - Stores in an `InMemoryEmbeddingStore<TextSegment>`
  - Ingests expeditions via `Expedition.toRagDocument()`
  - Produces a `@Named("my-rag") ContentRetriever` of type `EmbeddingStoreContentRetriever`
- `ChatAssistant.java`: references the ContentRetriever via `contentRetrieverName = "my-rag"` in `@RegisterAIService`
- RAG is functional in both base AND solution (it is not a live coding TODO)

### Expedition API (in-memory mock)

- `Expedition.java`: model (id, destination, departureDate, warriorSlots, enrollments)
- `ExpeditionRepository.java`: `@ApplicationScoped`, 3 pre-filled expeditions:
  - `iceland-raid`: Iceland Raid, 5 warrior slots (small to test the "full" scenario)
  - `england-conquest`: England Conquest, 30 warrior slots
  - `north-sea-exploration`: North Sea Exploration, 200 warrior slots

### Per-Tab Memory

Each browser tab generates a `SESSION_ID` via `crypto.randomUUID()`, sent in the `X-Session-Id` header. Two tabs = two independent conversations.

A "Memory" button in the UI opens a debug endpoint: `GET /api/chat/memory?sessionId=xxx`.

### Business Rules (in @SystemMessage)

- The user must provide first name AND last name
- They can only register themselves
- The AI handles expedition IDs internally (the user doesn't need to know them)
- Tools accept an exact ID or a partial destination (fallback `findByDestination`)

### Fault Tolerance (live coding topic)

In **base/** the FT annotations are TODOs. In **solution/**:
- `@Retry(maxRetries = 3, delay = 1000)`
- `@Timeout(value = 30, unit = ChronoUnit.SECONDS)`
- `@Fallback(fallbackMethod = "chatFallback")`
- `@CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)`

### Galleon Layers (WildFly Provisioning)

The demo-2 POM requires specific layers:
```xml
<layers>
    <layer>jaxrs-server</layer>
    <layer>microprofile-fault-tolerance</layer>
    <layer>microprofile-telemetry</layer>
</layers>
```

Without these: `ClassNotFoundException: org.eclipse.microprofile.faulttolerance.Retry`.

## Chat UI

Each demo has an `index.html` in `src/main/webapp/` with:
- Minimalist chat interface (HTMX-style, fetch API)
- Smooth auto-scroll (`scrollIntoView({ behavior: 'smooth', block: 'end' })`)
- Button animation (3 bouncing dots) while waiting for a response
- Demo-2 only: per-tab session ID + Memory debug button

**Rule**: the 6 `index.html` files (3 demos x 2 modules) must remain consistent for shared elements (style, animation, scroll).

## Known Pitfalls

| Problem | Cause | Solution |
|---------|-------|----------|
| `ClassNotFoundException: ...faulttolerance.Retry` | Missing Galleon layers | Add `microprofile-fault-tolerance` in `<layers>` |
| `IllegalConfigurationException: ...ChatMemoryProvider` | MicroProfile Config creates a ChatMemory, not a Provider | Use a CDI bean `ChatMemoryProviderBean` |
| Memory doesn't persist between messages | `get()` creates a new instance on each call | Use `computeIfAbsent()` in the provider |
| `ChatMessage.text()` doesn't compile | `ChatMessage` is an interface without `text()` | Pattern-match on subtypes (SystemMessage, UserMessage, etc.) |
| Incorrect context-root (404) | No `<name>` in wildfly-maven-plugin | Add `<name>demo-N</name>` in the plugin config |
| Properties ignored | Missing `.config.` prefix | `dev.langchain4j.cdi.plugin.X.config.prop=val` |
| AI doesn't show expedition IDs | Normal LLM behavior | The @SystemMessage tells the AI to manage IDs internally |

## Code Conventions

- Package: `com.example.demoN` (N = 1, 2, or 3)
- GroupId parent POM: `com.example`
- GroupId LangChain4j-CDI core: `dev.langchain4j.cdi`
- GroupId LangChain4j-CDI MicroProfile: `dev.langchain4j.cdi.mp`
- Language: English for code, comments, @SystemMessage, and UIs
- All modules have a `beans.xml` in `WEB-INF/` (required for CDI discovery)
- Each demo has a `JaxRsActivator.java` with `@ApplicationPath("api")`

## Useful Commands

```bash
# Verify Ollama is running
curl http://localhost:11434/api/tags

# Launch the slides: open slides/index.html in the browser

# Run a demo (replace N and the module name)
cd demo-project/demo-N-xxx/solution
mvn clean install
./target/server/bin/standalone.sh    # Linux / macOS
target\server\bin\standalone.bat     # Windows

# Test a chat endpoint
curl -X POST -H "Content-Type: text/plain" -d "Hello" http://localhost:8080/demo-1/api/chat

# Test with a session ID (demo-2)
curl -X POST -H "Content-Type: text/plain" -H "X-Session-Id: test-123" \
  -d "What expeditions are available?" http://localhost:8080/demo-2/api/chat

# Debug memory (demo-2)
curl "http://localhost:8080/demo-2/api/chat/memory?sessionId=test-123"
```
