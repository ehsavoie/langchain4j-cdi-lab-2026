# Demo 1 - Quick Start Guide

## Quick Start (2 minutes)

### 1. Start Ollama

```bash
ollama pull llama3.1:8b
ollama serve
```

> **Note**: Leave this terminal open. Ollama must be running on `localhost:11434`.

### 2. Start the application

**In a new terminal**:

```bash
cd demo-1-ai-agent/solution/
mvn clean install
./target/server/bin/standalone.sh   # Linux / macOS
target\server\bin\standalone.bat    # Windows
```

WildFly is provisioned via Galleon during `mvn clean install` (first run takes ~2 minutes).

### 3. Test the application

Open http://localhost:8080/demo-1/ and send a few messages:

- "Tell me a joke about slot machines"
- "What's the best Vegas show?"
- "Tell me about poker faces"

Or via curl:

```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Tell me a joke about slot machines" \
  http://localhost:8080/demo-1/api/chat
```

### 4. Test streaming

Open http://localhost:8080/demo-1/stream.html and type a message — tokens appear in real time!

### 5. Test image analysis (requires vision model)

Open http://localhost:8080/demo-1/image.html and upload an image.

> **Note**: The vision model requires either a local Ollama vision model (`llama3.2-vision:11b`) or a GROQ/OpenAI API key. See `microprofile-config.properties` for options.

## What You Will See

- **Chat**: A Vegas stand-up comedian powered by `@RegisterAIService` + `@Inject`
- **Streaming**: Real-time token display via `TokenStream` + Server-Sent Events
- **Vision**: Image analysis with a multimodal model

## Stopping Everything

```bash
# In the WildFly terminal: Ctrl+C
```

## Common Issues

**"Connection refused" on chat**:
- Check Ollama is running: `curl http://localhost:11434/api/tags`
- Check the model is pulled: `ollama list`

**Port 8080 already in use**:
- Check what's using it: `lsof -i :8080`

**Streaming not working**:
- Ensure the streaming model is configured in `microprofile-config.properties`
- Check browser console for EventSource errors

**Vision not working**:
- A vision-capable model must be configured (see config options in `microprofile-config.properties`)
- Check that `GROQ_API_KEY` or `OPENAI_API_KEY` env var is set if using a cloud provider
