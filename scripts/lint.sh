#!/usr/bin/env bash
# ImmersiAds — Lint Script
# Usage: bash scripts/lint.sh [--offline] [--no-daemon] [gradle-args...]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

cd "$PROJECT_ROOT"

echo "========================================"
echo " ImmersiAds — Lint"
echo "========================================"

GRADLE_ARGS=("lintDebug")
for arg in "$@"; do
    GRADLE_ARGS+=("$arg")
done

if [ -x ./gradlew ]; then
    echo "  Running: ./gradlew ${GRADLE_ARGS[*]}"
    ./gradlew "${GRADLE_ARGS[@]}"
    EXIT_CODE=$?
    if [ $EXIT_CODE -eq 0 ]; then
        echo ""
        success "Lint passed"
        echo "  Report: app/build/reports/lint-results-debug.html"
    else
        echo ""
        warning "Lint found issues (exit code $EXIT_CODE)"
        echo "  Report: app/build/reports/lint-results-debug.html"
        echo "  Lint errors are informational — they do not block the build"
    fi
    exit $EXIT_CODE
else
    error_msg "gradlew not found — run scripts/setup.sh first"
    exit 1
fi
