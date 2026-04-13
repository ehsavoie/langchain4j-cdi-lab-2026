# Serveur MCP de Dés

Serveur MCP (Model Context Protocol) autonome pour le lancer de pierres runiques.

## Description

Ce serveur expose un outil de lancer de dés via le protocole MCP sur stdio (JSON-RPC 2.0). Il est utilisé par l'agent `CasinoDealerAI` (Ragnar le Skald) pour gérer les mécaniques de jeu (lancers de pierres runiques).

## Outil Disponible

| Outil | Description | Paramètres |
|-------|-------------|------------|
| `roll` | Lance un nombre de dés à 6 faces | `numberOfDice` (int) : nombre de dés |

## Compilation

```bash
cd demo-3-mcp/mcp-server
mvn clean package
```

Le JAR généré se trouve dans `target/demo-3-mcp-dice-server.jar`.

## Utilisation

### En tant que serveur MCP (mode normal)

Le serveur est lancé **automatiquement** par le module `solution` ou `base` via le producteur CDI `McpConfig`. Il communique via stdin/stdout avec l'application WildFly.

Vous **n'avez pas besoin** de le démarrer manuellement pour la démo.

### Test manuel (mode autonome)

Pour tester le serveur de façon indépendante :

```bash
java -jar target/demo-3-mcp-dice-server.jar
```

Puis envoyer des commandes JSON-RPC sur stdin. Exemples :

**1. Initialisation**
```json
{"jsonrpc":"2.0","id":1,"method":"initialize","params":{}}
```

**2. Lister les outils**
```json
{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}
```

**3. Appel d'outil (lancer 2 pierres runiques)**
```json
{"jsonrpc":"2.0","id":3,"method":"tools/call","params":{"name":"roll","arguments":{"numberOfDice":2}}}
```

**4. Lancer 3 dés**
```json
{"jsonrpc":"2.0","id":4,"method":"tools/call","params":{"name":"roll","arguments":{"numberOfDice":3}}}
```

## Protocole MCP

Le serveur implémente le protocole MCP version `2024-11-05` :
- Communication via **stdin/stdout**
- Format **JSON-RPC 2.0**
- Transport **stdio** (pas de réseau)

## Architecture

```
+---------------------+
|  WildFly (solution)  |
|                      |
|  +----------------+  |
|  | CasinoDealerAI  |  |  Le LLM décide de lancer
|  +-------+--------+  |  les runes (tool calling)
|          |           |
|  +-------v--------+  |
|  |  McpConfig     |  |  Producteur CDI qui lance
|  |  (Producteur)  |  |  le processus MCP
|  +-------+--------+  |
+-----------+-----------+
            | stdio
            | (JSON-RPC)
+-----------v-----------+
|  Serveur MCP de Dés   |
|  (ce module)          |
|                       |
|  - roll               |  Lance N dés à 6 faces
|                       |  et retourne les résultats
+-----------------------+
```

## Logs

Les logs sont envoyés vers stderr :
```
[main] INFO org.acme.DiceRoller - Lancer de dés : 2 dés
[main] INFO org.acme.DiceRoller - Dé 0 : 4
```

## Résolution de Problèmes

**Le serveur ne répond pas**
- Vérifier que le JAR est correctement compilé : `ls -lh target/demo-3-mcp-dice-server.jar`
- Consulter les logs dans la console WildFly

**Erreur "Unable to start MCP server"**
- Le chemin vers le JAR dans `McpConfig.java` est incorrect
- Le JAR n'a pas les permissions d'exécution

**Les dés ne sont pas lancés**
- Vérifier que le `McpToolProvider` est correctement injecté avec `@Named("mcp")`
- Vérifier que le LLM supporte le tool calling (Ollama avec des modèles récents)

## Ressources

- **Protocole MCP** : https://modelcontextprotocol.io
- **Spécification JSON-RPC 2.0** : https://www.jsonrpc.org/specification
