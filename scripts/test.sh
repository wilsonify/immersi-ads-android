#!/usr/bin/env bash
# ImmersiAds — Test Script
# Usage: bash scripts/test.sh [--offline] [--no-daemon] [gradle-args...]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

cd "$PROJECT_ROOT"

echo "========================================"
echo " ImmersiAds — Unit Tests"
echo "========================================"

GRADLE_ARGS=("testDebugUnitTest")
for arg in "$@"; do
    GRADLE_ARGS+=("$arg")
done

if [ -x ./gradlew ]; then
    echo "  Running: ./gradlew ${GRADLE_ARGS[*]}"
    ./gradlew "${GRADLE_ARGS[@]}"
    EXIT_CODE=$?
    if [ $EXIT_CODE -eq 0 ]; then
        echo ""
        success "All tests passed"
        echo "  Report: app/build/reports/tests/testDebugUnitTest/index.html"
    else
        echo ""
        error_msg "Tests failed (exit code $EXIT_CODE)"
    fi
    exit $EXIT_CODE
else
    error_msg "gradlew not found — run scripts/setup.sh first"
    exit 1
fi
