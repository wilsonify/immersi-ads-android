#!/usr/bin/env bash
# ImmersiAds — Verify Script
# Runs lint, tests, and build sequentially.
# Usage: bash scripts/verify.sh [--offline]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

cd "$PROJECT_ROOT"

echo "========================================"
echo " ImmersiAds — Full Verification"
echo "========================================"

GRADLE_EXTRA=()
if [ "${1:-}" = "--offline" ]; then
    GRADLE_EXTRA+=("--offline")
fi

# 1. Lint
step "Step 1/3: Lint"
bash "$SCRIPT_DIR/lint.sh" "${GRADLE_EXTRA[@]}" || true
echo ""  # spacing

# 2. Unit tests
step "Step 2/3: Unit Tests"
bash "$SCRIPT_DIR/test.sh" "${GRADLE_EXTRA[@]}"
TEST_EXIT=$?
if [ $TEST_EXIT -ne 0 ]; then
    error_msg "Tests failed — aborting verification"
    exit $TEST_EXIT
fi
echo ""

# 3. Build
step "Step 3/3: Build"
bash "$SCRIPT_DIR/build.sh" "${GRADLE_EXTRA[@]}"
BUILD_EXIT=$?
if [ $BUILD_EXIT -ne 0 ]; then
    error_msg "Build failed — aborting verification"
    exit $BUILD_EXIT
fi

echo ""
echo "========================================"
success "All checks passed!"
echo "========================================"
