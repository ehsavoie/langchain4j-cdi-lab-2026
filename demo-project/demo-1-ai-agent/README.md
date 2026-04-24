# Demo 1 - Injectable AI Agent with @RegisterAIService

First demo at Devoxx France on **LangChain4j-CDI**: creating an injectable AI agent via CDI on WildFly with Ollama or Mistral AI.

## Objective

Demonstrate that creating an injected AI agent requires:
1. An interface annotated with `@RegisterAIService`
2. Configuration via MicroProfile Config (zero LLM boilerplate!)
3. Injection of the service into a REST endpoint with `@Inject`

**Key message**: "From 15 lines of boilerplate to 1 annotation + 1 config"

## Prerequisites

- **Java 21+**, **Maven 3.8+**
- **Ollama** (local) or a **Mistral AI API key** (remote)

```bash
# Option A: Ollama (local)
ollama pull ministral-3:3b
ollama serve

# Option B: Mistral AI (remote)
export MISTRAL_API_KEY=your-key-here
```

## Project Structure

```
demo-1-ai-agent/
├── base/                          # Live coding skeleton (TODOs)
│   ├── src/main/java/com/example/demo1/
│   │   ├── JaxRsActivator.java           # @ApplicationPath("/api")
│   │   ├── ChatAssistant.java            # TODO: Add @RegisterAIService
│   │   ├── ChatAssistantStreaming.java   # TODO: Add @RegisterAIService (streaming)
│   │   ├── ChatResource.java             # TODO: @Inject + call the assistant
│   │   └── ImageAnalyzerServlet.java     # Bonus: vision model (TODOs)
│   └── src/main/
│       ├── resources/META-INF/
│       │   └── microprofile-config.properties  # TODO: uncomment model config
│       └── webapp/
│           ├── WEB-INF/beans.xml
│           ├── index.html         # Chat UI (ready!)
│           ├── stream.html        # SSE streaming UI (ready!)
│           └── image.html         # Vision UI (ready!)
│
└── solution/                      # Complete reference implementation
    ├── src/main/java/com/example/demo1/
    │   ├── JaxRsActivator.java
    │   ├── ChatAssistant.java            # With @RegisterAIService
    │   ├── ChatAssistantStreaming.java   # With @RegisterAIService
    │   └── ChatResource.java             # With @Inject
    └── src/main/
        ├── resources/META-INF/
        │   └── microprofile-config.properties  # All models configured
        └── webapp/
            ├── WEB-INF/beans.xml
            ├── index.html
            ├── stream.html
            └── image.html
```

## Launch

```bash
cd demo-project/demo-1-ai-agent/base    # or solution/
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

WildFly is provisioned via Galleon during `mvn clean install`. The app is at **http://localhost:8080/demo-1/**

The chat UI is directly accessible — no curl needed to demo!

## Live Coding Walkthrough

### Step 1: Add @RegisterAIService to ChatAssistant

Open `ChatAssistant.java` — it is an empty interface with a system message already set.

Add `@RegisterAIService` and the necessary imports:

```java
import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAIService(chatModelName = "my-model")
public interface ChatAssistant {

    @SystemMessage("""
        Tu es un skald viking qui raconte des blagues et des histoires drôles dans la grande salle.
        Tes blagues portent sur les guerriers maladroits, les raids qui tournent mal,
        les festins trop arrosés, les dieux nordiques et leurs facéties.
        Tes blagues sont courtes, percutantes et font rire tout le monde.
        """)
    String chat(@UserMessage String userMessage);
}
```

### Step 2: Inject into ChatResource

Open `ChatResource.java` and inject the assistant:

```java
@Inject
ChatAssistant assistant;

@POST
@Consumes(MediaType.TEXT_PLAIN)
@Produces(MediaType.TEXT_PLAIN)
public String chat(String message) {
    return assistant.chat(message);
}
```

### Step 3: Configure the model

Uncomment the chosen backend in `microprofile-config.properties`:

```properties
# Option A: Ollama (local)
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b

# Option B: Mistral AI (remote)
# dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.mistralai.MistralAiChatModel
# dev.langchain4j.cdi.plugin.my-model.config.api-key=${MISTRAL_API_KEY}
# dev.langchain4j.cdi.plugin.my-model.config.model-name=mistral-small-latest
```

The pattern: `dev.langchain4j.cdi.plugin.<name>.class` for the implementation type, then `.config.<property>` for each builder parameter.

**Critical**: the `.config.` prefix is mandatory — without it, properties are silently ignored.

### Step 4: Test

WildFly hot-reloads automatically. Open **http://localhost:8080/demo-1/** and test the chat in the UI.

Or via curl:
```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Raconte-moi une blague sur les raiders vikings" \
  http://localhost:8080/demo-1/api/chat
```

## Bonus: Streaming with SSE (Server-Sent Events)

Demo 1 also includes a streaming variant that shows AI responses token by token in real time.

### Key interface

`ChatAssistantStreaming.java` returns a `TokenStream` instead of a `String`:

```java
@RegisterAIService(chatModelName = "my-streaming-model")
public interface ChatAssistantStreaming {

    @SystemMessage("""
        Tu es un skald viking, un conteur et poète de la grande salle.
        Tu chantes des récits épiques sur les batailles glorieuses...
        """)
    TokenStream chatStream(@UserMessage String userMessage);
}
```

### Configuration

```properties
# Streaming model (OllamaStreamingChatModel or MistralAiStreamingChatModel)
dev.langchain4j.cdi.plugin.my-streaming-model.class=dev.langchain4j.model.ollama.OllamaStreamingChatModel
dev.langchain4j.cdi.plugin.my-streaming-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-streaming-model.config.model-name=ministral-3:3b
```

### Access

- **Simple chat**: http://localhost:8080/demo-1/
- **Streaming chat**: http://localhost:8080/demo-1/stream.html
- **Vision (bonus)**: http://localhost:8080/demo-1/image.html

### How SSE works

The `/api/chat/stream` endpoint uses JAX-RS SSE (GET, because `EventSource` only supports GET):

```java
@GET @Path("/stream")
@Produces(MediaType.SERVER_SENT_EVENTS)
public void chatStream(@QueryParam("message") String message,
                       @Context SseEventSink sink, @Context Sse sse)
```

Three event types are emitted:
- `token` — each token as it's generated
- `done` — end-of-stream signal
- `error` — if an error occurs

## Bonus: Vision / Image Analysis

`ImageAnalyzerServlet.java` demonstrates injecting a vision-capable `ChatModel` via `@Named`:

```java
@Inject
@Named("vision-model")
ChatModel visionModel;
```

The vision model is configured separately in `microprofile-config.properties`:

```properties
dev.langchain4j.cdi.plugin.vision-model.class=dev.langchain4j.model.mistralai.MistralAiChatModel
dev.langchain4j.cdi.plugin.vision-model.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.vision-model.config.model-name=pixtral-large-latest
```

## Key Takeaways

1. **CDI magic**: `@RegisterAIService` automatically produces a CDI bean — no factory, no builder
2. **Zero LLM code**: Everything is in MicroProfile Config; switching models = changing a property
3. **External configuration**: Same interface, swap Ollama for Mistral in one line
4. **Injectability = testability**: It is a CDI bean, can be mocked in tests like any other bean

## Troubleshooting

- **Ollama not responding**: `curl http://localhost:11434/api/tags`
- **Port 8080 in use**: `lsof -i :8080`
- **Model not found**: `ollama pull ministral-3:3b`
- **Run the solution directly**: `cd solution && mvn clean install` then `./target/server/bin/standalone.sh` (Linux/macOS) or `target\server\bin\standalone.bat` (Windows)

## Resources

- **LangChain4j-CDI**: https://github.com/langchain4j/langchain4j-cdi
- **LangChain4j Docs**: https://docs.langchain4j.dev
- **MicroProfile Config**: https://microprofile.io/project/eclipse/microprofile-config
- **WildFly**: https://www.wildfly.org
