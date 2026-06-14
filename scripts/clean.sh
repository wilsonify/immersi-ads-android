#!/usr/bin/env bash
# ImmersiAds — Clean Script
# Removes all generated artifacts.
# Usage: bash scripts/clean.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPT_DIR/lib/common.sh"

cd "$PROJECT_ROOT"

echo "========================================"
echo " ImmersiAds — Clean"
echo "========================================"

DIRS_TO_CLEAN=(
    "app/build"
    ".gradle"
    "build"
)

for dir in "${DIRS_TO_CLEAN[@]}"; do
    if [ -d "$dir" ]; then
        echo "  Removing $dir/"
        rm -rf "$dir"
    fi
done

echo ""
success "Clean complete"
echo ""
echo "  To also clear Gradle cache (global):"
echo "    rm -rf ~/.gradle/caches/"
echo "  To clear only this project's cached Gradle distribution:"
echo "    rm -rf ~/.gradle/wrapper/dists/gradle-8.11.1*"
