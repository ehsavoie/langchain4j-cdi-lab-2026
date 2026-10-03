# Prerequisites — LangChain4j-CDI Workshop — {{conference_name}}

## Required software

| Tool | Version | Purpose |
|------|---------|---------|
| **JDK** | 21+ | Java development |
| **Maven** | 3.9+ | Build tool |
| **IDE** | Apache NetBeans / IntelliJ IDEA / VS Code | Code editor |
| **curl** | any | API testing |
| **Mistral AI API Key** _or_ **Ollama** | — | LLM provider — remote (Mistral AI, free) or local (Ollama) |
| **Docker / Podman** | any | Grafana LGTM stack (Exercise 2 only) |

## 1. Get the source code

```bash
git clone https://github.com/ehsavoie/langchain4j-cdi-lab-2026.git
cd langchain4j-cdi-lab-2026
git checkout devoxx2026
cd demo-project
```

## 2. Choose your LLM provider

Two options are available:

**Option A — Mistral AI (remote)**: sign up for free at [console.mistral.ai](https://console.mistral.ai/) to get an API key. No local installation required.

**Option B — Ollama (local)**: install [Ollama](https://ollama.ai), then download the models:

```bash
# In a first terminal (keep running):
ollama serve

# In a second terminal:
ollama pull ministral-3:3b   # exercises 1, 2, 3, 4
ollama pull qwen2.5:7b       # exercise 5 (A2A — always required)
```

Ollama must remain active at `http://localhost:11434` throughout the workshop.

### How to configure the provider in each exercise

Each module contains a `microprofile-config.properties` file structured like this:

```properties
# ---- Option A: Mistral AI (remote) ----
dev.langchain4j.cdi.plugin.my-model.class=...MistralAiChatModel
dev.langchain4j.cdi.plugin.my-model.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.my-model.config.model-name=mistral-small-latest

# ---- Option B: Ollama (local) ----
# dev.langchain4j.cdi.plugin.my-model.class=...OllamaChatModel
# dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
# dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b

dev.langchain4j.cdi.plugin.my-model.config.temperature=0.9
dev.langchain4j.cdi.plugin.my-model.config.log-requests=true
```

**By default, Option A (Mistral AI) is active.** To switch to Ollama:

1. **Comment out** the 3 lines of Option A (add `#` in front)
2. **Uncomment** the 3 lines of Option B (remove the `#`)

The common properties (`temperature`, `log-requests`, etc.) are below and remain unchanged — they apply regardless of the chosen provider.

If you chose **Option A**, export your Mistral AI API key in your terminal:

```bash
# Linux / macOS
export MISTRAL_API_KEY=your-api-key-here

# Windows (PowerShell)
$env:MISTRAL_API_KEY="your-api-key-here"
```

## 3. WildFly (automatic)

WildFly 39 is automatically downloaded and provisioned by the Maven plugin. No manual installation required:

```bash
# Build, provision WildFly and deploy the application
cd demo-1-ai-agent/solution
mvn clean install

# Start the server
./target/server/bin/standalone.sh          # Linux / macOS
target\server\bin\standalone.bat           # Windows
```

`mvn clean install` provisions WildFly and deploys the application to `target/server/`. Then start the server with the appropriate script for your system.

### Configure the LLM provider in the solution

Before verifying your installation, make sure `demo-1-ai-agent/solution/src/main/resources/META-INF/microprofile-config.properties` is configured for your provider:

- **Option A — Mistral AI (remote)**: this is the default. Simply verify that `MISTRAL_API_KEY` is exported in your terminal.
- **Option B — Ollama (local)**: for **each block** (my-model, my-streaming-model, vision-model), comment out the 3 lines of Option A and uncomment the 3 lines of Option B.

```properties
# Example for my-model — Option B: Ollama
# (comment out the 3 Mistral AI lines above, then uncomment these)
dev.langchain4j.cdi.plugin.my-model.class=dev.langchain4j.model.ollama.OllamaChatModel
dev.langchain4j.cdi.plugin.my-model.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.my-model.config.model-name=ministral-3:3b
```

> **Warning**: the file contains **3 models** (my-model, my-streaming-model, vision-model). Remember to switch **all three** if you use Ollama.

## 4. Verify your installation

```bash
# Build all modules to download dependencies
cd demo-project
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

You can also test directly in your browser at [http://localhost:8080/demo-1](http://localhost:8080/demo-1) — the web interface lets you chat with the Viking skald.

If you get a response from the AI Viking skald (via curl or via the browser), your environment is correctly configured. Stop the server (`Ctrl+C`) and open `workshop/index.html` in your browser to start the exercises.
