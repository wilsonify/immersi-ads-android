# ImmersiAds - Run on Emulator/Device (Windows)
# Usage: .\scripts\run.ps1 [--offline] [--no-daemon] [gradle-args...]

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Set-Location $Script:PROJECT_ROOT

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Run" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$gradleArgs = @("installDebug")
$gradleArgs += $args

$exitCode = Invoke-Gradle -Arguments $gradleArgs

if ($exitCode -ne 0) {
    Write-Host ""
    Write-Error "Install failed (exit code $exitCode)"
    exit $exitCode
}

Write-Host ""
Write-Success "APK installed"

$androidHome = Get-AndroidHome
if (Get-Command "adb" -ErrorAction SilentlyContinue) {
    $adb = "adb"
} elseif ($androidHome) {
    $adb = Join-Path $androidHome "platform-tools\adb.exe"
} else {
    Write-Warning "adb not found - app installed but cannot launch"
    exit 0
}

Write-Info "Launching app..."
$launchArgs = @("shell", "am", "start", "-n", "com.immersiads.app.debug/com.immersiads.app.MainActivity")
& $adb $launchArgs 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Success "App launched"
} else {
    Write-Warning "Could not launch app - ensure emulator/device is connected"
}
