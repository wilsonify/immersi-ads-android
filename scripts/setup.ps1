# ImmersiAds - Setup Script (Windows)
# Validates prerequisites and provides remediation steps.
# Usage: .\scripts\setup.ps1

$ErrorActionPreference = "Stop"
$SCRIPT_DIR = Split-Path -Parent $PSCommandPath
. "$SCRIPT_DIR\lib\common.ps1"

$PASS = 0; $FAIL = 0; $WARN = 0

Write-Host "========================================" -ForegroundColor Cyan
Write-Host " ImmersiAds - Development Setup Check" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

Write-Step "Java"
$javaVer = Get-JavaVersion
if ($javaVer) {
    if ($javaVer -ge 17) {
        Write-Success "Java $javaVer found"
        $PASS++
    } else {
        Write-Warning "Java $javaVer found, but 17+ is required"
        $WARN++
        Write-Action "Install JDK 17 or later from https://adoptium.net/"
    }
} else {
    Write-Error "Java not found"
    $FAIL++
    Write-Action "Install JDK 17+ from https://adoptium.net/"
    Write-Action "After install, set JAVA_HOME environment variable"
}

Write-Step "Android SDK"
$androidHome = Get-AndroidHome
if ($androidHome) {
    Write-Success "Android SDK found at: $androidHome"
    $PASS++
    $env:ANDROID_HOME = $androidHome

    if (Test-Path "$androidHome\platforms\android-35") {
        Write-Success "Android SDK 35 found"
        $PASS++
    } else {
        Write-Warning "Android SDK 35 not installed"
        $WARN++
        Write-Action "Install using SDK Manager in Android Studio, or run:"
        Write-Action "  sdkmanager 'platforms;android-35'"
    }

    if (Test-Path "$androidHome\build-tools\35.0.0") {
        Write-Success "Android Build-Tools 35.0.0 found"
        $PASS++
    } else {
        Write-Warning "Android Build-Tools 35.0.0 not found"
        $WARN++
        Write-Action "Install: sdkmanager 'build-tools;35.0.0'"
    }

    if (Test-Path "$androidHome\licenses\android-sdk-license") {
        Write-Success "Android SDK licenses accepted"
        $PASS++
    } else {
        Write-Warning "Android SDK licenses not accepted"
        $WARN++
        Write-Action "Accept licenses: sdkmanager --licenses"
    }
} else {
    Write-Error "Android SDK not found"
    $FAIL++
    Write-Action "Install Android Studio from https://developer.android.com/studio"
    Write-Action "Set ANDROID_HOME to the SDK path"
    Write-Action "  Typically: C:\Users\$env:USERNAME\AppData\Local\Android\Sdk"
}

Write-Step "Gradle Wrapper"
$gradlew = "$SCRIPT_DIR\..\gradlew.bat"
$wrapperJar = "$SCRIPT_DIR\..\gradle\wrapper\gradle-wrapper.jar"

if (Test-Path $gradlew) {
    Write-Success "Gradle wrapper found (gradlew.bat)"
    $PASS++
} else {
    Write-Error "gradlew.bat not found"
    $FAIL++
    Write-Action "Ensure the repository was cloned fully"
}

if (Test-Path $wrapperJar) {
    Write-Success "Gradle wrapper JAR found"
    $PASS++
} else {
    Write-Warning "gradle-wrapper.jar missing"
    $WARN++
    Write-Action "Regenerate: gradle wrapper --gradle-version 8.11.1"
}

Write-Step "Network Connectivity"
Write-Info "Testing connection to key services..."

$tests = @(
    @{ Name = "services.gradle.org"; Url = "https://services.gradle.org" },
    @{ Name = "github.com"; Url = "https://github.com" },
    @{ Name = "dl.google.com"; Url = "https://dl.google.com" },
    @{ Name = "Maven Central"; Url = "https://repo1.maven.org/maven2/" },
    @{ Name = "Gradle Plugin Portal"; Url = "https://plugins.gradle.org" }
)

$allNetOk = $true
foreach ($t in $tests) {
    if (Invoke-NetworkTest $t.Name $t.Url 5) {
        Write-Success "  Can reach $($t.Name)"
    } else {
        Write-Warning "  Cannot reach $($t.Name)"
        $allNetOk = $false
    }
}

if ($allNetOk) {
    $PASS++
} else {
    $WARN++
    Write-Action "Network issues detected. See docs/NETWORK.md for troubleshooting."
    Write-Action "Quick fixes:"
    Write-Action '  - Set proxy: ="http://proxy:port"'
    Write-Action "  - Add to gradle.properties: systemProp.http.proxyHost=... systemProp.http.proxyPort=..."
    Write-Action '  - Use offline mode: .\gradlew.bat --offline <task>'
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host " Results: $PASS passed, $WARN warnings, $FAIL failed" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if ($FAIL -gt 0) {
    Write-Host ""
    Write-Host "  Some checks failed. Fix the issues above and re-run:" -ForegroundColor Yellow
    Write-Host "    .\scripts\setup.ps1" -ForegroundColor Magenta
    exit 1
} elseif ($WARN -gt 0) {
    Write-Host ""
    Write-Host "  All critical checks passed with warnings." -ForegroundColor Yellow
    Write-Host "  You can proceed, but some features may not work." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "  Next steps:" -ForegroundColor Cyan
    Write-Host "    .\scripts\build.ps1    - Build the app"
    Write-Host "    .\scripts\test.ps1     - Run unit tests"
    Write-Host "    .\scripts\lint.ps1     - Run linting"
    exit 0
} else {
    Write-Host ""
    Write-Host "  Everything looks good!" -ForegroundColor Green
    Write-Host ""
    Write-Host "  Quick start:" -ForegroundColor Cyan
    Write-Host "    .\scripts\build.ps1    - Build the app"
    Write-Host "    .\scripts\test.ps1     - Run unit tests"
    Write-Host "    .\scripts\lint.ps1     - Run linting"
    exit 0
}