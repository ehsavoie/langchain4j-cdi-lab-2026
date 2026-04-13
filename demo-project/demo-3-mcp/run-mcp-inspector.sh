  #!/bin/sh
# Lance le MCP Inspector UI connecté au serveur MCP Helidon.
# Prérequis : Node.js / npm doivent être installés.
#
# 1. Démarrez d'abord le serveur MCP :
#      cd mcp-server && mvn clean package && java -jar target/casino-dice-roller.jar
#
# 2. Puis lancez ce script :
#      ./run-mcp-inspector.sh

npx @modelcontextprotocol/inspector --url http://localhost:8090/mcp
