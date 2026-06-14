#!/usr/bin/env bash
# ImmersiAds — Run on Emulator/Device
# Usage: bash scripts/run.sh [gradle-args...]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

cd "$PROJECT_ROOT"

echo "========================================"
echo " ImmersiAds — Run"
echo "========================================"

GRADLE_ARGS=("installDebug")
for arg in "$@"; do
    GRADLE_ARGS+=("$arg")
done

if [ -x ./gradlew ]; then
    echo "  Running: ./gradlew ${GRADLE_ARGS[*]}"
    ./gradlew "${GRADLE_ARGS[@]}"
    EXIT_CODE=$?
    if [ $EXIT_CODE -ne 0 ]; then
        echo ""
        error_msg "Install failed (exit code $EXIT_CODE)"
        exit $EXIT_CODE
    fi
    echo ""
    success "APK installed"

    ANDROID_HOME_DIR=$(find_android_home) || true
    if command_exists adb; then
        ADB="adb"
    elif [ -n "$ANDROID_HOME_DIR" ]; then
        ADB="$ANDROID_HOME_DIR/platform-tools/adb"
    else
        warning "adb not found — app installed but cannot launch"
        exit 0
    fi

    echo "  Launching app..."
    "$ADB" shell am start -n "com.immersiads.app.debug/com.immersiads.app.MainActivity" 2>/dev/null && \
        success "App launched" || \
        warning "Could not launch app — ensure emulator/device is connected"
else
    error_msg "gradlew not found — run scripts/setup.sh first"
    exit 1
fi
