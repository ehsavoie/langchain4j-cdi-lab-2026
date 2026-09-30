# LangChain4j-CDI — RivieraDev 2026 Demos

**Branch** : `rivieradev`

## Setup before the big day

### 1. Choose your LLM provider

**Option A — Mistral AI (remote)**: sign up for free at [console.mistral.ai](https://console.mistral.ai/) to get an API key. No local installation required.

**Option B — Ollama (local)**: install [Ollama](https://ollama.ai), then download the models:

```bash
# In a first terminal (keep running):
ollama serve

# In a second terminal:
ollama pull ministral-3:3b    # exercises 1, 2, 3, 4
ollama pull qwen2.5:7b        # exercise 5 & 6 (A2A — always required)
```

Ollama must stay running on `http://localhost:11434` during the entire workshop.

### 2. Get the code

```bash
git clone https://github.com/ehsavoie/langchain4j-cdi-lab-2026.git
cd langchain4j-cdi-lab-2026/demo-project
git checkout rivieradev
```

### 3. WildFly (automatic)

WildFly 39 is automatically downloaded and provisioned by the Maven plugin. No manual installation needed:

```bash
cd demo-1-ai-agent/solution
mvn clean install

# Start the server:
./target/server/bin/standalone.sh    # Linux / macOS
target\server\bin\standalone.bat     # Windows
```

### 4. Verify your installation

```bash
# Build all modules to download dependencies
mvn clean install -DskipTests

# Quick verification test
cd demo-1-ai-agent/solution
mvn clean install
./target/server/bin/standalone.sh          # Linux / macOS
target\server\bin\standalone.bat           # Windows

# In another terminal:
curl -X POST -H "Content-Type: text/plain" \
  -d "Sing me a heroic song" \
  http://localhost:8080/demo-1/api/chat
```

You can also test directly in your browser at [http://localhost:8080/demo-1](http://localhost:8080/demo-1).

### 5. IDE

- Open `demo-project/` as a Maven project
- Make sure JDK 21 is configured
- Configure the integrated terminal for Maven commands

### 6. Grafana LGTM (Exercise 2 only)

```bash
podman run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
# Or with Docker:
docker run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm
```

---

## Workshop plan

| Exercise | Topic | Module |
|----------|-------|--------|
| **Exercise 1** | Injectable AI Agent — `@RegisterAIService`, streaming, multimodal vision | `demo-1-ai-agent/` |
| **Exercise 2** | Fault Tolerance + Telemetry — Memory, RAG, Tools, `@Retry`, `@CircuitBreaker`, OpenTelemetry | `demo-2-ft-telemetry/` |
| **Exercise 3** | MCP Integration — Model Context Protocol, external tool providers | `demo-3-mcp/` |
| **Exercise 4** | Guardrails — Input/output validation, language filtering, content safety | `demo-4-guardrails/` |
| **Exercise 5** | Agent-to-Agent (A2A) — Multi-agent orchestration, A2A protocol, Java/Jakarta SDK | `demo-5-a2a/` |
| **Exercise 6** | Supervisor Agent — LLM-driven orchestration with `@RegisterSupervisorAgent` | `demo-6-supervisor/` |

### Exercise strategy

Each module contains:
- `base/` — the starting code (skeleton with TODOs) for live coding
- `solution/` — the complete working version, as a backup

**Live**: work in `base/`, follow the TODOs
**If something breaks**: `cd solution && mvn clean install` then start the server and move on

---

## LLM provider configuration

Each module contains a `microprofile-config.properties` structured like this:

```properties
# ---- Option A: Mistral AI (remote) ---- [ACTIVE BY DEFAULT]
dev.langchain4j.cdi.plugin.my-model.class=...MistralAiChatModel
dev.langchain4j.cdi.plugin.my-model.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.my-model.config.model-name=mistral-small-latest

# ---- Option B: Ollama (local) ----
# dev.langchain4j.cdi.plugin.my-model.class=...OllamaChatModel
# dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
# dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

By default, **Option A (Mistral AI) is active**. To switch to Ollama: comment the 3 Option A lines, uncomment the 3 Option B lines.

```bash
# Export your Mistral API key (Option A only):
export MISTRAL_API_KEY=your-api-key-here
```

---

## Exercise 1: Injectable AI Agent

**Key message**: "From 15 lines of boilerplate to 1 annotation + 1 config"

```bash
cd demo-1-ai-agent/base
```

**Live steps:**
1. Show the empty skeleton of `ChatAssistant.java`
2. Add `@RegisterAIService(chatModelName = "my-model")`
3. Add `@SystemMessage` with the prompt
4. Open `ChatResource.java`, replace manual wiring with `@Inject ChatAssistant`
5. Verify the config in `microprofile-config.properties`
6. `mvn clean install` then start the server
7. Test:
```bash
curl -X POST http://localhost:8080/demo-1/api/chat \
  -H "Content-Type: text/plain" \
  -d "Tell me a Viking joke!"
```
8. Add streaming with `ChatAssistantStreaming` + `TokenStream`
9. Add vision with `ImageAnalyzerServlet` + `@Named("vision-model")`

---

## Exercise 2: Fault Tolerance + Telemetry + Tools

**Key message**: "MicroProfile annotations work on AI Services because they are CDI beans"

```bash
cd demo-2-ft-telemetry/base
```

**Live steps:**
1. Show that the agent with Memory + RAG + Tools already works
2. Add `@Retry(maxRetries = 3, delay = 1000)`
3. Add `@Timeout(value = 30, unit = ChronoUnit.SECONDS)`
4. Add `@Fallback(fallbackMethod = "chatFallback")`
5. Add `@CircuitBreaker(requestVolumeThreshold = 5, failureRatio = 0.5)`
6. Uncomment the `langchain4j-cdi-fault-tolerance` dependency in `pom.xml`
7. Kill Ollama or disconnect wifi — the fallback responds!
8. Restart — the retry works
9. Open Grafana at http://localhost:3000 → Explore → Tempo

---

## Exercise 3: MCP (Model Context Protocol)

**Key message**: "MCP is JDBC for AI — your agents talk to any tool"

```bash
# First build the MCP server
cd demo-3-mcp/mcp-server
mvn clean package
java -jar target/casino-dice-roller.jar

# Then work in base/
cd ../base
```

**Live steps:**
1. Show the MCP server — exposed tools for dice rolling
2. Create the `@RegisterAIService` with `toolProviderName = "mcp"`
3. Configure MCP via the WildFly AI subsystem in `standalone.xml`
4. Deploy and test the Hnefatafl dice game

---

## Exercise 4: Guardrails

**Key message**: "Validate input and output of your AI agents with CDI beans"

```bash
cd demo-4-guardrails/base
```

**Live steps:**
1. Add `@Named` on each guardrail bean
2. Implement `init()` with `OptimaizeLangDetector().loadModels()`
3. Implement `validate()` for each guardrail (english-input, fantasy-input, english-output, fantasy-output)
4. Wire `inputGuardrailNames` + `outputGuardrailNames` in `@RegisterAIService`
5. Test: French request → rejected, English with elves → rejected, valid English → accepted

---

## Exercise 5: Agent-to-Agent (A2A)

**Key message**: "Microservices, but for AI agents — CDI was built for this"

```bash
cd demo-5-a2a/base
```

Three independent WildFly services communicating via A2A:
- **Creative Writer** (port 8080): `@RegisterAIService` + `AgentCard`
- **Style Scorer** (port 8081): `@RegisterAIService` + `AgentCard`
- **Orchestrator** (port 8082): `@RegisterSequenceAgent` + `@RegisterLoopAgent`

---

## Exercise 6: Supervisor Agent

**Key message**: "Let the LLM decide when quality is good enough"

```bash
cd demo-6-supervisor/base
```

Replace the explicit loop from Exercise 5 with `@RegisterSupervisorAgent` — the model itself decides when to stop.

---

## Project structure

```
demo-project/
├── pom.xml                    ← Parent POM (centralized versions)
├── demo-1-ai-agent/           ← Injectable AI Agent (chat, streaming, vision)
│   ├── base/
│   └── solution/
├── demo-2-ft-telemetry/       ← Memory + RAG + Tools + Fault Tolerance + Telemetry
│   ├── base/
│   └── solution/
├── demo-3-mcp/                ← MCP Integration (Hnefatafl dice game)
│   ├── mcp-server/            ← Standalone MCP server (Helidon 4)
│   ├── base/
│   └── solution/
├── demo-4-guardrails/         ← Input & Output Guardrails
│   ├── base/
│   └── solution/
├── demo-5-a2a/                ← Agent-to-Agent Protocol (Story Forge)
│   ├── base/
│   └── solution/
└── demo-6-supervisor/         ← Supervisor Agent (LLM-driven orchestration)
    ├── base/
    └── solution/
```

---

## Day-of checklist

- [ ] LLM provider ready (Mistral API key exported or Ollama running)
- [ ] `curl http://localhost:11434/api/tags` responds (Ollama only)
- [ ] Docker/Podman ready for Grafana LGTM
- [ ] IDE open on demo-project/
- [ ] Terminal ready for curl commands
- [ ] Solutions tested the day before (`solution/` of each exercise)
- [ ] Reveal.js slides launched (open `slides/index.html` in browser)
- [ ] Workshop page launched (open `workshop/index.html` in browser)

## In case of emergency

```bash
# Backup solution for each exercise (Linux / macOS):
cd demo-1-ai-agent/solution && mvn clean install && ./target/server/bin/standalone.sh
cd demo-2-ft-telemetry/solution && mvn clean install && ./target/server/bin/standalone.sh
cd demo-3-mcp/solution && mvn clean install && ./target/server/bin/standalone.sh
cd demo-4-guardrails/solution && mvn clean install && ./target/server/bin/standalone.sh

# Windows:
cd demo-1-ai-agent\solution && mvn clean install && target\server\bin\standalone.bat
```

Breathe. Smile. "That is why we have backup slides."
