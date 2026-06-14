# ImmersiAds - Lint Script (Windows)
# Usage: .\scripts\lint.ps1 [--offline] [--no-daemon] [gradle-args...]

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Set-Location $Script:PROJECT_ROOT

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Lint" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$gradleArgs = @("lintDebug")
$gradleArgs += $args

$exitCode = Invoke-Gradle -Arguments $gradleArgs

if ($exitCode -eq 0) {
    Write-Host ""
    Write-Success "Lint passed"
    Write-Info "Report: app/build/reports/lint-results-debug.html"
} else {
    Write-Host ""
    Write-Warning "Lint found issues (exit code $exitCode)"
    Write-Info "Report: app/build/reports/lint-results-debug.html"
    Write-Info "Lint errors are informational - they do not block the build"
}
exit $exitCode