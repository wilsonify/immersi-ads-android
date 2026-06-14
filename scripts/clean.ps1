# ImmersiAds - Clean Script (Windows)
# Removes all generated artifacts.
# Usage: .\scripts\clean.ps1

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Set-Location $Script:PROJECT_ROOT

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Clean" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$dirsToClean = @(
    "app\build",
    ".gradle",
    "build"
)

foreach ($dir in $dirsToClean) {
    if (Test-Path $dir) {
        Write-Info "Removing $dir\"
        Remove-Item -Path $dir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

Write-Host ""
Write-Success "Clean complete"
Write-Host ""
Write-Info "To also clear Gradle cache (global):"
$cachePath = [IO.Path]::Combine($env:USERPROFILE, ".gradle", "caches")
Write-Info "  Remove-Item -Path '$cachePath' -Recurse -Force"
$distsPath = [IO.Path]::Combine($env:USERPROFILE, ".gradle", "wrapper", "dists", "gradle-8.11.1*")
Write-Info "To clear only this project's cached Gradle distribution:"
Write-Info "  Remove-Item -Path '$distsPath' -Recurse -Force"