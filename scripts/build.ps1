# ImmersiAds - Build Script (Windows)
# Usage: .\scripts\build.ps1 [--offline] [--no-daemon] [gradle-args...]

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Set-Location $Script:PROJECT_ROOT

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Build" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$gradleArgs = @("assembleDebug")
$gradleArgs += $args

$exitCode = Invoke-Gradle -Arguments $gradleArgs

if ($exitCode -eq 0) {
    Write-Host ""
    Write-Success "Build successful"
    Write-Info "APK: app/build/outputs/apk/debug/app-debug.apk"
} else {
    Write-Host ""
    Write-Error "Build failed (exit code $exitCode)"
}
exit $exitCode