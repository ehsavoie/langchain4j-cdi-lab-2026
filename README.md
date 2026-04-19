# LangChain4j-CDI — Devoxx France 2026

## Prérequis

### Outils

| Outil | Version | Utilité |
|-------|---------|---------|
| **JDK** | 21+ | `java -version` |
| **Maven** | 3.9+ | `mvn -version` |
| **Git** | n'importe quelle | Cloner le dépôt |
| **curl** | n'importe quelle | Tester les API REST |
| **Docker / Podman** | n'importe quelle | Stack Grafana LGTM (Exercice 2 uniquement) |

> WildFly 39 est téléchargé et provisionné **automatiquement** par le plugin Maven — aucune installation manuelle.

### Fournisseur LLM

Choisir **une** des deux options :

**Option A — Mistral AI (distant, gratuit)**

Créer un compte sur https://console.mistral.ai et exporter la clé :

```bash
export MISTRAL_API_KEY=your-api-key-here   # Linux / macOS
$env:MISTRAL_API_KEY="your-api-key-here"   # Windows PowerShell
```

**Option B — Ollama (local)**

Installer Ollama depuis https://ollama.com, puis :

```bash
# Dans un premier terminal (laisser tourner pendant tout l'atelier)
ollama serve

# Dans un second terminal, télécharger les modèles
ollama pull ministral-3:3b   # démos 1, 3, 4
ollama pull qwen2.5:7b       # démo 2 (tool calling + embeddings), démo 5 (A2A)
```

### Code source

```bash
git clone https://github.com/yblazart/confs-langchain4j-cdi-javaone2026.git
cd confs-langchain4j-cdi-javaone2026/demo-project

# Télécharger toutes les dépendances Maven
mvn clean install -DskipTests
```

### Vérifier l'installation

```bash
cd demo-1-ai-agent/solution
mvn clean wildfly:dev

# Dans un autre terminal :
curl -X POST -H "Content-Type: text/plain" \
  -d "Chante-moi une chanson héroïque" \
  http://localhost:8080/demo-1/api/chat
```

Une réponse du skald viking confirme que l'environnement est prêt. Arrêter avec `Ctrl+C`.

---

## Workshop

Guide pratique en autonomie couvrant les 5 exercices étape par étape.

Ouvrir `workshop/index.html` directement dans le navigateur.

## Structure

```
slides/          → Présentation Reveal.js
  index.html     → Slides + notes présentateur
introduction/    → Slides d'introduction (Devoxx)
  index.html     → Présentation des intervenants et contexte
workshop/        → Guide pratique du workshop
  index.html     → Tutoriel en autonomie
demo-project/    → Projet Maven multi-modules
  demo-1-ai-agent/         → Agent IA injectable (@RegisterAIService)
  demo-2-ft-telemetry/     → Memory + RAG + Tools + Fault Tolerance + Telemetry
  demo-3-mcp/              → MCP (Model Context Protocol)
  demo-4-guardrails/       → Guardrails (validation entrée/sortie)
  demo-5-a2a/              → A2A (Agent-to-Agent Protocol)
```

## Démos

Chaque démo contient un module `base/` (squelette avec TODOs pour le live coding) et `solution/` (référence complète).

| Démo | Sujet | Thème | Modèle |
|------|-------|-------|--------|
| **Démo 1** | Agent IA injectable (`@RegisterAIService`) | Skald Viking — blagues et sagas épiques | `ministral-3:3b` |
| **Démo 2** | Memory + RAG + Tools + Fault Tolerance + Telemetry | Inscriptions aux expéditions vikings | `qwen2.5:7b` |
| **Démo 3** | MCP (Model Context Protocol) | Jeu de dés Hnefatafl avec Ragnar le Skald | `ministral-3:3b` |
| **Démo 4** | Guardrails (validation entrée/sortie) | Skald Viking avec garde-fous | `ministral-3:3b` |
| **Démo 5** | A2A (Agent-to-Agent Protocol) | Story Forge — pipeline multi-agents | `qwen2.5:7b` |

### Démo 1 — Skald Viking

```bash
cd demo-project/demo-1-ai-agent/solution && mvn clean wildfly:dev

# Test : blague viking
curl -X POST -H "Content-Type: text/plain" \
  -d "Raconte-moi une blague viking" \
  http://localhost:8080/demo-1/api/chat

# Test streaming : saga épique
curl -X POST -H "Content-Type: text/plain" \
  -d "Compose une saga épique sur Ragnar" \
  http://localhost:8080/demo-1/api/stream
```

### Démo 2 — Expéditions Vikings

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
# Démarrer le serveur MCP (Helidon 4, port 8090)
cd demo-project/demo-3-mcp/mcp-server && mvn clean package && java -jar target/casino-dice-roller.jar

# Lancer la démo
cd demo-project/demo-3-mcp/solution && mvn clean wildfly:dev
```

### Démo 4 — Guardrails

```bash
cd demo-project/demo-4-guardrails/solution && mvn clean wildfly:dev

curl -X POST -H "Content-Type: text/plain" \
  -d "Chante-moi une chanson viking" \
  http://localhost:8080/demo-4/api/chat
```

### Démo 5 — A2A Story Forge

```bash
# Terminal 1 : Creative Writer (port 8080)
cd demo-project/demo-5-a2a/solution/a2a-creative-writer && mvn clean wildfly:dev

# Terminal 2 : Style Scorer (port 8081)
cd demo-project/demo-5-a2a/solution/a2a-style-scorer && mvn clean wildfly:dev -Djboss.socket.binding.port-offset=1

# Terminal 3 : Orchestrateur (port 8082)
cd demo-project/demo-5-a2a/solution/a2a-orchestrator && mvn clean wildfly:dev -Djboss.socket.binding.port-offset=2

curl "http://localhost:8082/api/styled-story?topic=Erik+le+Rouge+traverse+les+mers&style=epique"
```
