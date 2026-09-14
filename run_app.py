#!/usr/bin/env python3
"""
KIDE Enterprise Application Launcher (Compatibility Wrapper)
============================================================
Delegates to run.py with full argument pass-through.
"""

import os
import sys
import subprocess

if __name__ == "__main__":
    script_dir = os.path.dirname(os.path.abspath(__file__))
    run_py = os.path.join(script_dir, "run.py")
    cmd = [sys.executable, run_py] + sys.argv[1:]
    sys.exit(subprocess.call(cmd))
