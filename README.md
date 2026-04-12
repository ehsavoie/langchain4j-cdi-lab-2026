# LangChain4j-CDI — Devoxx France 2026

## Prérequis

| Outil | Version | Objectif |
|------|---------|---------|
| **Java** | 21+ | Compiler et exécuter les démos |
| **Maven** | 3.8+ | Système de build |
| **Ollama** | latest | Inférence LLM locale |

### Installer Ollama

Télécharger depuis https://ollama.com et récupérer les modèles requis :
```bash
ollama pull mistral-small3.1
```

## Workshop

Un guide pratique en autonomie couvrant les 3 démos étape par étape.

Ouvrir directement `workshop/index.html` dans le navigateur.

## Structure

```
slides/          → Présentation Reveal.js
  index.html     → Slides + notes présentateur
workshop/        → Guide pratique du workshop
  index.html     → Tutoriel en autonomie
demo-project/    → Projet Maven pour les démos IntelliJ
```

## Démos

Chaque démo contient un module `base/` (squelette avec TODOs pour le live coding) et `solution/` (référence complète).

| Démo | Sujet | Thème | Module |
|------|-------|-------|--------|
| **Démo 1** | Agent IA injectable (`@RegisterAIService`) | Skald Viking — blagues et sagas épiques | `demo-project/demo-1-ai-agent/` |
| **Démo 2** | Memory + RAG + Tools + Fault Tolerance + Telemetry | Inscriptions aux expéditions vikings (sélection chef, prêts de drakkar) | `demo-project/demo-2-ft-telemetry/` |
| **Démo 3** | MCP (Model Context Protocol) — outils externes | MCP Integration | `demo-project/demo-3-mcp/` |

### Démo 1 — Skald Viking

Un skald (poète-guerrier viking) qui raconte des blagues en mode chat et compose des sagas épiques en streaming.

```bash
cd demo-project/demo-1-ai-agent/solution && mvn clean wildfly:dev

# Test : blague viking
curl -X POST -H "Content-Type: text/plain" \
  -d "Tell me a Viking joke" \
  http://localhost:8080/demo-1/api/chat

# Test streaming : saga épique
curl -X POST -H "Content-Type: text/plain" \
  -d "Compose an epic Viking saga about Ragnar" \
  http://localhost:8080/demo-1/api/stream
```

### Démo 2 — Expéditions Vikings

Système d'inscription aux expéditions vikings avec mémoire de conversation, RAG (recherche documentaire), outils métier (sélection du chef, prêt de drakkar), fault tolerance et télémétrie.

```bash
cd demo-project/demo-2-ft-telemetry/solution && mvn clean wildfly:dev

# Test : recherche d'expéditions
curl -X POST -H "Content-Type: text/plain" \
  -H "X-Session-Id: session-odin" \
  -d "Quelles expéditions sont disponibles ?" \
  http://localhost:8080/demo-2/api/chat

# Test : inscription à une expédition
curl -X POST -H "Content-Type: text/plain" \
  -H "X-Session-Id: session-odin" \
  -d "Inscris-moi à l'expédition vers Lindisfarne" \
  http://localhost:8080/demo-2/api/chat
```

### Démo 3 — MCP Integration

```bash
# Construire le serveur MCP d'abord
cd demo-project/demo-3-mcp/mcp-server && mvn clean package

# Lancer la démo
cd demo-project/demo-3-mcp/solution && mvn clean wildfly:dev
```
