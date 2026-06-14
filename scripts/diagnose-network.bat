@echo off
REM ImmersiAds — Network Diagnostics (Windows)
REM Tests connectivity to all required services.
REM Usage: scripts\diagnose-network.bat

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\diagnose-network.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
