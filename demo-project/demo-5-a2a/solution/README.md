# A2A Jakarta EE Server

Three WildFly-based A2A agents orchestrated with the [langchain4j](https://docs.langchain4j.dev/) agentic framework and the [A2A protocol](https://google.github.io/A2A/).

| Module | Role | Default Port |
|--------|------|--------------|
| `a2a-creative-writer` | A2A server – generates a short story from a topic | 8080 |
| `a2a-style-scorer` | A2A server – scores a story against a given style (0.0–1.0) | 8081 |
| `a2a-orchestrator` | Web app – calls both A2A servers, loops until score >= 0.8 | 8082 |

## Prerequisites

- Java 21
- Maven
- [Ollama](https://ollama.com/) running locally with the `qwen2.5:7b` model pulled:

```bash
ollama pull qwen2.5:7b
```

- The following artifacts installed in your local Maven repository (not yet on Maven Central):
  - `dev.langchain4j.cdi:langchain4j-cdi-core:1.1.0`
  - `dev.langchain4j.cdi:langchain4j-cdi-portable-ext:1.1.0`
  - `dev.langchain4j.cdi.mp:langchain4j-cdi-config:1.1.0`
  - `dev.langchain4j:langchain4j-agentic:1.13.0-beta23`
  - `dev.langchain4j:langchain4j-agentic-a2a:1.13.0-beta23`

## Build

Build all three modules from the repository root:

```bash
mvn clean package
```

Each module produces a self-contained WildFly server in its `target/server/` directory via [WildFly Glow](https://docs.wildfly.org/wildfly-glow/).

## Start the servers

Open three terminals and start each server with a port offset so they don't conflict:

**Terminal 1 – Creative Writer (port 8080)**

```bash
cd a2a-creative-writer
./target/server/bin/standalone.sh
```

**Terminal 2 – Style Scorer (port 8081)**

```bash
cd a2a-style-scorer
./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=1 -Da2a.server.url=http://localhost:8081
```

**Terminal 3 – Orchestrator (port 8082)**

```bash
cd a2a-orchestrator
./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=2
```

Wait for all three to print `WildFly ... started` before testing.

## Test

### Agent cards

Verify each A2A server exposes its agent card:

```bash
curl http://localhost:8080/.well-known/agent-card.json | jq .
curl http://localhost:8081/.well-known/agent-card.json | jq .
```

### Orchestrator REST endpoint

Call the orchestrator directly:

```bash
curl "http://localhost:8082/api/styled-story?topic=dragons+and+wizards&style=comedy" | jq .
```

The response contains the final story, style score, topic, and style:

```json
{
  "story": "...",
  "score": 0.85,
  "topic": "dragons and wizards",
  "style": "comedy"
}
```

### Web UI

Open [http://localhost:8082](http://localhost:8082) in a browser. Fill in a **Topic** and a **Style**, then click **Forge the Story**.

## How it works

```
                       ┌──────────────────────┐
                       │   a2a-orchestrator    │
                       │    (port 8082)        │
                       │                       │
  Browser ──GET───────►│  StyledWriterEndpoint │
                       │         │             │
                       │    sequence           │
                       │    ┌────┴────┐        │
                       │    │         │        │
                       │  step 1   step 2      │
                       │    │      (loop)       │
                       └────┼────────┼─────────┘
                            │        │
                     A2A    │        │  A2A + local LLM
                            │        │
               ┌────────────▼──┐  ┌──▼──────────────┐
               │creative-writer│  │  style-scorer    │
               │  (port 8080)  │  │  (port 8081)     │
               │               │  │                  │
               │ Ollama LLM    │  │ Ollama LLM       │
               └───────────────┘  └──────────────────┘
```

1. The orchestrator calls the **creative-writer** A2A server to generate a story from the topic.
2. It enters a **review loop** (max 5 iterations):
   - The **style-scorer** A2A server scores the story against the requested style.
   - If the score is below 0.8, a **local style editor** (running on the orchestrator's own Ollama model) rewrites the story.
3. Once the score reaches 0.8 or the loop exhausts its iterations, the final story is returned.

## Configuration

All configuration is in `src/main/resources/META-INF/microprofile-config.properties` for each module. Properties can be overridden at startup with `-D` system properties.

### A2A servers (creative-writer & style-scorer)

| Property | Default | Description |
|----------|---------|-------------|
| `a2a.server.url` | `http://localhost:8080` | URL advertised in the agent card |
| `dev.langchain4j.cdi.plugin.ollama.config.base-url` | `http://127.0.0.1:11434` | Ollama base URL |
| `dev.langchain4j.cdi.plugin.ollama.config.model-name` | `qwen2.5:7b` | Ollama model |

Example – run style-scorer on port 9090 with a different model:

```bash
./target/server/bin/standalone.sh \
  -Djboss.socket.binding.port-offset=1010 \
  -Da2a.server.url=http://localhost:9090 \
  -Ddev.langchain4j.cdi.plugin.ollama.config.model-name=llama3.2:3b
```

### Orchestrator

| Property | Default | Description |
|----------|---------|-------------|
| `a2a.creative-writer.url` | `http://localhost:8080` | Creative writer A2A server URL |
| `a2a.style-scorer.url` | `http://localhost:8081` | Style scorer A2A server URL |
| `ollama.base-url` | `http://127.0.0.1:11434` | Ollama base URL (for local style editor) |
| `ollama.model-name` | `qwen2.5:7b` | Ollama model (for local style editor) |

## Technology stack

- **Runtime**: [WildFly 39](https://www.wildfly.org/) (Jakarta EE 10 + MicroProfile 6.1)
- **AI framework**: [LangChain4j 1.13.0](https://docs.langchain4j.dev/) with [langchain4j-cdi](https://github.com/langchain4j/langchain4j-cdi)
- **Agent protocol**: [A2A (Agent-to-Agent)](https://google.github.io/A2A/) via [a2a-java-sdk](https://github.com/a2aproject/a2a-java-sdk)
- **Agentic orchestration**: [langchain4j-agentic](https://github.com/langchain4j/langchain4j) (sequence, loop, A2A client)
- **LLM provider**: [Ollama](https://ollama.com/) (local)
