# ImmersiAds - Verify Script (Windows)
# Runs lint, tests, and build sequentially.
# Usage: .\scripts\verify.ps1 [--offline]

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Set-Location $Script:PROJECT_ROOT

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Full Verification" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$gradleExtra = @()
if ($args[0] -eq "--offline") {
    $gradleExtra += "--offline"
}

Write-Step "Step 1/3: Lint"
& "$SCRIPT_DIR\lint.ps1" @gradleExtra
Write-Host ""

Write-Step "Step 2/3: Unit Tests"
& "$SCRIPT_DIR\test.ps1" @gradleExtra
if ($LASTEXITCODE -ne 0) {
    Write-Error "Tests failed - aborting verification"
    exit $LASTEXITCODE
}
Write-Host ""

Write-Step "Step 3/3: Build"
& "$SCRIPT_DIR\build.ps1" @gradleExtra
if ($LASTEXITCODE -ne 0) {
    Write-Error "Build failed - aborting verification"
    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Success "All checks passed!"
Write-Host "========================================" -ForegroundColor Green