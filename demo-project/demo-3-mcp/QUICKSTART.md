# Demo 3 - Quick Start Guide

## Quick Start (3 minutes)

### 1. Start Ollama

```bash
ollama pull ministral-3:3b
ollama serve
```

> **Note**: Leave this terminal open. Ollama must be running on `localhost:11434`.

### 2. Build the MCP dice server

```bash
cd demo-3-mcp/mcp-server
mvn clean package
```

Produces `target/demo-3-mcp-dice-server.jar`.

### 3. Start the application

```bash
cd ../solution/
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

WildFly is provisioned via Galleon during `mvn clean install` (first run ~2 minutes).

### 4. Play Hnefatafl!

Open http://localhost:8080/demo-3/ — the Viking interface loads automatically.

Try these commands:

- `Roll the runes` — Ragnar rolls 2 rune stones to determine your fate
- `Roll again` — Continue during the rune phase
- `New game` — Start a new round

Or via curl:

```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Roll the runes" \
  http://localhost:8080/demo-3/api/game/play
```

## What You Will See

- **Ragnar the Skald**: An AI Jarl agent hosting Hnefatafl at the Grand Thing
- **MCP tool calls**: The LLM calls `roll(numberOfDice=2)` via the MCP protocol
- **Hnefatafl rules**: 7/11 = Odin's Favor, 2/3/12 = Norns' Curse, other = Marked Rune

## How It Works

```
Warrior -> JAX-RS -> CasinoDealerAI (@RegisterAIService)
  -> LLM decides to call the roll tool
  -> McpToolProvider -> JSON-RPC -> MCP dice server (stdio)
  -> Server rolls 2d6 -> returns the result
  -> LLM applies the rules -> responds in character
```

## Stopping Everything

```bash
# In the WildFly terminal: Ctrl+C
```

## Common Issues

**"MCP server not found"**:
- Check that the JAR was built: `ls mcp-server/target/demo-3-mcp-dice-server.jar`
- Rebuild if necessary: `cd mcp-server && mvn clean package`

**"Connection refused" on chat**:
- Check that Ollama is running: `curl http://localhost:11434/api/tags`
- Check that the model is downloaded: `ollama list`

**Port 8080 already in use**:
- Check what's using it: `lsof -i :8080`
- Or use the provisioned server with port offset: `./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=10`

**Runes are not rolled (LLM invents results)**:
- Check WildFly logs for MCP tool call traces
- Try a larger model (`qwen2.5:7b`) for better tool calling accuracy
