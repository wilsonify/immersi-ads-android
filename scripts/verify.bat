@echo off
REM ImmersiAds — Verify Script (Windows)
REM Runs lint, tests, and build sequentially.
REM Usage: scripts\verify.bat [--offline]

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\verify.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
