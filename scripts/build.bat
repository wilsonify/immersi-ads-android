@echo off
REM ImmersiAds — Build Script (Windows)
REM Usage: scripts\build.bat [--offline]

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\build.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
