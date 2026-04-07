# Demo 2 - Fault Tolerance + Telemetry

Second Devoxx France demo: adding **resilience** (MicroProfile Fault Tolerance) and **telemetry** (OpenTelemetry) to LangChain4j-CDI AI Services.

## Goals

- Show that AI Services are **full-fledged CDI beans**
- Use **Tools** for function calling
- Add **resilience** with `@Retry`, `@Timeout`, `@Fallback`, `@CircuitBreaker`
- Observe **telemetry** in action

## Prerequisites

- Demo 1 working
- **Ollama** with `ministral-3:3b`:
  ```bash
  ollama pull ministral-3:3b
  ollama serve
  ```
- **Grafana LGTM Stack** (optional, to observe traces):
  ```bash
  # Start the full LGTM stack (Loki + Grafana + Tempo + Mimir)
  # with integrated OpenTelemetry Collector
  podman run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm

  # Or with Docker
  docker run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
  ```

## Project Structure

```
demo-2-ft-telemetry/
├── base/                          # TODO version (to complete)
│   ├── src/main/java/com/example/demo2/
│   │   ├── JaxRsActivator.java
│   │   ├── ChatAssistant.java    # TODOs for FT
│   │   ├── BookingTools.java     # Tools (complete)
│   │   └── ChatResource.java
│   └── src/main/webapp/
│       ├── WEB-INF/beans.xml
│       └── index.html             # Chat UI with FT hints
│
├── solution/                      # Complete version
│   ├── src/main/java/com/example/demo2/
│   │   ├── ChatAssistant.java    # With FT annotations
│   │   ├── BookingTools.java
│   │   └── ChatResource.java
│   └── src/main/webapp/
│       ├── WEB-INF/beans.xml
│       └── index.html             # UI with "stop Ollama" tip
│
└── README.md
```

## Running

### 1. Start the Grafana LGTM stack (optional)

```bash
podman run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
```

> **Note**: Use `docker` instead of `podman` if you prefer Docker.

**Grafana LGTM** (Loki, Grafana, Tempo, Mimir) is an all-in-one stack that provides:
- **OpenTelemetry Collector** built-in: receives traces on ports 4317 (gRPC) and 4318 (HTTP)
- **Tempo**: distributed trace storage
- **Loki**: log storage
- **Mimir**: metrics storage
- **Grafana**: unified visualization at http://localhost:3000

Benefits:
- Zero configuration - everything is pre-configured
- Single command (podman/docker run)
- Automatic correlation between traces, logs, and metrics
- Pre-configured datasources in Grafana

### 2. Start the application

```bash
cd demo-2-ft-telemetry/base    # or solution/
mvn clean wildfly:dev
```

The app is available at **http://localhost:8080/demo-2/** with a built-in chat UI.

The UI shows the available Tools (findSessions, reservePlace, placesRestantes) and the FT annotations to add.

### 3. Observe traces in Grafana

After sending a few messages in the chat:

1. **Open Grafana**: http://localhost:3000
2. Go to **Explore** (compass icon in the left menu)
3. Select the **Tempo** datasource
4. In "Query type", choose **Search**
5. Filter by `Service Name = demo-2-langchain4j-cdi`
6. Click on a trace to see:
   - LLM call latency
   - Tokens consumed (input/output)
   - Tool calls (function calling)
   - Errors and retries
   - Correlation with logs and metrics

## Live Coding Walkthrough

### Step 1: Verify the agent with Tools

The agent already has Tools in `BookingTools.java`:
```java
@ApplicationScoped
public class BookingTools {
    @Tool("Searches for Devoxx France sessions for a given date")
    public List<String> findSessions(@P("Date") String date) { ... }

    @Tool("Registers a seat for a session")
    public String reservePlace(@P("Name") String name, @P("Session") String session) { ... }

    @Tool("Number of remaining seats")
    public int placesRestantes(@P("Session") String session) { ... }
}
```

Test: "What sessions are on Day 1?" or "Register Yann for a session".

### Step 2: Ask the critical question

"What happens if Ollama stops?" -> Exception, timeout, 500 error.

### Step 3: Add @Retry + @Timeout

```java
@Retry(maxRetries = 3, delay = 1000)
@Timeout(value = 30, unit = ChronoUnit.SECONDS)
@SystemMessage(...)
String chat(@UserMessage String message);
```

### Step 4: Add @Fallback

```java
@Fallback(fallbackMethod = "chatFallback")
String chat(@UserMessage String message);

default String chatFallback(String message) {
    return "Oops! The LLM is taking a nap. Please try again in a moment.";
}
```

### Step 5: Add @CircuitBreaker

```java
@CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)
```

### Step 6: Test resilience

Stop Ollama (`killall ollama`) and send messages in the UI. The fallback activates!

Restart Ollama -> the circuit closes, everything works again.

## Configuration

### LangChain4j Model

```properties
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

### OpenTelemetry

```properties
# MicroProfile OpenTelemetry - OTLP Exporter
otel.exporter.otlp.endpoint=http://localhost:4318
otel.exporter.otlp.protocol=http/protobuf
otel.service.name=demo-2-langchain4j-cdi
otel.traces.exporter=otlp
otel.metrics.exporter=otlp
otel.logs.exporter=otlp

# LangChain4j-CDI Telemetry - Listeners
dev.langchain4j.cdi.plugin.my-model.config.listeners=\
    dev.langchain4j.cdi.telemetry.SpanChatModelListener,\
    dev.langchain4j.cdi.telemetry.MetricsChatModelListener
```

**Ports exposed by Grafana LGTM**:
- `3000`: Grafana UI (visualization)
- `4317`: OTLP gRPC endpoint (for the application)
- `4318`: OTLP HTTP endpoint (used by default)
- `3100`: Loki (logs)
- `3200`: Tempo (traces)
- `9009`: Mimir (metrics)

**What the traces capture**:
- LLM call latency (response time)
- Tokens consumed (input/output)
- Tool calls (function calling with parameters)
- Errors and retries (MicroProfile Fault Tolerance)
- Full context of each request

**Visualization in Grafana**:
- Tempo Dashboard: http://localhost:3000/explore
- Search by service, operation, duration, error
- Correlation traces <-> logs <-> metrics

### Fault Tolerance (optional override)

```properties
ChatAssistant/chat/Retry/maxRetries=5
ChatAssistant/chat/Timeout/value=45000
```

## Key Takeaways

1. **AI Services = CDI Beans**: MicroProfile FT annotations work directly
2. **Tools = Regular Beans**: Inject any CDI service into BookingTools
3. **Declarative resilience**: 4 annotations instead of 200 lines of code
4. **Production-ready**: Fault Tolerance + Telemetry = app that stays up

## Troubleshooting

- **Timeout during chat**: Increase `@Timeout` to 60 seconds
- **CircuitBreaker open**: Restart Ollama and wait a few seconds
- **Deploy the solution**: `cd solution && mvn clean wildfly:dev`

## Stopping services

```bash
# Stop WildFly
# Ctrl+C in the wildfly:dev terminal

# Stop Grafana LGTM
# Ctrl+C in the podman/docker run terminal
# (the --rm flag automatically cleans up the container)
```

## Resources

- **MicroProfile Fault Tolerance**: https://microprofile.io/project/eclipse/microprofile-fault-tolerance
- **MicroProfile Telemetry**: https://microprofile.io/specifications/microprofile-telemetry/
- **LangChain4j-CDI**: https://github.com/langchain4j/langchain4j-cdi
- **OpenTelemetry**: https://opentelemetry.io
- **Grafana LGTM Stack**: https://grafana.com/docs/lgtm/
- **Grafana Tempo**: https://grafana.com/oss/tempo/
- **Grafana Loki**: https://grafana.com/oss/loki/
- **Grafana Mimir**: https://grafana.com/oss/mimir/
