# MCP Dice Server

Standalone MCP (Model Context Protocol) server for rune stone rolling.

## Description

This server exposes a dice rolling tool via the MCP protocol over **Streamable HTTP** (JSON-RPC 2.0). It is used by the `HnefataflJarlAI` agent (Ragnar the Skald) to manage game mechanics (rune stone rolls). It listens on port **8090**.

## Available Tool

| Tool | Description | Parameters |
|------|-------------|------------|
| `roll` | Rolls a number of 6-sided dice | `numberOfDice` (int): number of dice |

## Build

```bash
cd demo-3-mcp/mcp-server
mvn clean package
```

The generated JAR is located at `target/casino-dice-roller.jar`.

## Usage

### Start the server (required before the WildFly application)

The server must be **started manually** before launching the WildFly application:

```bash
java -jar target/casino-dice-roller.jar
```

The server starts on `http://localhost:8090/mcp` and waits for JSON-RPC requests over Streamable HTTP.

### Verification

```bash
# List available tools
curl -X POST http://localhost:8090/mcp \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}'
```

## MCP Protocol

The server implements MCP protocol version `2024-11-05`:
- Communication via **Streamable HTTP** on port `8090`
- **JSON-RPC 2.0** format
- Endpoint: `http://localhost:8090/mcp`

## Architecture

```
+---------------------+
|  WildFly (solution)  |
|                      |
|  +----------------+  |
|  | HnefataflJarlAI|  |  The LLM decides to roll
|  +-------+--------+  |  the runes (tool calling)
|          |           |
|  +-------v--------+  |
|  |  McpToolProvider|  |  Configured via MicroProfile Config
|  +-------+--------+  |
+-----------+-----------+
            | HTTP (Streamable HTTP JSON-RPC)
            | http://localhost:8090/mcp
+-----------v-----------+
|  MCP Dice Server      |
|  (this module)        |
|                       |
|  - roll               |  Rolls N 6-sided dice
|                       |  and returns the results
+-----------------------+
```

## Logs

Logs are sent to the console:
```
[main] INFO org.acme.DiceRoller - Dice roll: 2 dice
[main] INFO org.acme.DiceRoller - Die 0: 4
```

## Troubleshooting

**Server doesn't respond**
- Check that the JAR is correctly built: `ls -lh target/casino-dice-roller.jar`
- Check that port 8090 is not already in use: `lsof -i :8090`

**Error "Connection refused" on port 8090**
- The server is not started — relaunch `java -jar target/casino-dice-roller.jar`

**Dice are not rolled**
- Check that `McpToolProvider` is correctly configured with the MCP client pointing to `http://localhost:8090/mcp`
- Check that the LLM supports tool calling (Ollama with recent models)

## Resources

- **MCP Protocol**: https://modelcontextprotocol.io
- **JSON-RPC 2.0 Specification**: https://www.jsonrpc.org/specification
