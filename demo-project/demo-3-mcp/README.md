# Démo 3 - Hnefatafl au Grand Thing avec MCP

Troisième démo pour Devoxx France : jouez au **Hnefatafl** (jeu de pierres runiques nordique) contre une IA qui utilise le protocole **MCP** pour gérer les lancers sur WildFly.

## Vue d'ensemble

1. Un **serveur MCP autonome** expose un outil de lancer de dés (`roll` pour 2d6)
2. Un **agent IA Jarl** (Ragnar le Skald) se connecte à ce serveur via `McpToolProvider`
3. L'agent anime une partie de Hnefatafl : lance les runes via MCP, applique les règles, et annonce le destin du guerrier
4. Une **interface web thème viking** permet de jouer en temps réel

**MCP pour le jeu** : Vos agents Jakarta EE peuvent piloter des mécaniques de jeu externes via des outils standardisés !

## Prérequis

- **Java 21+**, **Maven 3.8+**
- **Ollama** avec `ministral-3:3b` :
  ```bash
  ollama pull ministral-3:3b
  ollama serve
  ```

## Règles du Hnefatafl (lancer de pierres runiques)

Le **Hnefatafl** utilise 2 pierres runiques à six faces :

**Lancer d'ouverture (premier lancer d'un tour) :**
- **7 ou 11** : Faveur d'Odin — le guerrier **GAGNE** immédiatement !
- **2, 3 ou 12** : Malédiction des Nornes — le guerrier **PERD** immédiatement !
- **Tout autre nombre** (4, 5, 6, 8, 9, 10) : ce nombre devient la **Rune Marquée**

**Phase de la Rune (si une rune a été marquée) :**
- Le guerrier continue de lancer
- S'il relance **la Rune Marquée** : il **GAGNE** !
- S'il lance un **7** : Ragnarök — il **PERD** !
- Tout autre nombre : pas de décision, on relance

## Structure du Projet

```
demo-3-mcp/
├── pom.xml                          # POM agrégateur
├── mcp-server/                      # Serveur MCP de lancer de dés (JAR)
│   └── src/main/java/org/acme/
│       └── DiceRoller.java          # Outil MCP : lance N dés à 6 faces
├── base/                            # Base pour le live coding
│   ├── src/main/java/com/example/demo3/
│   │   ├── JaxRsActivator.java
│   │   ├── CasinoDealerAI.java      # TODO : @RegisterAIService (Jarl du Thing)
│   │   └── GameResource.java        # TODO : @Inject + appel
│   └── src/main/webapp/
│       ├── WEB-INF/beans.xml
│       └── index.html               # Interface viking (prête !)
└── solution/                        # Solution complète
    ├── src/main/java/com/example/demo3/
    │   ├── CasinoDealerAI.java      # Complet (Ragnar le Skald, Jarl)
    │   ├── ChatMemoryProviderBean.java
    │   ├── LastDiceRollChatMemory.java
    │   └── GameResource.java        # Complet
    └── src/main/webapp/
        ├── WEB-INF/beans.xml
        └── index.html               # Interface viking
```

## Démarrage

### Option 1 : Démarrage rapide avec wildfly:dev (recommandé pour le live coding)

```bash
# 1. Compiler le serveur MCP de dés
cd demo-3-mcp/mcp-server
mvn clean package

# 2. Lancer l'application avec hot reload (base ou solution)
cd ../base    # ou ../solution
mvn clean wildfly:dev
```

L'application est disponible sur **http://localhost:8080/demo-3/** avec l'interface viking.

### Option 2 : Build complet avec serveur provisionné

```bash
# 1. Builder l'ensemble (serveur MCP + WAR avec WildFly provisionné)
cd demo-3-mcp/solution  # ou base
mvn clean install

# 2. Démarrer le serveur WildFly provisionné
./target/server/bin/standalone.sh -Djboss.socket.binding.port-offset=10
```

L'application est alors disponible sur **http://localhost:8090/** (port 8080 + décalage 10).

### Vérification

```bash
# Vérification de santé
curl http://localhost:8080/demo-3/api/game/health
# ou avec décalage
curl http://localhost:8090/api/game/health
```

## Guide du Live Coding

### Étape 1 : Comprendre le serveur MCP de dés

Examiner `DiceRoller.java` : outil MCP qui lance N dés à 6 faces via stdio/JSON-RPC 2.0.

L'outil principal :
- `roll` : Lance N dés avec `{"numberOfDice": 2}`

### Étape 2 : Annoter CasinoDealerAI (le Jarl du Thing)

```java
@RegisterAIService(chatModelName = "mistral", toolProviderName = "mcp")
public interface CasinoDealerAI {
    @SystemMessage("""
        Tu es Ragnar le Skald, le Jarl qui anime le Hnefatafl au Grand Thing...
        [règles du Hnefatafl]
        [format : RUNES: [X, Y] / TOTAL: [somme] / DESTIN: [résultat]]
        """)
    String play(@UserMessage String playerAction);
}
```

### Étape 3 : Câbler le endpoint REST

```java
@Inject CasinoDealerAI gameMaster;

@POST @Path("/play")
public String play(String playerAction) {
    return gameMaster.play(playerAction);
}
```

### Étape 4 : Configurer et tester

Décommenter dans `microprofile-config.properties` :
```properties
dev.langchain4j.cdi.plugin.mistral.class=dev.langchain4j.model.ollama.OllamaChatModel
dev.langchain4j.cdi.plugin.mistral.config.base-url=http://localhost:11434
dev.langchain4j.cdi.plugin.mistral.config.model-name=ministral-3:3b
```

Ouvrir **http://localhost:8080/demo-3/** et jouer :

**Pour lancer les runes :**
- `Lance les runes`
- `Jette`
- `Nouvelle partie`

**Pour continuer (phase de la rune) :**
- `Relance`
- `Continue`

## Flux d'Exécution (simplifié)

```
Endpoint REST -> CasinoDealerAI.play()
    -> Le LLM reçoit : "Lance les runes"
    -> Le LLM décide d'utiliser roll(numberOfDice=2)
    -> McpToolProvider -> JSON-RPC -> Serveur MCP de dés
    -> Le serveur lance 2d6 et retourne [4, 3]
    -> Le LLM reçoit le résultat brut
    -> Le LLM affiche le FORMAT OBLIGATOIRE :
       RUNES: [4, 3]
       TOTAL: 7
       DESTIN: Faveur d'Odin ! Le guerrier gagne !
    -> Réponse complète renvoyée au guerrier
```

## Exemples d'Interaction

### Exemple 1 : Faveur d'Odin

**Guerrier :** `Lance les runes`

**Ragnar le Skald :**
```
Skál ! Que les runes décident !

RUNES: [4, 3]
TOTAL: 7
DESTIN: Faveur d'Odin ! Le guerrier gagne !

Par les dieux du Nord, un 7 ! La victoire est tienne !
```

### Exemple 2 : Malédiction des Nornes

**Guerrier :** `Nouvelle partie`

**Ragnar le Skald :**
```
RUNES: [1, 1]
TOTAL: 2
DESTIN: Yeux de serpent ! Malédiction des Nornes — le guerrier perd !

Les Nornes n'ont pas été clémentes... Tente ta chance à nouveau !
```

### Exemple 3 : Rune marquée puis atteinte

**Guerrier :** `Lance les runes`

**Ragnar :**
```
RUNES: [3, 5]
TOTAL: 8
DESTIN: La rune marquée est 8. Continue de lancer, guerrier !
```

**Guerrier :** `Relance`

**Ragnar :**
```
RUNES: [2, 6]
TOTAL: 8
DESTIN: Rune atteinte ! Le guerrier gagne !

La rune t'a souri — tu es digne de Valhalla !
```

## Points Clés

1. **MCP pour le jeu** : Les mécaniques de jeu sont externalisées dans un serveur MCP
2. **L'IA comme Jarl** : Le LLM lance les runes via MCP, applique les règles et annonce le destin
3. **Séparation des responsabilités** : Le serveur MCP gère le hasard, l'IA gère la logique de jeu
4. **Extensibilité** : Facile d'ajouter d'autres jeux de dés nordiques

## Résolution de Problèmes

- **Le serveur MCP ne démarre pas** : Vérifier que le JAR est compilé (`cd mcp-server && mvn package`)
- **L'agent ne répond pas** : Vérifier que `CasinoDealerAI` est annoté avec `@RegisterAIService`
- **Les runes ne sont pas lancées** : Vérifier les logs WildFly pour les appels d'outils MCP
- **Lancer la solution** : `cd solution && mvn clean wildfly:dev`

## Ressources

- **Protocole MCP** : https://modelcontextprotocol.io
- **LangChain4j-CDI** : https://github.com/langchain4j/langchain4j-cdi
- **LangChain4j** : https://docs.langchain4j.dev
- **WildFly** : https://www.wildfly.org
