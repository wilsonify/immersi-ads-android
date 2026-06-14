# Shared functions for ImmersiAds developer scripts
# Source this file: source "$(dirname "$0")/lib/common.sh"

_COMMON_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$_COMMON_DIR/../.." && pwd)"

step() {
    echo ""
    echo ">>> $1"
    info "$2"
}

info() {
    echo "  $1"
}

success() {
    echo "  [PASS] $1"
}

warning() {
    echo "  [WARN] $1"
}

error_msg() {
    echo "  [FAIL] $1"
}

action() {
    echo "  [ACTION] $1"
}

command_exists() {
    command -v "$1" >/dev/null 2>&1
}

get_java_major_version() {
    if command_exists java; then
        java -version 2>&1 | sed -E -n 's/.*"(1\.)?([0-9]+).*/\2/p'
    fi
}

find_android_home() {
    for dir in "${ANDROID_HOME:-}" "${ANDROID_SDK_ROOT:-}" "$HOME/Android/Sdk" "$HOME/Library/Android/Sdk"; do
        if [ -n "$dir" ] && [ -d "$dir" ]; then
            echo "$dir"
            return 0
        fi
    done
    return 1
}

network_test() {
    local name="$1"
    local url="$2"
    local timeout="${3:-10}"
    if command_exists curl; then
        curl -sI --connect-timeout "$timeout" --max-time "$timeout" "$url" >/dev/null 2>&1
        return $?
    elif command_exists wget; then
        wget --spider --timeout="$timeout" "$url" >/dev/null 2>&1
        return $?
    fi
    return 1
}
