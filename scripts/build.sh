#!/usr/bin/env bash
# ImmersiAds — Build Script
# Usage: bash scripts/build.sh [--offline] [--no-daemon] [gradle-args...]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

cd "$PROJECT_ROOT"

echo "========================================"
echo " ImmersiAds — Build"
echo "========================================"

# Forward extra args to gradle
GRADLE_ARGS=("assembleDebug")
for arg in "$@"; do
    GRADLE_ARGS+=("$arg")
done

if [ -x ./gradlew ]; then
    echo "  Running: ./gradlew ${GRADLE_ARGS[*]}"
    ./gradlew "${GRADLE_ARGS[@]}"
    EXIT_CODE=$?
    if [ $EXIT_CODE -eq 0 ]; then
        echo ""
        success "Build successful"
        echo "  APK: app/build/outputs/apk/debug/app-debug.apk"
    else
        echo ""
        error_msg "Build failed (exit code $EXIT_CODE)"
    fi
    exit $EXIT_CODE
else
    error_msg "gradlew not found — run scripts/setup.sh first"
    exit 1
fi
