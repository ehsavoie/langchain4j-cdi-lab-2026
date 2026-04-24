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
│   │   ├── JaxRsActivator.java           # @ApplicationPath("/api") — ready
│   │   ├── ChatAssistant.java            # TODO: Add @RegisterAIService
│   │   ├── ChatAssistantStreaming.java   # TODO: Add @RegisterAIService (streaming)
│   │   ├── ChatResource.java             # TODO: Replace @PostConstruct with @Inject
│   │   └── ImageAnalyzerServlet.java     # Bonus: vision model (TODOs inside)
│   └── src/main/
│       ├── resources/META-INF/
│       │   └── microprofile-config.properties  # Pre-configured — switch Mistral/Ollama as needed
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
    │   ├── ChatResource.java             # With @Inject
    │   └── ImageAnalyzerServlet.java     # Vision analysis implemented
    └── src/main/
        ├── resources/META-INF/
        │   └── microprofile-config.properties
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

## Live Coding Walkthrough

The `base/` module starts with a working but non-CDI version: `ChatResource` manually builds
the LLM models using `AiServices.builder()` in a `@PostConstruct` method. The goal is to
replace all that boilerplate with CDI injection.

### Step 1: Add @RegisterAIService to ChatAssistant

Open `ChatAssistant.java`. Follow the TODO comments: uncomment the import, then add the annotation:

```java
import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(chatModelName = "my-model")
public interface ChatAssistant {

    @SystemMessage("...")
    String chat(@UserMessage String userMessage);
}
```

`chatModelName = "my-model"` references the plugin name in `microprofile-config.properties`.
CDI will now produce a managed bean for this interface automatically.

### Step 2: Add @RegisterAIService to ChatAssistantStreaming

Open `ChatAssistantStreaming.java`. Same pattern, but using `streamingChatModelName`:

```java
import dev.langchain4j.cdi.spi.RegisterAIService;

@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@RegisterAIService(streamingChatModelName = "my-streaming-model")
public interface ChatAssistantStreaming {

    @SystemMessage("...")
    TokenStream chatStream(@UserMessage String userMessage);
}
```

The method returns `TokenStream` — tokens arrive one by one from the LLM.

### Step 3: Refactor ChatResource to use @Inject

Open `ChatResource.java`. Follow the TODO comments: this is the main refactoring step.

**3a. Replace the imports** — remove the four LangChain4j/PostConstruct imports and add `@Inject`:

```java
// Remove these:
// import dev.langchain4j.model.mistralai.MistralAiChatModel;
// import dev.langchain4j.model.mistralai.MistralAiStreamingChatModel;
// import dev.langchain4j.service.AiServices;
// import jakarta.annotation.PostConstruct;

// Add this:
import jakarta.inject.Inject;
```

**3b. Replace the private fields with @Inject fields**:

```java
// Before:
private ChatAssistant assistant;
private ChatAssistantStreaming streamingAssistant;

// After:
@Inject
ChatAssistant assistant;

@Inject
ChatAssistantStreaming streamingAssistant;
```

**3c. Delete the entire @PostConstruct init() method** — all 22 lines of manual model construction. CDI injects the beans before the first request; no initialization code is needed.

The `chat()` and `chatStream()` methods stay untouched.

### Step 4: Choose the LLM provider

`microprofile-config.properties` is already configured. By default it uses **Mistral AI** (requires `MISTRAL_API_KEY`). To switch to **Ollama**, comment out the three Mistral lines and uncomment the three Ollama lines for each model block (`my-model`, `my-streaming-model`, `vision-model`).

```properties
# Active by default — Mistral AI:
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.mistralai.MistralAiChatModel
dev.langchain4j.cdi.plugin.my-model.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.my-model.config.model-name=mistral-small-latest

# Switch to Ollama by commenting above and uncommenting below:
#dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
#dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
#dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

**Critical**: the `.config.` prefix in property keys is mandatory — without it, properties are silently ignored.

### Step 5: Build and test

```bash
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

Open **http://localhost:8080/demo-1/** and test the chat UI.

Or via curl:
```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Raconte-moi une blague sur les raiders vikings" \
  http://localhost:8080/demo-1/api/chat
```

## Bonus: Streaming with SSE (Server-Sent Events)

`ChatAssistantStreaming` returns a `TokenStream` instead of a `String`. The `chatStream()`
endpoint in `ChatResource` wires it to JAX-RS Server-Sent Events — tokens appear in the
browser as they arrive from the LLM.

- **Simple chat**: http://localhost:8080/demo-1/
- **Streaming chat**: http://localhost:8080/demo-1/stream.html

## Bonus: Vision / Image Analysis

`ImageAnalyzerServlet.java` contains its own TODO steps (ÉTAPE 1–6) for injecting a
vision-capable `ChatModel` via `@Named` and sending an image to the LLM:

```java
@Inject
@Named("vision-model")
ChatModel visionModel;
```

The vision model is pre-configured in `microprofile-config.properties` (`pixtral-large-latest`
for Mistral AI, or `ministral-3:3b` for Ollama).

- **Vision UI**: http://localhost:8080/demo-1/image.html

## Key Takeaways

1. **CDI magic**: `@RegisterAIService` automatically produces a CDI bean — no factory, no builder
2. **Zero LLM code**: everything is in MicroProfile Config; switching models = changing a property
3. **External configuration**: same interface, swap Ollama for Mistral in one line
4. **Injectability = testability**: it is a CDI bean, can be mocked in tests like any other bean

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
