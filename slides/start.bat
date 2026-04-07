@echo off
REM Launch Reveal.js slides
REM Usage: start.bat [port]

set PORT=%1
if "%PORT%"=="" set PORT=8000

echo 🎬 LangChain4j-CDI — JavaOne 2026
echo ===================================
echo.
echo 📺 Slides (main screen)    : http://localhost:%PORT%
echo 📝 Speaker view (iPad)     : http://localhost:%PORT%/?view=speaker
echo.
echo 💡 Tip: Press 'S' on slides to open speaker view
echo.
echo Ctrl+C to stop
echo.

start http://localhost:%PORT%
python3 -m http.server %PORT%