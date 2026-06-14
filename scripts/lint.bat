@echo off
REM ImmersiAds — Lint Script (Windows)
REM Usage: scripts\lint.bat [--offline]

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\lint.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
