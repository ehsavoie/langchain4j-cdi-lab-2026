# CLAUDE.md

Ce fichier fournit des instructions à Claude Code (claude.ai/code) lors du travail avec le code de ce dépôt.

## Projet

Présentation Devoxx France 2026 sur **LangChain4j-CDI** : intégration de LangChain4j dans Jakarta EE / MicroProfile via CDI.
Intervenants : Yann Blazart & Emmanuel Hugonnet. Licence : Apache 2.0.

Le dépôt contient des **slides Reveal.js** (`slides/`) et un **projet Maven multi-modules** (`demo-project/`) avec 3 démos progressives, chacune ayant un module `base/` (squelette avec des TODOs pour le live coding) et `solution/` (référence complète).

**Voir `AGENT.md` pour les détails d'architecture LangChain4j-CDI** (patterns @RegisterAIService, ChatMemoryProvider, Tools, config MicroProfile, pièges connus).

## Stack

Java 21, Maven 3.8+, Jakarta EE 10, MicroProfile 6.1, WildFly 39 (Galleon), LangChain4j 1.11.0, LangChain4j-CDI 1.0.0, local Ollama.

## Commandes Essentielles

```bash
# Prérequis : Ollama
ollama pull ministral-3:3b    # demo-1, demo-3
ollama pull qwen2.5:7b        # demo-2 (tool calling + embeddings)
ollama serve

# Lancer une démo (remplacer N et le nom du module)
cd demo-project/demo-1-ai-agent/solution && mvn clean wildfly:dev
cd demo-project/demo-2-ft-telemetry/solution && mvn clean wildfly:dev

# Demo 3 : compiler d'abord le serveur MCP
cd demo-project/demo-3-mcp/mcp-server && mvn clean package
cd demo-project/demo-3-mcp/solution && mvn clean wildfly:dev

# Tester
curl -X POST -H "Content-Type: text/plain" -d "Raconte-moi une blague viking" http://localhost:8080/demo-1/api/chat
curl -X POST -H "Content-Type: text/plain" -H "X-Session-Id: test-123" -d "Quelles expéditions sont disponibles ?" http://localhost:8080/demo-2/api/chat

# Slides
cd slides && python3 -m http.server 8000
```

Il n'y a pas de tests unitaires dans ce projet. La validation est faite manuellement via curl ou les IUs web.

## Structure du Projet

```
slides/index.html              <- Présentation Reveal.js (tout-en-un)
demo-project/
├── pom.xml                    <- POM parent (versions centralisées)
├── demo-1-ai-agent/           <- Skald Viking injectable (blagues et chansons) (@RegisterAIService)
│   ├── base/                  <- Squelette avec TODOs
│   └── solution/              <- Référence complète
├── demo-2-ft-telemetry/       <- Inscription aux expéditions vikings (Memory + RAG + Tools + Fault Tolerance + Telemetry)
│   ├── base/
│   └── solution/
└── demo-3-mcp/                <- Intégration MCP (Model Context Protocol)
    ├── mcp-server/            <- Serveur MCP standalone (fat JAR)
    ├── base/
    └── solution/
```

## Règles Critiques

- **Synchronisation base/solution** : les classes métier partagées (modèles, repositories, tools) doivent être identiques dans `base/` et `solution/`. Seules les annotations/config pour le live coding diffèrent.
- **Les 6 fichiers `index.html`** (3 démos x 2 modules) doivent rester cohérents pour les éléments partagés (style, animation, scroll).
- **Package** : `com.example.demoN` (N = 1, 2 ou 3).
- **Langue** : Français pour le code, `@SystemMessage`, et les IUs.
- **MicroProfile Config** : le préfixe `.config.` est obligatoire pour les propriétés de builder (`dev.langchain4j.cdi.plugin.<name>.config.<prop>=val`). Sans lui, la propriété est ignorée silencieusement.
- Chaque module WAR nécessite un `beans.xml` dans `WEB-INF/` et un `JaxRsActivator.java` avec `@ApplicationPath("api")`.
