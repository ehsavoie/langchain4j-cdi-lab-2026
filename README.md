# LangChain4j-CDI — {{conference_name}}

Hands-on workshop: integrating LangChain4j into Jakarta EE / MicroProfile via CDI.
Speakers: **Yann Blazart** & **Emmanuel Hugonnet**.

## Presented At

| Conference | Year |
|------------|------|
| JavaOne | 2026 |
| Devoxx France | 2026 |
| RivieraDev | 2026 |
| Volcamp | 2026 |
| Devoxx Belgium | 2026 |

## Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| **JDK** | 21+ | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Git** | any | Clone the repository |
| **curl** | any | Test the REST APIs |
| **Docker / Podman** | any | Grafana LGTM stack (Exercise 2 only) |

> WildFly 39 is downloaded and provisioned **automatically** by the Maven plugin.

### LLM Provider

**Option A — Mistral AI (remote, free)**

Create an account at https://console.mistral.ai and export the key:

```bash
export MISTRAL_API_KEY=your-api-key-here   # Linux / macOS
$env:MISTRAL_API_KEY="your-api-key-here"   # Windows PowerShell
```

**Option B — Ollama (local)**

Install Ollama from https://ollama.com, then:

```bash
ollama serve                         # keep running in a dedicated terminal
ollama pull ministral-3:3b           # demos 1, 3, 4
ollama pull qwen2.5:7b               # demo 2 (tools + embeddings), demo 5 (A2A)
```

### Get the Source

```bash
git clone https://github.com/ehsavoie/langchain4j-cdi-lab-2026.git
cd langchain4j-cdi-lab-2026/demo-project
mvn clean install -DskipTests
```

## Workshop

Open `workshop/index.html` in your browser for the self-paced hands-on guide.

## Structure

```
slides/          → Reveal.js presentation
introduction/    → Introduction slides
workshop/        → Self-paced tutorial
demo-project/
  demo-1-ai-agent/         → Injectable AI Agent (@RegisterAIService)
  demo-2-ft-telemetry/     → Memory + RAG + Tools + Fault Tolerance + Telemetry
  demo-3-mcp/              → MCP (Model Context Protocol)
  demo-4-guardrails/       → Guardrails (input/output validation)
  demo-5-a2a/              → A2A (Agent-to-Agent Protocol)
```

Each demo has a `base/` module (skeleton with TODOs) and a `solution/` module (complete reference).

## Demos

| Demo | Topic | Model |
|------|-------|-------|
| **Demo 1** | Injectable AI Agent — Viking Skald | `ministral-3:3b` |
| **Demo 2** | Memory + RAG + Tools + Fault Tolerance + Telemetry | `qwen2.5:7b` |
| **Demo 3** | MCP — Hnefatafl dice game | `ministral-3:3b` |
| **Demo 4** | Guardrails — input/output validation | `ministral-3:3b` |
| **Demo 5** | A2A — Story Forge multi-agent pipeline | `qwen2.5:7b` |
