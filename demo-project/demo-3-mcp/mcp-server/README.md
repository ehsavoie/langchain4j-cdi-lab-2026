# MCP Dice Server

Standalone MCP (Model Context Protocol) server for rune stone rolling.

## Description

This server exposes a dice rolling tool via the MCP protocol over stdio (JSON-RPC 2.0). It is used by the `CasinoDealerAI` agent (Ragnar the Skald) to manage game mechanics (rune stone rolls).

## Available Tool

| Tool | Description | Parameters |
|------|-------------|------------|
| `roll` | Rolls a number of 6-sided dice | `numberOfDice` (int): number of dice |

## Build

```bash
cd demo-3-mcp/mcp-server
mvn clean package
```

The generated JAR is located at `target/demo-3-mcp-dice-server.jar`.

## Usage

### As an MCP server (normal mode)

The server is launched **automatically** by the `solution` or `base` module via the CDI producer `McpConfig`. It communicates via stdin/stdout with the WildFly application.

You **don't need** to start it manually for the demo.

### Manual testing (standalone mode)

To test the server independently:

```bash
java -jar target/demo-3-mcp-dice-server.jar
```

Then send JSON-RPC commands on stdin. Examples:

**1. Initialization**
```json
{"jsonrpc":"2.0","id":1,"method":"initialize","params":{}}
```

**2. List tools**
```json
{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}
```

**3. Tool call (roll 2 rune stones)**
```json
{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"roll","arguments":{"numberOfDice":2}}}
```

**4. Roll 3 dice**
```json
{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"roll","arguments":{"numberOfDice":3}}}
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
|  | CasinoDealerAI  |  |  The LLM decides to roll
|  +-------+--------+  |  the runes (tool calling)
|          |           |
|  +-------v--------+  |
|  |  McpConfig     |  |  CDI producer that launches
|  |  (Producer)    |  |  the MCP process
|  +-------+--------+  |
+-----------+-----------+
            | stdio
            | (JSON-RPC)
+-----------v-----------+
|  MCP Dice Server      |
|  (this module)        |
|                       |
|  - roll               |  Rolls N 6-sided dice
|                       |  and returns the results
+-----------------------+
```

## Logs

Logs are sent to stderr:
```
[main] INFO org.acme.DiceRoller - Dice roll: 2 dice
[main] INFO org.acme.DiceRoller - Die 0: 4
```

## Troubleshooting

**Server doesn't respond**
- Check that the JAR is correctly built: `ls -lh target/demo-3-mcp-dice-server.jar`
- Check the logs in the WildFly console

**Error "Unable to start MCP server"**
- The path to the JAR in `McpConfig.java` is incorrect
- The JAR doesn't have execution permissions

**Dice are not rolled**
- Check that `McpToolProvider` is correctly injected with `@Named("mcp")`
- Check that the LLM supports tool calling (Ollama with recent models)

## Resources

- **MCP Protocol**: https://modelcontextprotocol.io
- **JSON-RPC 2.0 Specification**: https://www.jsonrpc.org/specification
