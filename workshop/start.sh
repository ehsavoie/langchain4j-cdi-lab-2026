#!/bin/bash
# Launch the workshop guide
# Usage: ./start.sh

PORT=${1:-8001}
URL="http://localhost:$PORT"

echo "📘 LangChain4j-CDI Workshop — JavaOne 2026"
echo "============================================"
echo ""
echo "🌐 Workshop: $URL"
echo ""
echo "Ctrl+C to stop"
echo ""

cd "$(dirname "$0")"

# Open browser (works on macOS and Linux)
if command -v open &>/dev/null; then
    open "$URL"
elif command -v xdg-open &>/dev/null; then
    xdg-open "$URL"
fi

python3 -m http.server $PORT
