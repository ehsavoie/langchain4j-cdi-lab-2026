# Demo 1 - Injected AI Agent with @RegisterAIService

First demo at Devoxx France on **LangChain4j-CDI**: creating an injectable AI agent via CDI on WildFly with Ollama.

## Objective

Demonstrate that creating an injected AI agent requires:
1. An interface annotated with `@RegisterAIService`
2. Configuration via MicroProfile Config (zero LLM code!)
3. Injection of the service into a REST endpoint

## Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **Ollama** running with `ministral-3:3b`:
  ```bash
  ollama pull ministral-3:3b
  ollama serve
  ```

## Project structure

```
demo-1-ai-agent/
├── base/                          # Module for live coding
│   ├── src/main/java/com/example/demo1/
│   │   ├── JaxRsActivator.java
│   │   ├── ChatAssistant.java    # To complete (TODOs)
│   │   └── ChatResource.java     # To complete (TODOs)
│   └── src/main/
│       ├── resources/META-INF/
│       │   └── microprofile-config.properties  # To uncomment
│       └── webapp/
│           ├── WEB-INF/beans.xml
│           └── index.html         # Chat UI (ready!)
│
├── solution/                      # Complete solution
│   ├── src/main/java/com/example/demo1/
│   │   ├── JaxRsActivator.java
│   │   ├── ChatAssistant.java    # Complete
│   │   └── ChatResource.java     # Complete
│   └── src/main/
│       ├── resources/META-INF/
│       │   └── microprofile-config.properties  # Complete
│       └── webapp/
│           ├── WEB-INF/beans.xml
│           └── index.html         # Chat UI
│
└── pom.xml (aggregator)
```

## Launch

```bash
cd demo-1-ai-agent/base    # or solution/
mvn clean wildfly:dev
```

WildFly self-provisions via Galleon. The app is at **http://localhost:8080/demo-1/**

The chat UI is directly accessible -- no need for curl!

## Live Coding Walkthrough

### Step 1: Annotate ChatAssistant

Open `ChatAssistant.java` -- it is an empty interface with TODOs.

Add `@RegisterAIService`:
```java
import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;

@RegisterAIService(chatModelName = "my-model")
public interface ChatAssistant {

    @SystemMessage("""
        You are a Vegas stand-up comedian performing at a casino lounge.
        You tell jokes about tourists, slot machines, poker faces,
        and the glamorous chaos of Las Vegas.
        Your jokes are short, punchy, and full of showmanship.
        You can also tell funny anecdotes about life on the Vegas Strip.
        """)
    String chat(@UserMessage String userMessage);
}
```

### Step 2: Inject into ChatResource

Add `@Inject` and call the assistant:
```java
@Inject
ChatAssistant assistant;

@POST
public String chat(String message) {
    return assistant.chat(message);
}
```

### Step 3: Configure the Ollama model

Uncomment in `microprofile-config.properties`:
```properties
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

The pattern: `dev.langchain4j.cdi.plugin.<name>.class` for the type, then `.config.<property>` for the builder parameters.

### Step 4: Test

WildFly hot-reloads automatically. Open **http://localhost:8080/demo-1/** and test the chat in the UI.

Or via curl:
```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Tell me a joke about slot machines" \
  http://localhost:8080/demo-1/api/chat
```

## Key points to highlight

1. **CDI magic**: `@RegisterAIService` automatically produces a CDI bean
2. **Zero LLM code**: Everything is in MicroProfile Config configuration
3. **External configuration**: Switching models = changing a property
4. **Injectability = testability**: It is a CDI bean, mockable in tests

## Demo 2: Streaming with SSE (Server-Sent Events)

This demo shows how to use `TokenStream` to stream AI responses in real time via JAX-RS SSE.

### New files

- **`ChatAssistantStreaming.java`**: Interface annotated with `@RegisterAIService` that returns a `TokenStream`
- **`stream.html`**: Web interface that consumes SSE events and displays tokens in real time

### SSE endpoint

The new `/api/chat/stream` endpoint uses JAX-RS SSE with GET (because EventSource only supports GET):
```java
@GET
@Path("/stream")
@Produces(MediaType.SERVER_SENT_EVENTS)
public void chatStream(@QueryParam("message") String message, @Context SseEventSink sseEventSink, @Context Sse sse)
```

### Configuration

The streaming model uses `OllamaStreamingChatModel`:
```properties
dev.langchain4j.cdi.plugin.my-streaming-model.class=dev.langchain4j.model.ollama.OllamaStreamingChatModel
dev.langchain4j.cdi.plugin.my-streaming-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-streaming-model.config.model-name=ministral-3:3b
```

### Access

- **Simple chat**: http://localhost:8080/demo-1/
- **Streaming chat**: http://localhost:8080/demo-1/stream.html

### Key points about streaming

1. **TokenStream**: The interface returns a `TokenStream` instead of a `String`
2. **SSE Events**: Three types of events are emitted:
   - `token`: Each token generated by the model
   - `done`: End-of-generation signal
   - `error`: In case of an error
3. **EventSource**: The client uses the `EventSource` API to consume the events
4. **Real-time display**: Tokens are displayed as they are generated

## Troubleshooting

- **Ollama not responding**: `curl http://localhost:11434/api/tags`
- **Port 8080 in use**: `lsof -i :8080`
- **Deploy the solution**: `cd solution && mvn clean wildfly:dev`

## Resources

- **LangChain4j-CDI**: https://github.com/langchain4j/langchain4j-cdi
- **LangChain4j Docs**: https://docs.langchain4j.dev
- **WildFly**: https://www.wildfly.org
