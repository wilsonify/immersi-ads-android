# Shared functions for ImmersiAds developer scripts

$Script:PROJECT_ROOT = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)

function Write-Step($message) {
    Write-Host "
>>> $message" -ForegroundColor Cyan
}

function Write-Info($message) {
    Write-Host "  $message" -ForegroundColor White
}

function Write-Success($message) {
    Write-Host "  [PASS] $message" -ForegroundColor Green
}

function Write-Warning($message) {
    Write-Host "  [WARN] $message" -ForegroundColor Yellow
}

function Write-Error($message) {
    Write-Host "  [FAIL] $message" -ForegroundColor Red
}

function Write-Action($message) {
    Write-Host "  [ACTION] $message" -ForegroundColor Magenta
}

function Test-CommandExists($command) {
    $oldPreference = $ErrorActionPreference
    $ErrorActionPreference = 'Stop'
    try {
        Get-Command $command -ErrorAction Stop | Out-Null
        return $true
    } catch {
        return $false
    } finally {
        $ErrorActionPreference = $oldPreference
    }
}

function Get-JavaVersion {
    try {
        $versionString = java -version 2>&1
        $versionStr = "$versionString"
        if ($versionStr -match '"(\d+)') {
            return [int]$Matches[1]
        }
        if ($versionStr -match '"1\.(\d+)') {
            return [int]$Matches[1]
        }
    } catch {}
    return $null
}

function Get-AndroidHome {
    $paths = @(
        $env:ANDROID_HOME,
        $env:ANDROID_SDK_ROOT,
        [IO.Path]::Combine($env:LOCALAPPDATA, "Android", "Sdk"),
        [IO.Path]::Combine($env:USERPROFILE, "Android", "Sdk"),
        "C:\Android\Sdk"
    )
    foreach ($p in $paths) {
        if ($p -and (Test-Path $p)) { return $p }
    }
    return $null
}

function Invoke-NetworkTest($name, $url, $timeoutSeconds = 10) {
    try {
        $request = [System.Net.WebRequest]::Create($url)
        $request.Method = "HEAD"
        $request.Timeout = $timeoutSeconds * 1000
        $response = $request.GetResponse()
        $response.Close()
        return $true
    } catch {
        return $false
    }
}

function Invoke-Gradle {
    param([string[]]$Arguments, [switch]$NoDaemon)
    $cmd = ".\gradlew.bat"
    if ($IsMacOS -or $IsLinux) { $cmd = "./gradlew" }
    $args = @()
    if ($NoDaemon) { $args += "--no-daemon" }
    $args += "--console=auto"
    $args += $Arguments
    Write-Host "  Running: $cmd $($args -join ' ')" -ForegroundColor Gray
    & $cmd $args
    return $LASTEXITCODE
}