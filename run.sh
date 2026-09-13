#!/usr/bin/env bash
# ========================================================================
# KIDE Enterprise Unix Launcher (macOS / Linux)
# ========================================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ANSI color codes
CYAN='\033[0;36m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${CYAN}========================================================================${NC}"
echo -e "${CYAN}             KIDE Enterprise Unix Launcher (macOS / Linux)              ${NC}"
echo -e "${CYAN}========================================================================${NC}"
echo ""

# 1. Find Python 3
PYTHON_BIN=""
if command -v python3 &> /dev/null; then
    PYTHON_BIN="python3"
elif command -v python &> /dev/null; then
    # Verify it is Python 3
    if python -c 'import sys; exit(0 if sys.version_info >= (3, 9) else 1)' &> /dev/null; then
        PYTHON_BIN="python"
    fi
fi

if [ -z "$PYTHON_BIN" ]; then
    echo -e "${RED}[ERROR] Python 3.9+ was not found.${NC}"
    echo -e "${YELLOW}Please install Python 3 using your package manager (brew install python3, apt install python3 python3-venv, etc.)${NC}"
    exit 1
fi

# 2. Check Node.js and npm
if ! command -v node &> /dev/null; then
    echo -e "${RED}[ERROR] Node.js was not found.${NC}"
    echo -e "${YELLOW}Please install Node.js 18+ (https://nodejs.org or nvm).${NC}"
    exit 1
fi

if ! command -v npm &> /dev/null; then
    echo -e "${RED}[ERROR] npm was not found.${NC}"
    exit 1
fi

# 3. Execute run.py
exec "$PYTHON_BIN" "$SCRIPT_DIR/run.py" "$@"

