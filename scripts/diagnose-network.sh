#!/usr/bin/env bash
# ImmersiAds — Network Diagnostics
# Tests connectivity to all services required to build the project.
# Usage: bash scripts/diagnose-network.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

echo "============================================"
echo " ImmersiAds — Network Diagnostics"
echo "============================================"
echo ""

# Detect proxy
echo "--- Proxy Configuration ---"
if [ -n "${HTTP_PROXY:-}" ]; then
    info "HTTP_PROXY=$HTTP_PROXY"
fi
if [ -n "${HTTPS_PROXY:-}" ] || [ -n "${https_proxy:-}" ]; then
    info "HTTPS_PROXY=${HTTPS_PROXY:-${https_proxy:-}}"
fi
if [ -n "${NO_PROXY:-}" ]; then
    info "NO_PROXY=$NO_PROXY"
fi
if [ -z "${HTTP_PROXY:-}" ] && [ -z "${HTTPS_PROXY:-}" ]; then
    info "No proxy configured"
fi
echo ""

# DNS resolution
echo "--- DNS Resolution ---"
HOSTS=(
    "services.gradle.org"
    "github.com"
    "dl.google.com"
    "repo1.maven.org"
    "plugins.gradle.org"
    "release-assets.githubusercontent.com"
)
for host in "${HOSTS[@]}"; do
    if command_exists nslookup; then
        result=$(nslookup "$host" 2>&1 | grep -i "name\|address" | head -3)
        if [ -n "$result" ]; then
            success "$host resolves"
            info "  $(echo "$result" | head -1)"
        else
            warning "$host — DNS resolution failed"
        fi
    elif command_exists dig; then
        if dig +short "$host" >/dev/null 2>&1; then
            success "$host resolves"
        else
            warning "$host — DNS resolution failed"
        fi
    elif command_exists ping; then
        if ping -c 1 -W 3 "$host" >/dev/null 2>&1; then
            success "$host reachable via ping"
        else
            warning "$host — ping failed (may be blocked, not necessarily a problem)"
        fi
    else
        info "  Skipping DNS check for $host (no nslookup/dig/ping available)"
    fi
done
echo ""

# HTTP connectivity with details
echo "--- HTTP Connectivity ---"
TESTS=(
    "Gradle distribution:https://services.gradle.org/distributions/gradle-8.11.1-bin.zip"
    "GitHub:https://github.com"
    "Google Maven:https://dl.google.com/dl/android/maven2/"
    "Maven Central:https://repo1.maven.org/maven2/"
    "Gradle Plugin Portal:https://plugins.gradle.org"
    "GitHub assets:https://release-assets.githubusercontent.com"
)

for entry in "${TESTS[@]}"; do
    NAME="${entry%%:*}"
    URL="${entry##*:}"
    echo "  Testing: $NAME"
    if command_exists curl; then
        HTTP_CODE=$(curl -sI --connect-timeout 10 --max-time 15 -o /dev/null -w "%{http_code}" "$URL" 2>&1 || true)
        if [ -n "$HTTP_CODE" ] && [ "$HTTP_CODE" != "000" ]; then
            success "  HTTP $HTTP_CODE"
        else
            warning "  Connection failed"
            action "  Check proxy/firewall settings"
        fi
    elif command_exists wget; then
        if wget --spider --timeout=10 "$URL" >/dev/null 2>&1; then
            success "  Reachable"
        else
            warning "  Connection failed"
        fi
    else
        info "  Neither curl nor wget available — cannot test HTTP"
    fi
done
echo ""

# Gradle-specific checks
echo "--- Gradle Configuration ---"
WRAPPER_PROPS="$PROJECT_ROOT/gradle/wrapper/gradle-wrapper.properties"
if [ -f "$WRAPPER_PROPS" ]; then
    DIST_URL=$(grep "^distributionUrl" "$WRAPPER_PROPS" | cut -d= -f2- | sed 's/\\:/:/g')
    info "Distribution URL: $DIST_URL"
    TIMEOUT=$(grep "^networkTimeout" "$WRAPPER_PROPS" | cut -d= -f2-)
    info "Network timeout: ${TIMEOUT}ms"
    RETRIES=$(grep "^retries=" "$WRAPPER_PROPS" | cut -d= -f2-)
    info "Retries: $RETRIES"
    if [ "$RETRIES" = "0" ]; then
        warning "Retries are disabled — consider increasing for unreliable networks"
    fi
    if [ "$TIMEOUT" -lt 30000 ]; then
        warning "Timeout is very low (${TIMEOUT}ms) — may cause failures on slow connections"
    fi
fi
echo ""

# Offline mode check
echo "--- Gradle Cache Status ---"
GRADLE_CACHE="$HOME/.gradle"
if [ -d "$GRADLE_CACHE" ]; then
    CACHE_SIZE=$(du -sh "$GRADLE_CACHE" 2>/dev/null | cut -f1)
    info "Gradle cache size: ${CACHE_SIZE:-unknown}"
    if [ -d "$GRADLE_CACHE/wrapper/dists" ]; then
        DIST_COUNT=$(find "$GRADLE_CACHE/wrapper/dists" -maxdepth 1 -type d 2>/dev/null | wc -l)
        info "Cached distributions: $((DIST_COUNT - 1))"
    fi
else
    info "No Gradle cache found"
fi

# Android SDK connectivity
echo ""
echo "--- Android SDK ---"
    ANDROID_HOME_PATH=$(find_android_home) || true
if [ -n "$ANDROID_HOME_PATH" ]; then
    info "SDK location: $ANDROID_HOME_PATH"
    if [ -f "$ANDROID_HOME_PATH/licenses/android-sdk-license" ]; then
        success "SDK licenses accepted"
    else
        warning "SDK licenses not accepted"
    fi
else
    warning "Android SDK not found — set ANDROID_HOME"
fi

echo ""
echo "============================================"
echo " Diagnostics complete"
echo "============================================"
echo ""
echo "  If you see failures, check:"
echo "    1. Are you behind a corporate proxy? Set HTTP_PROXY/HTTPS_PROXY"
echo "    2. Can you reach GitHub? Try: curl -I https://github.com"
echo "    3. Gradle timeout too short? Edit gradle-wrapper.properties"
echo "    4. Need offline mode? Run with --offline flag"
echo "    5. See docs/NETWORK.md for full troubleshooting guide"
