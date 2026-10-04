# LangChain4j-CDI — Devoxx 2026

## Prerequisites

### Tools

| Tool | Version | Purpose |
|------|---------|---------|
| **JDK** | 21+ | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Git** | any | Clone the repository |
| **curl** | any | Test the REST APIs |
| **Docker / Podman** | any | Grafana LGTM stack (Exercise 2 only) |

> WildFly 39 is downloaded and provisioned **automatically** by the Maven plugin — no manual installation needed.

### LLM Provider

Choose **one** of the two options:

**Option A — Mistral AI (remote, free)**

Create an account at https://console.mistral.ai and export the key:

```bash
export MISTRAL_API_KEY=your-api-key-here   # Linux / macOS
$env:MISTRAL_API_KEY="your-api-key-here"   # Windows PowerShell
```

**Option B — Ollama (local)**

Install Ollama from https://ollama.com, then:

```bash
# In a first terminal (keep running during the entire workshop)
ollama serve

# In a second terminal, download the models
ollama pull ministral-3:3b   # Option B for demos 1, 3
ollama pull qwen2.5:7b       # Option B for demos 2, 5, 6
```

### Source Code

```bash
git clone https://github.com/ehsavoie/langchain4j-cdi-lab-2026.git
git checkout rivieradev
cd langchain4j-cdi-lab-2026/demo-project

# Download all Maven dependencies
mvn clean install -DskipTests
```

### Verify the Installation

```bash
cd demo-1-ai-agent/solution
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows

# In another terminal:
curl -X POST -H "Content-Type: text/plain" \
  -d "Sing me a heroic song" \
  http://localhost:8080/demo-1/api/chat
```

A response from the Viking skald confirms that the environment is ready. Stop with `Ctrl+C`.

---

## Workshop

Self-paced hands-on guide covering all 6 exercises step by step.

Open `workshop/index.html` directly in the browser.

## Structure

```
slides/          → Reveal.js presentation
  index.html     → Slides + speaker notes
introduction/    → Introduction slides (Devoxx)
  index.html     → Speaker introductions and context
workshop/        → Workshop hands-on guide
  index.html     → Self-paced tutorial
demo-project/    → Multi-module Maven project
  demo-1-ai-agent/         → Injectable AI Agent (@RegisterAIService)
  demo-2-ft-telemetry/     → Memory + RAG + Tools + Fault Tolerance + Telemetry
  demo-3-mcp/              → MCP (Model Context Protocol)
  demo-4-guardrails/       → Guardrails (input/output validation)
  demo-5-a2a/              → A2A (Agent-to-Agent Protocol)
  demo-6-supervisor/       → Supervisor pattern (multi-agent orchestration)
```

## Demos

| Demo | Topic | Model | Provider |
|------|-------|-------|----------|
| **Demo 1** | Injectable AI Agent — Viking Skald | `mistral-small-latest` | Mistral AI |
| **Demo 2** | Memory + RAG + Tools + Fault Tolerance + Telemetry | `mistral-small-latest` | Mistral AI |
| **Demo 3** | MCP — Hnefatafl dice game | `mistral-small-latest` | Mistral AI |
| **Demo 4** | Guardrails — input/output validation | `mistral-small-latest` | Mistral AI |
| **Demo 5** | A2A — Story Forge multi-agent pipeline | `mistral-small-latest` | Mistral AI |
| **Demo 6** | Supervisor — multi-agent orchestration | `mistral-small-latest` | Mistral AI |

### Demo 1 — Injectable AI Agent (`@RegisterAIService`)

Annotate a plain Java interface with `@RegisterAIService` and let LangChain4j-CDI turn it into an injectable CDI bean — no boilerplate. The demo exposes a Viking Skald that tells jokes, sings epic songs, and analyses images. Also demonstrates streaming responses via Server-Sent Events (SSE).

### Demo 2 — Memory, RAG, Tools, Fault Tolerance & Telemetry

A Viking expedition booking assistant that combines several production-grade concerns in one CDI bean: conversation memory, retrieval-augmented generation (RAG) over expedition data, Java tool calls (real booking logic), MicroProfile Fault Tolerance annotations (retry, circuit breaker, fallback), and distributed tracing/metrics exported to an OpenTelemetry collector.

### Demo 3 — MCP (Model Context Protocol)

An AI game master runs an Hnefatafl dice game using tools provided by an external MCP server (standalone Helidon 4 JAR). Shows how LangChain4j-CDI wires an `McpToolProvider` via configuration so the AI can invoke remote tools without any glue code.

### Demo 4 — Guardrails

Declarative input and output validation applied to an AI service. An input guardrail blocks off-topic requests before they reach the LLM; an output guardrail inspects and optionally rejects the model's response. No changes to business logic — validation is pure CDI decoration.

### Demo 5 — A2A (Agent-to-Agent Protocol)

Three WildFly instances cooperate over the A2A protocol. The **Creative Writer** (port 8080) drafts a Norse saga, the **Style Scorer** (port 8081) rates it, and the **Style Editor** rewrites it until the score reaches 0.8 — all wired by a declarative `@RegisterLoopAgent` / `@RegisterSequenceAgent` pipeline on the orchestrator (port 8082).

### Demo 6 — Supervisor Pattern

Same three-agent topology as Demo 5, but the explicit review loop and exit condition are replaced by a single `@RegisterSupervisorAgent`. The LLM supervisor autonomously decides which sub-agent to call next and when the story quality is sufficient, demonstrating emergent orchestration without hand-written control flow.

