@echo off
REM ImmersiAds — Setup Script (Windows)
REM Runs the PowerShell setup script.
REM Usage: scripts\setup.bat

powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0setup.ps1" %*
if %ERRORLEVEL% neq 0 exit /b %ERRORLEVEL%
