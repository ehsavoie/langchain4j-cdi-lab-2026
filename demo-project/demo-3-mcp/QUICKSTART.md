# Demo 3 - Quick Start Guide

## Quick Start (3 minutes)

### 1. Start Ollama

```bash
ollama pull ministral:3b
ollama serve
```

> **Note**: Leave this terminal open. Ollama must be running on `localhost:11434`.

### 2. Build the MCP dice server

```bash
cd demo-3-mcp/mcp-server
mvn clean package
```

This produces `target/demo-3-mcp-dice-server.jar`.

### 3. Start the application

```bash
cd ../solution/
mvn clean wildfly:dev
```

WildFly self-provisions via Galleon (first run takes ~2 minutes).

### 4. Play 421!

Open http://localhost:8080/demo-3/ — the casino UI loads automatically.

Try these commands:

- "Roll the dice" — Lucky Jack rolls 3d6 for you
- "Your turn" — Lucky Jack rolls for himself
- "Who wins?" — Lucky Jack compares results

Or via curl:

```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Roll the dice" \
  http://localhost:8080/demo-3/api/game/play
```

## What You Will See

- **Lucky Jack Diamond**: A Vegas casino dealer AI agent
- **MCP Tool Calls**: The LLM calls `roll_multiple(count=3, sides=6)` via MCP protocol
- **421 Combinations**: The AI evaluates dice combinations (421 > Three of a Kind > Straight > Pair > Nothing)

## How It Works

```
Player -> JAX-RS -> CasinoDealerAI (@RegisterAIService)
  -> LLM decides to call roll_multiple tool
  -> McpToolProvider -> JSON-RPC -> MCP Dice Server (stdio)
  -> Server rolls 3d6 -> returns result
  -> LLM analyzes combination -> responds in character
```

## Stopping Everything

```bash
# In the WildFly terminal: Ctrl+C
```

## Common Issues

**"MCP server not found"**:
- Check the JAR was built: `ls mcp-server/target/demo-3-mcp-dice-server.jar`
- Rebuild if needed: `cd mcp-server && mvn clean package`

**"Connection refused" on chat**:
- Check Ollama is running: `curl http://localhost:11434/api/tags`
- Check the model is pulled: `ollama list`

**Port 8080 already in use**:
- Check what's using it: `lsof -i :8080`
- Or use the provisioned server with port offset: `./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=10`

**Dice not rolling (LLM makes up results)**:
- Check WildFly logs for MCP tool call traces
- Try a larger model (`qwen2.5:7b`) for better tool-calling accuracy
