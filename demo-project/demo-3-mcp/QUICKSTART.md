# Démo 3 - Guide de Démarrage Rapide

## Démarrage Rapide (3 minutes)

### 1. Démarrer Ollama

```bash
ollama pull ministral-3:3b
ollama serve
```

> **Note** : Laisser ce terminal ouvert. Ollama doit tourner sur `localhost:11434`.

### 2. Compiler le serveur MCP de dés

```bash
cd demo-3-mcp/mcp-server
mvn clean package
```

Produit `target/demo-3-mcp-dice-server.jar`.

### 3. Démarrer l'application

```bash
cd ../solution/
mvn clean wildfly:dev
```

WildFly se provisionne automatiquement via Galleon (première exécution ~2 minutes).

### 4. Jouer au Hnefatafl !

Ouvrir http://localhost:8080/demo-3/ — l'interface viking se charge automatiquement.

Essayez ces commandes :

- `Lance les runes` — Ragnar lance 2 pierres runiques pour déterminer votre destin
- `Relance` — Continuer pendant la phase de la rune
- `Nouvelle partie` — Recommencer une nouvelle manche

Ou via curl :

```bash
curl -X POST -H "Content-Type: text/plain" \
  -d "Lance les runes" \
  http://localhost:8080/demo-3/api/game/play
```

## Ce Que Vous Verrez

- **Ragnar le Skald** : Un agent IA Jarl du Grand Thing animant le Hnefatafl
- **Appels d'outils MCP** : Le LLM appelle `roll(numberOfDice=2)` via le protocole MCP
- **Règles du Hnefatafl** : 7/11 = Faveur d'Odin, 2/3/12 = Malédiction, autre = Rune Marquée

## Comment Ça Fonctionne

```
Guerrier -> JAX-RS -> CasinoDealerAI (@RegisterAIService)
  -> Le LLM décide d'appeler l'outil roll
  -> McpToolProvider -> JSON-RPC -> Serveur MCP de dés (stdio)
  -> Le serveur lance 2d6 -> retourne le résultat
  -> Le LLM applique les règles -> répond en personnage
```

## Arrêter Tout

```bash
# Dans le terminal WildFly : Ctrl+C
```

## Problèmes Courants

**"Serveur MCP introuvable"** :
- Vérifier que le JAR a été compilé : `ls mcp-server/target/demo-3-mcp-dice-server.jar`
- Recompiler si nécessaire : `cd mcp-server && mvn clean package`

**"Connection refused" sur le chat** :
- Vérifier qu'Ollama tourne : `curl http://localhost:11434/api/tags`
- Vérifier que le modèle est téléchargé : `ollama list`

**Port 8080 déjà utilisé** :
- Vérifier ce qui l'utilise : `lsof -i :8080`
- Ou utiliser le serveur provisionné avec décalage de port : `./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=10`

**Les runes ne sont pas lancées (le LLM invente les résultats)** :
- Vérifier les logs WildFly pour les traces d'appels d'outils MCP
- Essayer un modèle plus grand (`qwen2.5:7b`) pour une meilleure précision des appels d'outils
