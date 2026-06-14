@echo off
REM ImmersiAds — Run on Emulator/Device (Windows)
REM Usage: scripts\run.bat [--offline]

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\run.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
