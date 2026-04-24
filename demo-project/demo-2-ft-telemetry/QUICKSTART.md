# Demo 2 - Quick Start Guide

## Quick Start (2 minutes)

### 1. Start the observability stack

```bash
podman run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
```

> **Note**: Use `docker` instead of `podman` if you prefer Docker.

Leave this terminal open. Wait for the logs indicating Grafana is ready (about 10-15 seconds).

### 2. Start the application

**In a new terminal**:

```bash
cd demo-2-ft-telemetry/solution/
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

### 3. Test the application

Open http://localhost:8080/demo-2/ and send a few messages:

- "What sessions are available?"
- "Register Yann for the workshop"
- "How many seats are left?"

### 4. View traces in Grafana

1. Open http://localhost:3000
2. Left menu -> **Explore** (compass icon)
3. At the top -> Select **Tempo**
4. Query type -> **Search**
5. Service Name -> `demo-2-langchain4j-cdi`
6. **Run query** -> Click on a trace to explore!

## Testing Resilience

### Scenario: Stop Ollama

```bash
# In a terminal
killall ollama
```

Send a message in the chat -> you will see the fallback activate!

In Grafana, the traces will show:
- Automatic retries (3 attempts)
- Timeout after 30 seconds
- Fallback activation

### Restart Ollama

```bash
ollama serve
```

The circuit breaker closes automatically.

## What You Will See in the Traces

- **Span "chat.generate"**: The LLM call
  - Total duration
  - Input/output tokens
  - Model used
- **Span "tool.execute"**: Each Tool call
  - Tool name
  - Parameters
  - Result
- **Span "retry"**: Each retry attempt
- **Custom attributes**:
  - `llm.model`: model name
  - `llm.tokens.input`: tokens consumed
  - `llm.tokens.output`: tokens generated
  - `tool.name`: name of the called function

## Stopping Everything

```bash
# In the WildFly terminal: Ctrl+C

# In the Grafana LGTM terminal: Ctrl+C
# (the --rm flag automatically cleans up the container)
```

## Tips

### View collector logs

Logs are displayed directly in the terminal where you launched `podman run` (interactive mode with `-ti`).

### Access backends directly

- **Grafana**: http://localhost:3000
- **Tempo**: http://localhost:3200
- **Loki**: http://localhost:3100
- **Mimir**: http://localhost:9009

### Common Issues

**"No traces found" in Grafana**:
- Check that the app is sending requests
- Wait 10-20 seconds (collector buffering)
- Check the logs in the terminal where Grafana LGTM is running

**The app does not start**:
- Check Ollama: `curl http://localhost:11434`
- Check the ports: `lsof -i :8080`

**Grafana LGTM does not start**:
- Check that the ports are not in use: `lsof -i :3000 -i :4317 -i :4318`
- Update the image: `podman pull grafana/otel-lgtm` (or `docker pull`)
