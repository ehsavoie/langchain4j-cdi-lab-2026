# Demo 3 - Hnefatafl at the Grand Thing with MCP

Third demo for Devoxx France: play **Hnefatafl** (a Nordic rune stone game) against an AI that uses the **MCP** (Model Context Protocol) to manage dice rolls on WildFly.

## Overview

1. A **standalone MCP server** (`mcp-server/`) exposes a dice rolling tool (`roll` for 2d6) via Streamable HTTP
2. An **AI Jarl agent** (`HnefataflJarlAI`) connects to this server via `McpToolProvider`
3. The agent plays the role of Ragnar the Skald at the Grand Thing: it rolls the runes via MCP, applies Hnefatafl rules, and announces the warrior's fate
4. A **Viking-themed web interface** allows real-time play

**Key message**: "MCP is the JDBC of AI — your Jakarta EE agents communicate with any external tool server"

## Prerequisites

- **Java 21+**, **Maven 3.8+**
- **Ollama** (local) or a **Mistral AI API key** (remote)

```bash
# Option A: Ollama (local)
ollama pull ministral-3:3b
ollama serve

# Option B: Mistral AI (remote)
export MISTRAL_API_KEY=your-key-here
```

## Hnefatafl Rules (rune stone rolling)

**Hnefatafl** uses 2 six-sided rune stones (2d6):

**Opening roll (first roll of a round):**
- **7 or 11**: Odin's Favor — the warrior **WINS** immediately!
- **2, 3, or 12**: Norns' Curse — the warrior **LOSES** immediately!
- **Any other number** (4, 5, 6, 8, 9, 10): this number becomes the **Marked Rune**

**Rune Phase (if a rune was marked):**
- The warrior keeps rolling
- If they roll **the Marked Rune** again: they **WIN**!
- If they roll a **7**: Ragnarok — they **LOSE**!
- Any other number: no decision, roll again

## Project Structure

```
demo-3-mcp/
├── pom.xml                               # Aggregator POM
├── mcp-server/                           # MCP dice rolling server (standalone JAR)
│   ├── pom.xml                           # Helidon 4 + langchain4j-cdi-mcp-server
│   └── src/main/java/org/acme/
│       └── DiceRoller.java               # @Tool: roll(numberOfDice) → dice results
│
├── base/                                 # Skeleton for live coding
│   ├── pom.xml
│   ├── src/main/java/com/example/demo3/
│   │   ├── JaxRsActivator.java
│   │   ├── HnefataflJarlAI.java          # TODO: @RegisterAIService + @SystemMessage
│   │   └── GameResource.java             # TODO: @Inject + call the agent
│   └── src/main/webapp/
│       ├── WEB-INF/beans.xml
│       └── index.html                    # Viking interface (ready!)
│
└── solution/                             # Complete reference implementation
    ├── pom.xml
    ├── src/main/java/com/example/demo3/
    │   ├── HnefataflJarlAI.java          # Complete: Ragnar the Skald, all rules
    │   ├── ChatMemoryProviderBean.java   # Session memory
    │   ├── LastDiceRollChatMemory.java   # Marked rune tracking
    │   └── GameResource.java             # Complete
    └── src/main/webapp/
        ├── WEB-INF/beans.xml
        └── index.html                    # Viking interface
```

## Getting Started

### Step 1: Build the MCP dice server

```bash
cd demo-project/demo-3-mcp/mcp-server
mvn clean package
```

This produces `target/casino-dice-roller.jar`. The server exposes the `roll` tool via Streamable HTTP on port 8090.

The server starts automatically with WildFly via the Maven plugin. Alternatively, start it manually:

```bash
java -jar target/casino-dice-roller.jar
```

### Step 2: Launch the WildFly application

```bash
cd demo-project/demo-3-mcp/base    # or solution/
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

The application is available at **http://localhost:8080/demo-3/**

### Verification

```bash
# Application health
curl http://localhost:8080/demo-3/api/game/health

# Start a game directly
curl http://localhost:8080/demo-3/api/game/start
```

## Live Coding Walkthrough

### Step 1: Understand the MCP dice server

Examine `DiceRoller.java` — a simple CDI bean annotated with `@Tool` that rolls N dice via `java.util.Random`:

```java
@ApplicationScoped
public class DiceRoller {

    @Tool(description = "Roll a number of dice and return the results")
    public String roll(@ToolArg(description = "The number of dice") int numberOfDice) {
        int[] result = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            result[i] = new Random().nextInt(1, 7);
        }
        return Arrays.toString(result);
    }
}
```

The `langchain4j-cdi-mcp-server` framework exposes this tool via JSON-RPC 2.0 over Streamable HTTP — no HTTP server code to write.

### Step 2: Annotate HnefataflJarlAI

Open `HnefataflJarlAI.java` and add `@RegisterAIService` with `toolProviderName = "mcp"`:

```java
import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAIService(chatModelName = "mistral", toolProviderName = "mcp")
public interface HnefataflJarlAI {

    @SystemMessage("""
        You are Ragnar the Skald, the Jarl hosting the Hnefatafl at the Grand Thing of the Northern warriors.

        HNEFATAFL RULES:
        - Roll 2 rune stones with roll(numberOfDice=2).
        - Opening roll: 7 or 11 → Odin's Favor (WIN)!
          2, 3, or 12 → Norns' Curse (LOSE)!
          Other → this total becomes the Marked Rune.
        - Rune Phase: keep rolling until the Marked Rune is hit (WIN) or a 7 (LOSE).

        MANDATORY FORMAT for each roll:
        RUNES: [X, Y]
        TOTAL: [sum]
        FATE: [what happened]

        Reply in English, be concise, Norse expressions welcome!
        """)
    String play(@UserMessage String playerAction);
}
```

### Step 3: Wire the REST endpoint

Open `GameResource.java` and inject the agent:

```java
@Inject
HnefataflJarlAI gameMaster;

@POST @Path("/play")
@Consumes(MediaType.TEXT_PLAIN) @Produces(MediaType.TEXT_PLAIN)
public String play(String playerAction) {
    return gameMaster.play(playerAction);
}

@GET @Path("/start")
@Produces(MediaType.TEXT_PLAIN)
public String start() {
    return gameMaster.play("Hail! I am ready to play Hnefatafl.");
}
```

### Step 4: Configure the model and MCP transport

Uncomment in `microprofile-config.properties`:

```properties
# AI Model (Option A: Mistral AI)
dev.langchain4j.cdi.plugin.mistral.class=dev.langchain4j.model.mistralai.MistralAiChatModel
dev.langchain4j.cdi.plugin.mistral.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.mistral.config.model-name=mistral-small-latest

# AI Model (Option B: Ollama)
# dev.langchain4j.cdi.plugin.mistral.class=dev.langchain4j.model.ollama.OllamaChatModel
# dev.langchain4j.cdi.plugin.mistral.config.base-url=http://localhost:11434
# dev.langchain4j.cdi.plugin.mistral.config.model-name=ministral-3:3b

# MCP Transport (Streamable HTTP → dice server)
dev.langchain4j.cdi.plugin.ssetransport.class=dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport
dev.langchain4j.cdi.plugin.ssetransport.config.url=http://localhost:8090/mcp

# MCP Client
dev.langchain4j.cdi.plugin.mcpclient.class=dev.langchain4j.mcp.client.DefaultMcpClient
dev.langchain4j.cdi.plugin.mcpclient.config.transport=lookup:@ssetransport

# Tool Provider (named "mcp" for @RegisterAIService)
dev.langchain4j.cdi.plugin.mcp.class=dev.langchain4j.mcp.McpToolProvider
dev.langchain4j.cdi.plugin.mcp.config.mcpClients=lookup:@mcpclient
```

### Step 5: Play

Open **http://localhost:8080/demo-3/** and play:

**To roll the runes:**
- `Roll the runes`
- `Roll`
- `New game`

**To continue (rune phase):**
- `Roll again`
- `Continue`

## Execution Flow

```
Browser → GET /api/game/start
  → HnefataflJarlAI.play("Hail! I am ready to play Hnefatafl.")
    → LLM decides to call roll(numberOfDice=2)
    → McpToolProvider → HTTP JSON-RPC → MCP Server (port 8090)
    → Server rolls 2d6, returns [4, 3]
    → LLM receives the result and writes the response:

       RUNES: [4, 3]
       TOTAL: 7
       FATE: Odin's Favor! The warrior wins!

       By the gods of the North, a 7! Victory is yours!
```

## Interaction Examples

**Warrior:** `Roll the runes`

**Ragnar the Skald:**
```
Skál! Let the runes decide!

RUNES: [4, 3]
TOTAL: 7
FATE: Odin's Favor! The warrior wins!

By the gods of the North! Victory is yours, warrior!
```

---

**Warrior:** `New game`

**Ragnar the Skald:**
```
RUNES: [3, 5]
TOTAL: 8
FATE: The Marked Rune is 8. Keep rolling, warrior!
```

**Warrior:** `Roll again`

**Ragnar the Skald:**
```
RUNES: [2, 6]
TOTAL: 8
FATE: Rune hit! The warrior wins!

The rune smiled upon you — you are worthy of Valhalla!
```

## Key MCP Takeaways

1. **Decoupling**: the dice server is an independent process (different JVM, could be a different language) — `McpToolProvider` bridges the gap
2. **Standard protocol**: JSON-RPC 2.0 over Streamable HTTP — any compatible MCP server can be plugged in
3. **Pure configuration**: the transport, client, and tool provider are all registered via MicroProfile Config — no Java code to write in the WildFly application
4. **`lookup:@`**: the `lookup:@ssetransport` prefix in the MCP config tells LangChain4j-CDI to inject the bean named `ssetransport`

## Troubleshooting

- **MCP server won't start**: Check that the JAR is built (`cd mcp-server && mvn clean package`)
- **`Connection refused` on port 8090**: The MCP server is not started — relaunch `java -jar mcp-server/target/casino-dice-roller.jar`
- **Agent doesn't respond**: Check that `HnefataflJarlAI` is annotated with `@RegisterAIService`
- **Runes are not rolled**: Check WildFly logs for MCP calls (`logRequests=true` in the config)
- **Run the solution directly**: `cd solution && mvn clean install` then `./target/server/bin/standalone.sh` (Linux/macOS) or `target\server\bin\standalone.bat` (Windows)

## Resources

- **MCP Protocol**: https://modelcontextprotocol.io
- **LangChain4j-CDI**: https://github.com/langchain4j/langchain4j-cdi
- **LangChain4j MCP**: https://docs.langchain4j.dev/integrations/mcp
- **WildFly**: https://www.wildfly.org
