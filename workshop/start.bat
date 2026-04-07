@echo off
REM Launch the workshop guide
REM Usage: start.bat [port]

set PORT=%1
if "%PORT%"=="" set PORT=8001
set URL=http://localhost:%PORT%

echo 📘 LangChain4j-CDI Workshop — JavaOne 2026
echo ============================================
echo.
echo 🌐 Workshop: %URL%
echo.
echo Ctrl+C to stop
echo.

start %URL%
python3 -m http.server %PORT%
