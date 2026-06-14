#!/usr/bin/env bash
# ImmersiAds — Setup Script
# Validates prerequisites and provides remediation steps.
# Usage: bash scripts/setup.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

PASS=0
FAIL=0
WARN=0

echo "========================================"
echo " ImmersiAds — Development Setup Check"
echo "========================================"

# ---- Java ----
echo ""
echo "--- Java ---"
JAVA_VER=$(get_java_major_version)
if [ -n "$JAVA_VER" ]; then
    if [ "$JAVA_VER" -ge 17 ]; then
        success "Java $JAVA_VER found"
        PASS=$((PASS + 1))
    else
        warning "Java $JAVA_VER found, but 17+ is required"
        WARN=$((WARN + 1))
        action "Install JDK 17 or later: https://adoptium.net/"
    fi
else
    error_msg "Java not found"
    FAIL=$((FAIL + 1))
    action "Install JDK 17+ from https://adoptium.net/ or https://www.oracle.com/java/"
    action "After install, set JAVA_HOME and ensure java is on your PATH"
fi

# ---- Android SDK ----
echo ""
echo "--- Android SDK ---"
ANDROID_HOME_PATH=$(find_android_home) || true
if [ -n "$ANDROID_HOME_PATH" ]; then
    success "Android SDK found at: $ANDROID_HOME_PATH"
    PASS=$((PASS + 1))
    export ANDROID_HOME="$ANDROID_HOME_PATH"
    export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

    # Check SDK 35
    if [ -d "$ANDROID_HOME/platforms/android-35" ]; then
        success "Android SDK 35 found"
        PASS=$((PASS + 1))
    else
        warning "Android SDK 35 not installed"
        WARN=$((WARN + 1))
        action "Install SDK 35: Run 'sdkmanager \"platforms;android-35\"'"
        action "  Or in Android Studio: SDK Manager -> SDK Platforms -> Android 35"
    fi

    # Check build-tools
    if [ -d "$ANDROID_HOME/build-tools/35.0.0" ]; then
        success "Android Build-Tools 35.0.0 found"
        PASS=$((PASS + 1))
    else
        warning "Android Build-Tools 35.0.0 not found"
        WARN=$((WARN + 1))
        action "Install: sdkmanager \"build-tools;35.0.0\""
    fi

    # Check licenses
    if [ -f "$ANDROID_HOME/licenses/android-sdk-license" ]; then
        success "Android SDK licenses accepted"
        PASS=$((PASS + 1))
    else
        warning "Android SDK licenses not accepted"
        WARN=$((WARN + 1))
        action "Accept licenses: sdkmanager --licenses"
        action "  Or: yes | sdkmanager --licenses"
    fi
else
    error_msg "Android SDK not found"
    FAIL=$((FAIL + 1))
    action "Install Android Studio: https://developer.android.com/studio"
    action "After install, set ANDROID_HOME to the SDK location"
    action "  Typical paths:"
    action "    macOS: ~/Library/Android/Sdk"
    action "    Linux: ~/Android/Sdk"
    action "    Windows: C:\\Users\\<user>\\AppData\\Local\\Android\\Sdk"
    action "Add to ~/.bashrc or ~/.zshrc: export ANDROID_HOME=~/Android/Sdk"
fi

# ---- Gradle Wrapper ----
echo ""
echo "--- Gradle Wrapper ---"
if [ -f "$PROJECT_ROOT/gradlew" ]; then
    success "Gradle wrapper found"
    PASS=$((PASS + 1))
    chmod +x "$PROJECT_ROOT/gradlew"
else
    error_msg "gradlew not found — repository may be incomplete"
    FAIL=$((FAIL + 1))
    action "Ensure the repository was cloned fully: git clone <repo>"
    action "Check if gradlew is in .gitignore or excluded"
fi

if [ -f "$PROJECT_ROOT/gradle/wrapper/gradle-wrapper.jar" ]; then
    success "Gradle wrapper JAR found"
    PASS=$((PASS + 1))
else
    warning "gradle-wrapper.jar missing"
    WARN=$((WARN + 1))
    action "Generate wrapper: gradle wrapper --gradle-version 8.11.1"
    action "  Or download from: https://raw.githubusercontent.com/gradle/gradle/v8.11.1/gradle/wrapper/gradle-wrapper.jar"
fi

# ---- Network Connectivity ----
echo ""
echo "--- Network Connectivity ---"

echo "  Testing connection to key services..."

TESTS=(
    "services.gradle.org:https://services.gradle.org"
    "github.com:https://github.com"
    "dl.google.com:https://dl.google.com"
    "repo1.maven.org:https://repo1.maven.org/maven2/"
    "plugins.gradle.org:https://plugins.gradle.org"
)

ALL_NET_OK=true
for test_entry in "${TESTS[@]}"; do
    NAME="${test_entry%%:*}"
    URL="${test_entry##*:}"
    if network_test "$NAME" "$URL" 5; then
        success "  Can reach $NAME"
    else
        warning "  Cannot reach $NAME"
        ALL_NET_OK=false
    fi
done

if [ "$ALL_NET_OK" = true ]; then
    PASS=$((PASS + 1))
else
    WARN=$((WARN + 1))
    action "Network issues detected. See docs/NETWORK.md for troubleshooting."
    action "Quick fixes:"
    action "  - Check proxy settings: export HTTP_PROXY=http://proxy:port HTTPS_PROXY=http://proxy:port"
    action "  - Add Gradle proxy in gradle.properties: systemProp.http.proxyHost=... systemProp.http.proxyPort=..."
    action "  - Use offline mode: ./gradlew --offline <task>"
fi

# ---- Summary ----
echo ""
echo "========================================"
echo " Results: $PASS passed, $WARN warnings, $FAIL failed"
echo "========================================"

if [ $FAIL -gt 0 ]; then
    echo ""
    echo "  Some checks failed. Fix the issues above and re-run:"
    echo "    bash scripts/setup.sh"
    exit 1
elif [ $WARN -gt 0 ]; then
    echo ""
    echo "  All critical checks passed with warnings."
    echo "  You can proceed, but some features may not work."
    echo ""
    echo "  Next steps:"
    echo "    bash scripts/build.sh    — Build the app"
    echo "    bash scripts/test.sh     — Run unit tests"
    echo "    bash scripts/lint.sh     — Run linting"
    exit 0
else
    echo ""
    echo "  Everything looks good!"
    echo ""
    echo "  Quick start:"
    echo "    bash scripts/build.sh    — Build the app"
    echo "    bash scripts/test.sh     — Run unit tests"
    echo "    bash scripts/lint.sh     — Run linting"
    exit 0
fi
