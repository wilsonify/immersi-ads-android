# ImmersiAds - Test Script (Windows)
# Usage: .\scripts\test.ps1 [--offline] [--no-daemon] [gradle-args...]

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Set-Location $Script:PROJECT_ROOT

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Unit Tests" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$gradleArgs = @("testDebugUnitTest")
$gradleArgs += $args

$exitCode = Invoke-Gradle -Arguments $gradleArgs

if ($exitCode -eq 0) {
    Write-Host ""
    Write-Success "All tests passed"
    Write-Info "Report: app/build/reports/tests/testDebugUnitTest/index.html"
} else {
    Write-Host ""
    Write-Error "Tests failed (exit code $exitCode)"
}
exit $exitCode