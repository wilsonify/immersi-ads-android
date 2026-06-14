# ImmersiAds - Network Diagnostics (Windows)
# Tests connectivity to all required services.
# Usage: .\scripts\diagnose-network.ps1

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

Write-Host "============================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Network Diagnostics" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "--- Proxy Configuration ---" -ForegroundColor Cyan
if ($env:HTTP_PROXY) {
    Write-Info "HTTP_PROXY = $env:HTTP_PROXY"
}
if ($env:HTTPS_PROXY) {
    Write-Info "HTTPS_PROXY = $env:HTTPS_PROXY"
}
if (-not $env:HTTP_PROXY -and -not $env:HTTPS_PROXY) {
    Write-Info "No proxy configured"
}
Write-Host ""

Write-Host "--- DNS Resolution ---" -ForegroundColor Cyan
$hosts = @(
    "services.gradle.org",
    "github.com",
    "dl.google.com",
    "repo1.maven.org",
    "plugins.gradle.org",
    "release-assets.githubusercontent.com"
)
foreach ($hostname in $hosts) {
    try {
        $result = [System.Net.Dns]::GetHostAddresses($hostname)
        if ($result.Count -gt 0) {
            Write-Success "$hostname resolves"
            Write-Info "  $($result[0].IPAddressToString)"
        } else {
            Write-Warning "$hostname - no addresses found"
        }
    } catch {
        Write-Warning "$hostname - DNS resolution failed"
    }
}
Write-Host ""

Write-Host "--- HTTP Connectivity ---" -ForegroundColor Cyan
$tests = @(
    @{ Name = "Gradle distribution"; Url = "https://services.gradle.org/distributions/gradle-8.11.1-bin.zip" },
    @{ Name = "GitHub"; Url = "https://github.com" },
    @{ Name = "Google Maven"; Url = "https://dl.google.com/dl/android/maven2/" },
    @{ Name = "Maven Central"; Url = "https://repo1.maven.org/maven2/" },
    @{ Name = "Gradle Plugin Portal"; Url = "https://plugins.gradle.org" },
    @{ Name = "GitHub assets"; Url = "https://release-assets.githubusercontent.com" }
)
foreach ($t in $tests) {
    Write-Info "Testing: $($t.Name)..."
    try {
        $request = [System.Net.WebRequest]::Create($t.Url)
        $request.Method = "HEAD"
        $request.Timeout = 15000
        $response = $request.GetResponse()
        Write-Success "  HTTP $([int]$response.StatusCode)"
        $response.Close()
    } catch {
        Write-Warning "  Connection failed"
        Write-Action "  Check proxy/firewall settings"
    }
}
Write-Host ""

Write-Host "--- Gradle Configuration ---" -ForegroundColor Cyan
$wrapperProps = "$Script:PROJECT_ROOT\gradle\wrapper\gradle-wrapper.properties"
if (Test-Path $wrapperProps) {
    $content = Get-Content $wrapperProps
    $distUrl = (($content | Select-String "^distributionUrl").Line -replace '^distributionUrl=', '') -replace '\\:', ':'
    $timeout = (($content | Select-String "^networkTimeout").Line -replace '^networkTimeout=', '')
    $retries = (($content | Select-String "^retries=").Line -replace '^retries=', '')
    Write-Info "Distribution URL: $distUrl"
    Write-Info "Network timeout: ${timeout}ms"
    Write-Info "Retries: $retries"
    if ($retries -eq "0") {
        Write-Warning "Retries are disabled - consider increasing for unreliable networks"
    }
    if ([int]$timeout -lt 30000) {
        Write-Warning "Timeout is low (${timeout}ms) - may fail on slow connections"
    }
} else {
    Write-Warning "gradle-wrapper.properties not found"
}
Write-Host ""

Write-Host "--- Gradle Cache Status ---" -ForegroundColor Cyan
$gradleCache = "$env:USERPROFILE\.gradle"
if (Test-Path $gradleCache) {
    $size = (Get-ChildItem $gradleCache -Recurse -ErrorAction SilentlyContinue | Measure-Object -Property Length -Sum).Sum / 1MB
    Write-Info "Gradle cache size: $('{0:N2}' -f $size) MB"
    $distsPath = "$gradleCache\wrapper\dists"
    if (Test-Path $distsPath) {
        $distCount = (Get-ChildItem $distsPath -Directory -ErrorAction SilentlyContinue).Count
        Write-Info "Cached distributions: $distCount"
    }
} else {
    Write-Info "No Gradle cache found"
}

Write-Host ""
Write-Host "--- Android SDK ---" -ForegroundColor Cyan
$androidHome = Get-AndroidHome
if ($androidHome) {
    Write-Info "SDK location: $androidHome"
    if (Test-Path "$androidHome\licenses\android-sdk-license") {
        Write-Success "SDK licenses accepted"
    } else {
        Write-Warning "SDK licenses not accepted"
    }
} else {
    Write-Warning "Android SDK not found - set ANDROID_HOME"
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Cyan
Write-Host " Diagnostics complete" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "  If you see failures, check:"
Write-Host "    1. Are you behind a corporate proxy? Set HTTP_PROXY/HTTPS_PROXY"
Write-Host "    2. Can you reach GitHub? Try: curl -I https://github.com"
Write-Host "    3. Gradle timeout too short? Edit gradle-wrapper.properties"
Write-Host "    4. Need offline mode? Run with --offline flag"
Write-Host "    5. See docs/NETWORK.md for full troubleshooting guide"