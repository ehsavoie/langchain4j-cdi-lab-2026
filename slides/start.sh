#!/bin/bash
# Launch Reveal.js slides with speaker view for iPad Sidecar
# Usage: ./start.sh

PORT=${1:-8000}

echo "🎬 LangChain4j-CDI — JavaOne 2026"
echo "==================================="
echo ""
echo "📺 Slides (main screen)    : http://localhost:$PORT"
echo "📝 Speaker view (iPad)     : http://localhost:$PORT/?view=speaker"
echo ""
echo "💡 Tip: Press 'S' on slides to open speaker view"
echo "💡 Speaker view opens in a new window"
echo "💡 Drag this window to your iPad in Sidecar"
echo ""
echo "Ctrl+C to stop"
echo ""

cd "$(dirname "$0")"

# Open browser (works on macOS and Linux)
if command -v open &>/dev/null; then
    open "http://localhost:$PORT"
elif command -v xdg-open &>/dev/null; then
    xdg-open "http://localhost:$PORT"
fi

python3 -m http.server $PORT
