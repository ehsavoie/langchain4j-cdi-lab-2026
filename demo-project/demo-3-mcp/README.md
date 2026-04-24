# Démo 3 - Hnefatafl au Grand Thing avec MCP

Troisième démo pour Devoxx France : jouez au **Hnefatafl** (jeu de pierres runiques nordique) contre une IA qui utilise le protocole **MCP** (Model Context Protocol) pour gérer les lancers de dés sur WildFly.

## Vue d'ensemble

1. Un **serveur MCP autonome** (`mcp-server/`) expose un outil de lancer de dés (`roll` pour 2d6) via Streamable HTTP
2. Un **agent IA Jarl** (`HnefataflJarlAI`) se connecte à ce serveur via `McpToolProvider`
3. L'agent incarne Ragnar le Skald au Grand Thing : il lance les runes via MCP, applique les règles du Hnefatafl, et annonce le destin du guerrier
4. Une **interface web thème viking** permet de jouer en temps réel

**Message clé** : « MCP est le JDBC de l'IA — vos agents Jakarta EE communiquent avec n'importe quel serveur d'outils externe »

## Prérequis

- **Java 21+**, **Maven 3.8+**
- **Ollama** (local) ou une **clé API Mistral AI** (distant)

```bash
# Option A : Ollama (local)
ollama pull ministral-3:3b
ollama serve

# Option B : Mistral AI (distant)
export MISTRAL_API_KEY=your-key-here
```

## Règles du Hnefatafl (lancer de pierres runiques)

Le **Hnefatafl** utilise 2 pierres runiques à six faces (2d6) :

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
├── pom.xml                               # POM agrégateur
├── mcp-server/                           # Serveur MCP de lancer de dés (JAR autonome)
│   ├── pom.xml                           # Helidon 4 + langchain4j-cdi-mcp-server
│   └── src/main/java/org/acme/
│       └── DiceRoller.java               # @Tool: roll(numberOfDice) → résultats des dés
│
├── base/                                 # Squelette pour le live coding
│   ├── pom.xml
│   ├── src/main/java/com/example/demo3/
│   │   ├── JaxRsActivator.java
│   │   ├── HnefataflJarlAI.java          # TODO: @RegisterAIService + @SystemMessage
│   │   └── GameResource.java             # TODO: @Inject + appeler l'agent
│   └── src/main/webapp/
│       ├── WEB-INF/beans.xml
│       └── index.html                    # Interface viking (prête !)
│
└── solution/                             # Implémentation de référence complète
    ├── pom.xml
    ├── src/main/java/com/example/demo3/
    │   ├── HnefataflJarlAI.java          # Complet : Ragnar le Skald, toutes les règles
    │   ├── ChatMemoryProviderBean.java   # Mémoire de session
    │   ├── LastDiceRollChatMemory.java   # Suivi de la rune marquée
    │   └── GameResource.java             # Complet
    └── src/main/webapp/
        ├── WEB-INF/beans.xml
        └── index.html                    # Interface viking
```

## Démarrage

### Étape 1 : Compiler le serveur MCP de dés

```bash
cd demo-project/demo-3-mcp/mcp-server
mvn clean package
```

Cela produit `target/casino-dice-roller.jar`. Le serveur expose l'outil `roll` en Streamable HTTP sur le port 8090.

Le serveur démarre automatiquement avec WildFly via le plugin Maven. Alternativement, lancez-le manuellement :

```bash
java -jar target/casino-dice-roller.jar
```

### Étape 2 : Lancer l'application WildFly

```bash
cd demo-project/demo-3-mcp/base    # ou solution/
mvn clean wildfly:dev
```

L'application est disponible sur **http://localhost:8080/demo-3/**

### Vérification

```bash
# Santé de l'application
curl http://localhost:8080/demo-3/api/game/health

# Démarrer une partie directement
curl http://localhost:8080/demo-3/api/game/start
```

## Guide du Live Coding

### Étape 1 : Comprendre le serveur MCP de dés

Examiner `DiceRoller.java` — un simple bean CDI annoté `@Tool` qui lance N dés via `java.util.Random` :

```java
@ApplicationScoped
public class DiceRoller {

    @Tool(description = "Lance un nombre de dés et retourne les résultats")
    public String roll(@ToolArg(description = "Le nombre de dés") int numberOfDice) {
        int[] result = new int[numberOfDice];
        for (int i = 0; i < numberOfDice; i++) {
            result[i] = new Random().nextInt(1, 7);
        }
        return Arrays.toString(result);
    }
}
```

Le framework `langchain4j-cdi-mcp-server` expose cet outil en JSON-RPC 2.0 via Streamable HTTP — aucun serveur HTTP à écrire.

### Étape 2 : Annoter HnefataflJarlAI

Ouvrir `HnefataflJarlAI.java` et ajouter `@RegisterAIService` avec `toolProviderName = "mcp"` :

```java
import dev.langchain4j.cdi.spi.RegisterAIService;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAIService(chatModelName = "mistral", toolProviderName = "mcp")
public interface HnefataflJarlAI {

    @SystemMessage("""
        Tu es Ragnar le Skald, le Jarl qui anime le Hnefatafl au Grand Thing des guerriers du Nord.

        RÈGLES DU HNEFATAFL :
        - Lance 2 pierres runiques avec roll(numberOfDice=2).
        - Lancer d'ouverture : 7 ou 11 → Faveur d'Odin (GAGNE) !
          2, 3 ou 12 → Malédiction des Nornes (PERD) !
          Autre → ce total devient la Rune Marquée.
        - Phase de la Rune : relance jusqu'à atteindre la Rune Marquée (GAGNE) ou un 7 (PERD).

        FORMAT OBLIGATOIRE pour chaque lancer :
        RUNES: [X, Y]
        TOTAL: [somme]
        DESTIN: [ce qui s'est passé]

        Réponds en français, sois concis, expressions nordiques bienvenues !
        """)
    String play(@UserMessage String playerAction);
}
```

### Étape 3 : Câbler le endpoint REST

Ouvrir `GameResource.java` et injecter l'agent :

```java
@Inject
HnefataflJarlAI gameMaster;

@POST @Path("/play")
@Consumes(MediaType.TEXT_PLAIN) @Produces(MediaType.TEXT_PLAIN)
public String play(String playerAction) {
    return gameMaster.play(playerAction);
}

@GET @Path("/start")
@Produces(MediaType.TEXT_PLAIN)
public String start() {
    return gameMaster.play("Salve ! Je suis prêt à jouer au Hnefatafl.");
}
```

### Étape 4 : Configurer le modèle et le transport MCP

Décommenter dans `microprofile-config.properties` :

```properties
# Modèle IA (Option A : Mistral AI)
dev.langchain4j.cdi.plugin.mistral.class=dev.langchain4j.model.mistralai.MistralAiChatModel
dev.langchain4j.cdi.plugin.mistral.config.api-key=${MISTRAL_API_KEY}
dev.langchain4j.cdi.plugin.mistral.config.model-name=mistral-small-latest

# Modèle IA (Option B : Ollama)
# dev.langchain4j.cdi.plugin.mistral.class=dev.langchain4j.model.ollama.OllamaChatModel
# dev.langchain4j.cdi.plugin.mistral.config.base-url=http://localhost:11434
# dev.langchain4j.cdi.plugin.mistral.config.model-name=ministral-3:3b

# Transport MCP (Streamable HTTP → serveur de dés)
dev.langchain4j.cdi.plugin.ssetransport.class=dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport
dev.langchain4j.cdi.plugin.ssetransport.config.url=http://localhost:8090/mcp

# Client MCP
dev.langchain4j.cdi.plugin.mcpclient.class=dev.langchain4j.mcp.client.DefaultMcpClient
dev.langchain4j.cdi.plugin.mcpclient.config.transport=lookup:@ssetransport

# Tool Provider (nommé "mcp" pour @RegisterAIService)
dev.langchain4j.cdi.plugin.mcp.class=dev.langchain4j.mcp.McpToolProvider
dev.langchain4j.cdi.plugin.mcp.config.mcpClients=lookup:@mcpclient
```

### Étape 5 : Jouer

Ouvrir **http://localhost:8080/demo-3/** et jouer :

**Pour lancer les runes :**
- `Lance les runes`
- `Jette`
- `Nouvelle partie`

**Pour continuer (phase de la rune) :**
- `Relance`
- `Continue`

## Flux d'Exécution

```
Navigateur → GET /api/game/start
  → HnefataflJarlAI.play("Salve ! Je suis prêt à jouer au Hnefatafl.")
    → Le LLM décide d'appeler roll(numberOfDice=2)
    → McpToolProvider → HTTP JSON-RPC → Serveur MCP (port 8090)
    → Serveur lance 2d6, retourne [4, 3]
    → Le LLM reçoit le résultat et rédige la réponse :

       RUNES: [4, 3]
       TOTAL: 7
       DESTIN: Faveur d'Odin ! Le guerrier gagne !

       Par les dieux du Nord, un 7 ! La victoire est tienne !
```

## Exemples d'Interaction

**Guerrier :** `Lance les runes`

**Ragnar le Skald :**
```
Skál ! Que les runes décident !

RUNES: [4, 3]
TOTAL: 7
DESTIN: Faveur d'Odin ! Le guerrier gagne !

Par les dieux du Nord ! La victoire est tienne, guerrier !
```

---

**Guerrier :** `Nouvelle partie`

**Ragnar le Skald :**
```
RUNES: [3, 5]
TOTAL: 8
DESTIN: La rune marquée est 8. Continue de lancer, guerrier !
```

**Guerrier :** `Relance`

**Ragnar le Skald :**
```
RUNES: [2, 6]
TOTAL: 8
DESTIN: Rune atteinte ! Le guerrier gagne !

La rune t'a souri — tu es digne du Valhalla !
```

## Points Clés MCP

1. **Découplage** : le serveur de dés est un processus indépendant (autre JVM, autre langage possible) — `McpToolProvider` fait le pont
2. **Protocole standard** : JSON-RPC 2.0 sur Streamable HTTP — n'importe quel serveur MCP compatible peut être branché
3. **Configuration pure** : le transport, le client, et le tool provider sont tous enregistrés via MicroProfile Config — aucun code Java à écrire dans l'application WildFly
4. **`lookup:@`** : le préfixe `lookup:@ssetransport` dans la config MCP indique à LangChain4j-CDI d'injecter le bean portant le nom `ssetransport`

## Résolution de Problèmes

- **Le serveur MCP ne démarre pas** : Vérifier que le JAR est compilé (`cd mcp-server && mvn clean package`)
- **`Connection refused` sur port 8090** : Le serveur MCP n'est pas démarré — relancer `java -jar mcp-server/target/casino-dice-roller.jar`
- **L'agent ne répond pas** : Vérifier que `HnefataflJarlAI` est annoté avec `@RegisterAIService`
- **Les runes ne sont pas lancées** : Vérifier les logs WildFly pour les appels MCP (`logRequests=true` dans la config)
- **Lancer la solution directement** : `cd solution && mvn clean wildfly:dev`

## Ressources

- **Protocole MCP** : https://modelcontextprotocol.io
- **LangChain4j-CDI** : https://github.com/langchain4j/langchain4j-cdi
- **LangChain4j MCP** : https://docs.langchain4j.dev/integrations/mcp
- **WildFly** : https://www.wildfly.org
