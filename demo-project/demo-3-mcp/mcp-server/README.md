# MCP Dice Server

Standalone MCP (Model Context Protocol) server for dice rolling.

## Description

This server exposes 5 dice rolling tools via the MCP protocol over stdio (JSON-RPC 2.0). It is used by the `CasinoDealerAI` to manage game mechanics (dice rolls, skill checks, etc.).

## Available Tools

| Tool | Description | Parameters |
|------|-------------|------------|
| `roll_d6` | Rolls a 6-sided die | None |
| `roll_d20` | Rolls a 20-sided die | None |
| `roll_custom` | Rolls a custom die | `sides` (int): number of faces |
| `roll_multiple` | Rolls multiple dice | `count` (int), `sides` (int) |
| `roll_with_modifier` | Rolls with modifier | `sides` (int), `modifier` (int) |

## Build

```bash
cd demo-4-casino-dice/mcp-server
mvn clean package
```

The generated JAR is located at `target/demo-4-mcp-dice-server.jar` (~8.4MB).

## Usage

### As an MCP server (normal mode)

The server is launched **automatically** by the `solution` or `base` module via the `McpConfig` CDI producer. It communicates via stdin/stdout with the WildFly application.

You do **not need** to start it manually for the demo.

### Manual testing (standalone mode)

To test the server independently:

```bash
java -jar target/demo-4-mcp-dice-server.jar
```

Then send JSON-RPC commands on stdin. Example:

**1. Initialization**
```json
{"jsonrpc":"2.0","id":1,"method":"initialize","params":{}}
```

**2. List tools**
```json
{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}
```

**3. Tool call (roll_d20)**
```json
{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"roll_d20","arguments":{}}}
```

**4. Call with parameters (roll_custom)**
```json
{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"roll_custom","arguments":{"sides":12}}}
```

**5. Multiple roll (3d6)**
```json
{"jsonrpc":"2.0","id":5,"method":"tools/call","params":{"name":"roll_multiple","arguments":{"count":3,"sides":6}}}
```

**6. Roll with modifier (d20+3)**
```json
{"jsonrpc":"2.0","id":6,"method":"tools/call","params":{"name":"roll_with_modifier","arguments":{"sides":20,"modifier":3}}}
```

## MCP Protocol

The server implements MCP protocol version `2024-11-05`:
- Communication via **stdin/stdout**
- **JSON-RPC 2.0** format
- **stdio** transport (no network)

## Architecture

```
+---------------------+
|  WildFly (solution)  |
|                      |
|  +----------------+  |
|  | CasinoDealerAI  |  |  LLM decides to roll
|  +-------+--------+  |  a die (tool calling)
|          |           |
|  +-------v--------+  |
|  |  McpConfig     |  |  CDI Producer launches
|  |  (Producer)    |  |  the MCP process
|  +-------+--------+  |
+-----------+-----------+
            | stdio
            | (JSON-RPC)
+-----------v-----------+
|  MCP Dice Server      |
|  (this module)        |
|                       |
|  - roll_d6            |
|  - roll_d20           |  Rolls the dice
|  - roll_custom        |  and returns
|  - roll_multiple      |  the results
|  - roll_with_mod.     |
+-----------------------+
```

## Logs

Logs are sent to stderr via SLF4J Simple:
```
[main] INFO com.example.mcp.DiceServer - Starting MCP Dice server...
[main] INFO com.example.mcp.DiceServer - MCP Dice server started successfully
```

## Troubleshooting

**The server does not respond**
- Check that the JAR is properly built: `ls -lh target/demo-4-mcp-dice-server.jar`
- Check the logs in the WildFly console

**Error "Unable to start MCP server"**
- The path to the JAR in `McpConfig.java` is incorrect
- The JAR does not have execution permissions

**Dice are not rolled**
- Check that the `McpToolProvider` is properly injected with `@Named("mcp")`
- Check that the LLM supports tool calling (Ollama with recent models)

## Resources

- **MCP Protocol**: https://modelcontextprotocol.io
- **JSON-RPC 2.0 Spec**: https://www.jsonrpc.org/specification
