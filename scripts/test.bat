@echo off
REM ImmersiAds — Test Script (Windows)
REM Usage: scripts\test.bat [--offline]

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\test.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
