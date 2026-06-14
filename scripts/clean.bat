@echo off
REM ImmersiAds — Clean Script (Windows)
REM Removes all generated artifacts.
REM Usage: scripts\clean.bat

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0..\scripts\clean.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
