# LangChain4j-CDI — Devoxx France 2026 Demos

## Setup before the big day

### 1. Install Ollama + model

```bash
# Install Ollama (https://ollama.ai)
brew install ollama

# Download the model (tool calling + good quality)
ollama pull qwen2.5:7b

# Alternative option:
ollama pull mistral:7b

# Verify it is running
ollama list
curl http://localhost:11434/api/tags
```

**Why qwen2.5:7b?**
- Excellent tool calling (function calling) support
- 7B parameters = runs well on Apple Silicon (M1/M2/M3)
- Good quality output for the demo
- Reasonable response time (~2-5s)

**Alternative: mistral:7b** -- good model, but tool calling slightly less reliable.

### 2. WildFly

```bash
# Build and provision WildFly automatically:
cd demo-1-ai-agent/solution
mvn clean install

# Then start the server:
./target/server/bin/standalone.sh    # Linux / macOS
target\server\bin\standalone.bat     # Windows
```

### 3. IntelliJ IDEA

- Open `demo-project/` as a Maven project
- Make sure JDK 21 is configured
- Configure the integrated terminal for Maven commands

---

## Demo plan (chronological)

| Demo | Topic | Duration | Module |
|------|-------|----------|--------|
| **Demo 1** | Injectable AI Agent in a few lines | ~10 min | `demo-1-ai-agent/` |
| **Demo 2** | Fault Tolerance + Telemetry + Tools | ~10 min | `demo-2-ft-telemetry/` |
| **Demo 3** | MCP connection to external tools | ~8 min | `demo-3-mcp/` |

### Demo strategy

Each module contains:
- `base/` -- the starting code (skeleton with TODOs) for live coding
- `solution/` -- the complete working version, as a backup

**Live**: work in `base/`, follow the TODOs
**If something breaks**: `cd solution && mvn clean install` then start the server and move on

---

## Demo 1: Injectable AI Agent

**Key message**: "From 15 lines of boilerplate to 1 annotation + 1 config"

```bash
cd demo-1-ai-agent/base
# Open in IntelliJ: ChatAssistant.java, ChatResource.java, microprofile-config.properties
```

**Live steps:**
1. Show the empty skeleton of `ChatAssistant.java`
2. Add `@RegisterAIService(chatModelName = "my-model")`
3. Add `@SystemMessage` with the prompt
4. Open `ChatResource.java`, add `@Inject ChatAssistant`
5. Uncomment the Ollama config in `microprofile-config.properties`
6. `mvn clean install` then `./target/server/bin/standalone.sh` (Linux/macOS) or `target\server\bin\standalone.bat` (Windows)
7. Test:
```bash
curl -X POST http://localhost:8080/demo1/api/chat \
  -H "Content-Type: text/plain" \
  -d "What is CDI in 2 sentences?"
```

**Points to highlight:**
- Zero LangChain4j code in the business logic
- Everything is in properties (changeable without recompilation)
- The interface is a contract, not an implementation

---

## Demo 2: Fault Tolerance + Telemetry + Tools

**Key message**: "MicroProfile annotations work on AI Services because they are CDI beans"

```bash
cd demo-2-ft-telemetry/base
```

**Live steps:**
1. Show that the agent with Tools already works:
```bash
curl -X POST http://localhost:8080/demo2/api/chat \
  -H "Content-Type: text/plain" \
  -d "What sessions are at Devoxx France?"
```
2. Show `BookingTools.java` -- it is a CDI bean with `@Tool`
3. Ask the question: "What if Ollama goes down?"
4. Add `@Retry`, `@Timeout`, `@Fallback`, `@CircuitBreaker`
5. Hot-reload (save in IntelliJ)
6. Kill Ollama: `pkill ollama` or `docker stop ollama`
7. Test again -- the fallback responds!
8. Restart Ollama -- the retry works
9. Show the OpenTelemetry traces in the WildFly logs

**Impact moment:** kill Ollama during the demo, show that the app does NOT crash.

---

## Demo 3: MCP (Model Context Protocol)

**Key message**: "MCP is JDBC for AI -- your agents talk to any tool"

```bash
# First build the MCP server
cd demo-3-mcp/mcp-server
mvn clean package

# Then work in base/
cd ../base
```

**Live steps:**
1. Show the MCP server (`ConferenceServer.java`) -- 3 exposed tools
2. Create the CDI producer for `McpToolProvider` in `McpConfig.java`
3. Annotate `ConferenceAgent` with `@RegisterAIService`
4. Wire the REST endpoint
5. Deploy and test:
```bash
curl -X POST http://localhost:8080/demo3/api/conference/query \
  -H "Content-Type: text/plain" \
  -d "What conferences are at Devoxx France?"
```
6. The agent calls the MCP server, retrieves the data, and responds

**Key point:** the MCP server is an EXTERNAL program. It could be in Python, Node.js, or anything else. The Jakarta EE agent does not care, it speaks MCP.

---

## Day-of checklist

- [ ] Ollama running with qwen2.5:7b
- [ ] `curl http://localhost:11434/api/tags` responds
- [ ] WildFly 39 ready (or the plugin downloads it)
- [ ] IntelliJ open on demo-project/
- [ ] Terminal ready for curl commands
- [ ] Solutions tested the day before (`solution/` of each demo)
- [ ] iPad in Sidecar for speaker notes
- [ ] Reveal.js slides launched (ouvrir `slides/index.html` dans le navigateur)

## In case of emergency

```bash
# Backup solution for each demo (Linux / macOS):
cd demo-1-ai-agent/solution && mvn clean install && ./target/server/bin/standalone.sh
cd demo-2-ft-telemetry/solution && mvn clean install && ./target/server/bin/standalone.sh
cd demo-3-mcp/solution && mvn clean install && ./target/server/bin/standalone.sh

# Windows:
cd demo-1-ai-agent/solution && mvn clean install && target\server\bin\standalone.bat
```

Breathe. Smile. "That is why we have backup slides."
