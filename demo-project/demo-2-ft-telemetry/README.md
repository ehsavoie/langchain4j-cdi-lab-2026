# Demo 2 - Fault Tolerance + Telemetry

Second Devoxx France demo: adding **resilience** (MicroProfile Fault Tolerance) and **observability** (OpenTelemetry) to a LangChain4j-CDI AI agent that already uses Memory, RAG, and Tools.

## Goals

- Show that AI Services are **full-fledged CDI beans** — MicroProfile interceptors apply automatically
- Use **Tools** (`@Tool`) for function calling: list, enroll, cancel, and check expedition slots
- Add **resilience** with `@Retry`, `@Timeout`, `@Fallback`, `@CircuitBreaker`
- Observe **distributed traces** in Grafana/Tempo via OpenTelemetry

**Key message**: "AI Services = CDI beans → all MicroProfile interceptors apply for free"

## Prerequisites

- **Java 21+**, **Maven 3.8+**
- **Ollama** (local) or a **Mistral AI API key** (remote)

```bash
# Option A: Ollama (local) — tool calling requires a capable model
ollama pull qwen2.5:7b
ollama serve

# Option B: Mistral AI (remote)
export MISTRAL_API_KEY=your-key-here
```

- **Docker or Podman** — for the Grafana LGTM observability stack

## Project Structure

```
demo-2-ft-telemetry/
├── base/                          # Live coding skeleton (FT annotations as TODOs)
│   ├── src/main/java/com/example/demo2/
│   │   ├── JaxRsActivator.java
│   │   ├── ChatAssistant.java             # TODO: Add @Retry, @Timeout, @Fallback, @CircuitBreaker
│   │   ├── ChatMemoryProviderBean.java    # Complete — per-session conversation memory
│   │   ├── ChatResource.java              # Complete — REST endpoints
│   │   ├── Expedition.java               # Complete — domain model
│   │   ├── ExpeditionRepository.java     # Complete — in-memory data store
│   │   ├── ExpeditionTools.java          # Complete — 5 @Tool methods
│   │   └── ExpeditionDetailsProvider.java # Complete — RAG content retriever
│   └── src/main/webapp/
│       ├── WEB-INF/beans.xml
│       └── index.html             # Chat UI with session memory
│
└── solution/                      # Complete reference implementation
    ├── src/main/java/com/example/demo2/
    │   ├── ChatAssistant.java             # With all 4 FT annotations + fallback
    │   └── (all other files identical to base)
    └── src/main/webapp/
        ├── WEB-INF/beans.xml
        └── index.html
```

## Launch

### 1. Start the Grafana LGTM stack first

Start this before the application so it's ready when you reach the telemetry step:

```bash
# Podman
podman run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm

# Or Docker
docker run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
```

**Grafana LGTM** bundles the full observability stack:
- **OpenTelemetry Collector** — receives traces on ports 4317 (gRPC) and 4318 (HTTP)
- **Tempo** — distributed trace storage
- **Loki** — log storage
- **Mimir** — metrics storage
- **Grafana** — unified visualization at http://localhost:3000

### 2. Start the application

```bash
cd demo-project/demo-2-ft-telemetry/base    # or solution/
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

The app is available at **http://localhost:8080/demo-2/**.

### 3. Observe traces in Grafana

After sending a few messages:

1. Open **http://localhost:3000** (Grafana)
2. Go to **Explore** (compass icon in the left menu)
3. Select the **Tempo** datasource
4. Choose **Search** as query type
5. Filter by `Service Name = demo-2-langchain4j-cdi`
6. Click any trace to see:
   - LLM call latency and token counts (input/output)
   - Tool calls and their duration
   - RAG retrieval steps
   - Errors and retries from Fault Tolerance

## What's Already Working in `base/`

The base module ships with Memory, RAG, and Tools fully wired — the only thing missing is the Fault Tolerance annotations:

| Feature | Class | Description |
|---|---|---|
| Memory | `ChatMemoryProviderBean` | Per-session `MessageWindowChatMemory` (20 messages), identified by `X-Session-Id` header |
| RAG | `ExpeditionDetailsProvider` | Ingests expedition data into an in-memory vector store using Mistral embeddings |
| Tools | `ExpeditionTools` | 5 `@Tool` methods: `listExpeditions`, `enrollWarrior`, `cancelEnrollment`, `remainingSlots`, `myEnrollments` |

Test that everything works before adding Fault Tolerance:

```bash
curl -X POST -H "Content-Type: text/plain" \
  -H "X-Session-Id: test-123" \
  -d "Quelles expéditions sont disponibles ?" \
  http://localhost:8080/demo-2/api/chat
```

## Live Coding Walkthrough

### Step 1: Add @Retry

Open `ChatAssistant.java`. The interface already has `@RegisterAIService` with memory, RAG, and tools.

Add `@Retry` on the `chat` method:

```java
import org.eclipse.microprofile.faulttolerance.Retry;

// existing @RegisterAIService + @SystemMessage stay as-is
@Retry(maxRetries = 3, delay = 1000)
String chat(@MemoryId String sessionId, @UserMessage String message);
```

Because `@RegisterAIService` produces a CDI bean, the CDI container wraps the proxy with the Fault Tolerance interceptor — no wiring needed.

### Step 2: Add @Timeout and @Fallback

```java
import org.eclipse.microprofile.faulttolerance.*;
import java.time.temporal.ChronoUnit;

@Retry(maxRetries = 3, delay = 1000)
@Timeout(value = 30, unit = ChronoUnit.SECONDS)
@Fallback(fallbackMethod = "chatFallback")
String chat(@MemoryId String sessionId, @UserMessage String message);

default String chatFallback(String sessionId, String message) {
    return "Oups ! Le LLM fait une sieste. Veuillez réessayer dans un moment.";
}
```

### Step 3: Add @CircuitBreaker

```java
@Retry(maxRetries = 3, delay = 1000)
@Timeout(value = 30, unit = ChronoUnit.SECONDS)
@Fallback(fallbackMethod = "chatFallback")
@CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)
String chat(@MemoryId String sessionId, @UserMessage String message);
```

### Step 4: Uncomment the Fault Tolerance dependency

Open `pom.xml` and uncomment the `langchain4j-cdi-fault-tolerance` dependency (marked with `TODO`):

```xml
<dependency>
    <groupId>dev.langchain4j.cdi.mp</groupId>
    <artifactId>langchain4j-cdi-fault-tolerance</artifactId>
</dependency>
```

Without this dependency, the annotations are present but no interceptor is registered — they are silently ignored.

### Step 5: Test resilience

Stop Ollama (or disconnect the network) and send messages. You should see:
- Retry attempts in WildFly logs
- Then the fallback message in the chat UI

Restart Ollama → the circuit breaker resets and normal operation resumes.

```bash
# Kill Ollama to simulate an outage
pkill -f ollama

# Send requests — should get fallback after retries
curl -X POST -H "Content-Type: text/plain" \
  -H "X-Session-Id: test-resilience" \
  -d "Quelles expéditions sont disponibles ?" \
  http://localhost:8080/demo-2/api/chat
```

## Configuration

### LangChain4j Model

```properties
# Option A: Mistral AI (remote)
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.mistralai.MistralAiChatModel
dev.langchain4j.cdi.plugin.my-model.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.my-model.config.model-name=mistral-small-latest

# Option B: Ollama (local) — use a model that supports tool calling
# dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
# dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
# dev.langchain4j.cdi.plugin.my-model.config.model-name=qwen2.5:7b
```

### OpenTelemetry

```properties
otel.exporter.otlp.endpoint=http://localhost:4318
otel.exporter.otlp.protocol=http/protobuf
otel.service.name=demo-2-langchain4j-cdi
otel.traces.exporter=otlp
otel.metrics.exporter=otlp
otel.logs.exporter=otlp

# LangChain4j-CDI telemetry listeners
dev.langchain4j.cdi.plugin.my-model.config.listeners=\
    dev.langchain4j.cdi.telemetry.SpanChatModelListener,\
    dev.langchain4j.cdi.telemetry.MetricsChatModelListener
```

**Ports exposed by Grafana LGTM**:
- `3000`: Grafana UI
- `4317`: OTLP gRPC
- `4318`: OTLP HTTP (default for this demo)

### Fault Tolerance overrides via config (optional)

```properties
# Override annotation values without recompiling
ChatAssistant/chat/Retry/maxRetries=5
ChatAssistant/chat/Timeout/value=45000
```

## Key Takeaways

1. **AI Services = CDI Beans**: `@RegisterAIService` makes the service a real CDI bean → MicroProfile FT interceptors apply without any extra configuration
2. **Declarative resilience**: 4 annotations replace ~200 lines of retry/timeout/breaker logic
3. **Tools = Regular Beans**: `ExpeditionTools` is `@ApplicationScoped` — inject any CDI service into it
4. **Full observability**: SpanChatModelListener + MetricsChatModelListener capture LLM calls, tool invocations, and RAG steps in OpenTelemetry traces

## Troubleshooting

- **Fallback activating immediately**: Check that the Ollama model supports tool calling (`qwen2.5:7b` recommended)
- **Timeout during chat**: Increase `@Timeout` to 60 seconds for slower models
- **CircuitBreaker open after test**: Restart Ollama and wait a few seconds for the circuit to close
- **No traces in Grafana**: Verify Grafana LGTM is running (`curl http://localhost:4318`) and `otel.sdk.disabled=false`
- **Fault Tolerance annotations ignored**: Ensure the `langchain4j-cdi-fault-tolerance` dependency is uncommented in `pom.xml`
- **Run the solution directly**: `cd solution && mvn clean install` then `./target/server/bin/standalone.sh` (Linux/macOS) or `target\server\bin\standalone.bat` (Windows)

## Stopping Services

```bash
# Stop WildFly
Ctrl+C in the WildFly terminal

# Stop Grafana LGTM (--rm cleans up automatically)
Ctrl+C in the podman/docker run terminal
```

## Resources

- **MicroProfile Fault Tolerance**: https://microprofile.io/project/eclipse/microprofile-fault-tolerance
- **MicroProfile Telemetry**: https://microprofile.io/specifications/microprofile-telemetry/
- **LangChain4j-CDI**: https://github.com/langchain4j/langchain4j-cdi
- **OpenTelemetry**: https://opentelemetry.io
- **Grafana LGTM Stack**: https://grafana.com/docs/lgtm/
