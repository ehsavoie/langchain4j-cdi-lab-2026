# LangChain4j-CDI — Riviera Dev 2026

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
ollama pull ministral-3:3b   # demos 1, 3, 4
ollama pull qwen2.5:7b       # demo 2 (tool calling + embeddings), demo 5 (A2A)
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

Self-paced hands-on guide covering all 5 exercises step by step.

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
```

## Demos

Each demo contains a `base/` module (skeleton with TODOs for live coding) and `solution/` (complete reference).

| Demo | Topic | Theme | Model |
|------|-------|-------|-------|
| **Demo 1** | Injectable AI Agent (`@RegisterAIService`) | Viking Skald — jokes and epic sagas | `ministral-3:3b` |
| **Demo 2** | Memory + RAG + Tools + Fault Tolerance + Telemetry | Viking expedition enrollments | `qwen2.5:7b` |
| **Demo 3** | MCP (Model Context Protocol) | Hnefatafl dice game with Ragnar the Skald | `ministral-3:3b` |
| **Demo 4** | Guardrails (input/output validation) | Viking Skald with guardrails | `ministral-3:3b` |
| **Demo 5** | A2A (Agent-to-Agent Protocol) | Story Forge — multi-agent pipeline | `qwen2.5:7b` |

### Demo 1 — Viking Skald

```bash
cd demo-project/demo-1-ai-agent/solution
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows

# Test: Viking joke
curl -X POST -H "Content-Type: text/plain" \
  -d "Tell me a Viking joke" \
  http://localhost:8080/demo-1/api/chat

# Test streaming: epic saga
curl -X POST -H "Content-Type: text/plain" \
  -d "Compose an epic saga about Ragnar" \
  http://localhost:8080/demo-1/api/stream
```

### Demo 2 — Viking Expeditions

```bash
cd demo-project/demo-2-ft-telemetry/solution
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows

# Test: search for expeditions
curl -X POST -H "Content-Type: text/plain" \
  -H "X-Session-Id: session-odin" \
  -d "What expeditions are available?" \
  http://localhost:8080/demo-2/api/chat

# Test: enroll in an expedition
curl -X POST -H "Content-Type: text/plain" \
  -H "X-Session-Id: session-odin" \
  -d "Enroll me in the expedition to Lindisfarne" \
  http://localhost:8080/demo-2/api/chat
```

### Demo 3 — MCP Integration

```bash
# Start the MCP server (Helidon 4, port 8090)
cd demo-project/demo-3-mcp/mcp-server && mvn clean package && java -jar target/casino-dice-roller.jar

# Launch the demo
cd demo-project/demo-3-mcp/solution
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

### Demo 4 — Guardrails

```bash
cd demo-project/demo-4-guardrails/solution
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows

curl -X POST -H "Content-Type: text/plain" \
  -d "Sing me a Viking song" \
  http://localhost:8080/demo-4/api/chat
```

### Demo 5 — A2A Story Forge

```bash
# Terminal 1: Creative Writer (port 8080)
cd demo-project/demo-5-a2a/solution/a2a-creative-writer
mvn clean install
./target/server/bin/standalone.sh                                           # Linux / macOS
target\server\bin\standalone.bat                                            # Windows

# Terminal 2: Style Scorer (port 8081)
cd demo-project/demo-5-a2a/solution/a2a-style-scorer
mvn clean install
./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=1     # Linux / macOS
target\server\bin\standalone.bat -Djboss.socket.binding.port-offset=1      # Windows

# Terminal 3: Orchestrator (port 8082)
cd demo-project/demo-5-a2a/solution/a2a-orchestrator
mvn clean install
./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=2     # Linux / macOS
target\server\bin\standalone.bat -Djboss.socket.binding.port-offset=2      # Windows

curl "http://localhost:8082/api/styled-story?topic=Erik+the+Red+crosses+the+seas&style=epic"
```
