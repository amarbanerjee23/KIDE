#!/usr/bin/env python3
"""
KIDE Enterprise Application Launcher
====================================
Cross-platform launcher for Windows, macOS, and Linux.
Manages dependencies, environment configuration, backend (FastAPI),
and frontend (Vite/React) development servers with unified lifecycle
and graceful shutdown.
"""

import os
import sys
import subprocess
import shutil
import signal
import time
import argparse
import webbrowser
import urllib.request
import urllib.error

# ANSI terminal colors
class Colors:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BLUE = '\033[94m'
    MAGENTA = '\033[95m'
    BOLD = '\033[1m'
    DIM = '\033[2m'
    RESET = '\033[0m'

    @classmethod
    def strip(cls):
        cls.CYAN = cls.GREEN = cls.YELLOW = cls.RED = cls.BLUE = cls.MAGENTA = cls.BOLD = cls.DIM = cls.RESET = ''

if os.name == 'nt' and not os.environ.get('WT_SESSION') and not os.environ.get('ANSICON'):
    try:
        import ctypes
        kernel32 = ctypes.windll.kernel32
        kernel32.SetConsoleMode(kernel32.GetStdHandle(-11), 7)
    except Exception:
        pass

def log(msg, color=Colors.CYAN, prefix="[KIDE]"):
    print(f"{color}{Colors.BOLD}{prefix}{Colors.RESET} {msg}")

def log_success(msg):
    log(msg, color=Colors.GREEN, prefix="[OK]")

def log_warn(msg):
    log(msg, color=Colors.YELLOW, prefix="[WARN]")

def log_error(msg):
    log(msg, color=Colors.RED, prefix="[ERROR]")

def check_port_in_use(port: int, host: str = "127.0.0.1") -> bool:
    import socket
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.5)
        return s.connect_ex((host, port)) == 0

def wait_for_service(url: str, timeout: float = 15.0, name: str = "Service") -> bool:
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            req = urllib.request.Request(url, headers={'User-Agent': 'KIDE-Launcher'})
            with urllib.request.urlopen(req, timeout=1.0) as resp:
                if resp.status < 500:
                    return True
        except (urllib.error.URLError, ConnectionResetError, ConnectionRefusedError, OSError):
            pass
        time.sleep(0.4)
    return False

def print_banner(frontend_url: str, backend_url: str, docs_url: str, mode: str = "web"):
    banner = f"""
{Colors.CYAN}{Colors.BOLD}========================================================================
                      KIDE Enterprise IDE Platform
========================================================================{Colors.RESET}
  {Colors.BOLD}Mode:{Colors.RESET}            {mode.upper()}
  {Colors.BOLD}Frontend UI:{Colors.RESET}     {Colors.GREEN}{frontend_url}{Colors.RESET}
  {Colors.BOLD}Backend API:{Colors.RESET}     {Colors.GREEN}{backend_url}{Colors.RESET}
  {Colors.BOLD}API Docs:{Colors.RESET}        {Colors.BLUE}{docs_url}{Colors.RESET}
{Colors.CYAN}------------------------------------------------------------------------{Colors.RESET}
  {Colors.YELLOW}* Native DSLs (DML, Capability, Operation, Activity, MNC-ML) active{Colors.RESET}
  {Colors.DIM}* Press Ctrl+C at any time to gracefully terminate all services.{Colors.RESET}
{Colors.CYAN}========================================================================{Colors.RESET}
"""
    print(banner)

def main():
    parser = argparse.ArgumentParser(
        description="Run KIDE Enterprise Web/Desktop IDE across Windows, Linux, and macOS.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
    )
    parser.add_argument("--mode", choices=["web", "desktop"], default="web", help="Execution mode (web or desktop)")
    parser.add_argument("--port", type=int, default=8005, help="Backend API port")
    parser.add_argument("--frontend-port", type=int, default=5173, help="Frontend dev server port")
    parser.add_argument("--host", default="127.0.0.1", help="Host interface to bind backend")
    parser.add_argument("--no-browser", action="store_true", help="Do not open browser automatically")
    parser.add_argument("--no-reload", action="store_true", help="Disable Uvicorn live code reloading")
    parser.add_argument("--install-only", action="store_true", help="Install dependencies without starting servers")
    parser.add_argument("--backend-only", action="store_true", help="Start only the backend API server")
    parser.add_argument("--frontend-only", action="store_true", help="Start only the frontend dev server")
    parser.add_argument("--clean", action="store_true", help="Clean virtual environment and reinstall dependencies")

    args = parser.parse_args()

    repo_root = os.path.dirname(os.path.abspath(__file__))
    kide_dir = os.path.join(repo_root, "kide-enterprise")
    backend_dir = os.path.join(kide_dir, "backend")
    frontend_dir = os.path.join(kide_dir, "frontend")
    desktop_dir = os.path.join(kide_dir, "desktop")

    if not os.path.isdir(kide_dir):
        log_error(f"Cannot find kide-enterprise root directory at: {kide_dir}")
        sys.exit(1)

    log(f"Initializing KIDE Enterprise on {sys.platform.upper()} (Python {sys.version.split()[0]})...")

    # 1. Prerequisite verification
    if sys.version_info < (3, 9):
        log_error("Python 3.9 or higher is required to run KIDE Enterprise.")
        sys.exit(1)

    node_path = shutil.which("node")
    npm_path = shutil.which("npm") or shutil.which("npm.cmd")
    if not node_path or not npm_path:
        log_error("Node.js and npm are required. Please install Node.js (https://nodejs.org) and add it to PATH.")
        sys.exit(1)

    # 2. Setup Backend Virtual Environment
    venv_dir = os.path.join(backend_dir, ".venv")
    is_win = sys.platform == "win32"
    python_exe = os.path.join(venv_dir, "Scripts", "python.exe") if is_win else os.path.join(venv_dir, "bin", "python")
    pip_exe = os.path.join(venv_dir, "Scripts", "pip.exe") if is_win else os.path.join(venv_dir, "bin", "pip")

    if args.clean and os.path.exists(venv_dir):
        log_warn("Cleaning backend virtual environment...")
        shutil.rmtree(venv_dir, ignore_errors=True)

    if not os.path.exists(python_exe):
        log("Creating backend Python virtual environment (.venv)...")
        res = subprocess.run([sys.executable, "-m", "venv", ".venv"], cwd=backend_dir)
        if res.returncode != 0:
            log_error("Failed to create Python virtual environment.")
            sys.exit(1)

    # Check if backend dependencies are installed
    check_code = "import fastapi, uvicorn, sqlalchemy, aiosqlite; print('READY')"
    has_deps = False
    try:
        check_proc = subprocess.run([python_exe, "-c", check_code], capture_output=True, text=True, cwd=backend_dir)
        if "READY" in check_proc.stdout:
            has_deps = True
    except Exception:
        has_deps = False

    if not has_deps:
        log("Installing backend dependencies (pip install -e .)...")
        install_res = subprocess.run([pip_exe, "install", "-e", "."], cwd=backend_dir)
        if install_res.returncode != 0:
            log_error("Backend dependency installation failed.")
            sys.exit(1)
        log_success("Backend dependencies installed successfully.")
    else:
        log_success("Backend dependencies are up to date.")

    # Check .env file
    env_file = os.path.join(backend_dir, ".env")
    env_example = os.path.join(kide_dir, ".env.example")
    if not os.path.exists(env_file):
        if os.path.exists(env_example):
            log("Copying .env configuration from .env.example...")
            shutil.copyfile(env_example, env_file)
        else:
            log("Generating default backend .env configuration...")
            with open(env_file, "w", encoding="utf-8") as f:
                f.write(
                    "DATABASE_URL=sqlite+aiosqlite:///./kide_data.db\n"
                    "SECRET_KEY=kide-enterprise-dev-secret-key-change-in-prod\n"
                    "DEBUG=True\n"
                    "CORS_ORIGINS=[\"*\"]\n"
                )
        log_success("Backend environment configuration prepared.")

    # 3. Setup Frontend Dependencies
    frontend_node_modules = os.path.join(frontend_dir, "node_modules")
    if args.clean and os.path.exists(frontend_node_modules):
        log_warn("Cleaning frontend node_modules...")
        shutil.rmtree(frontend_node_modules, ignore_errors=True)

    if not os.path.exists(frontend_node_modules):
        log("Installing frontend dependencies (npm install)...")
        npm_cmd = "npm.cmd" if is_win else "npm"
        npm_res = subprocess.run([npm_cmd, "install"], cwd=frontend_dir, shell=is_win)
        if npm_res.returncode != 0:
            log_error("Frontend dependency installation failed.")
            sys.exit(1)
        log_success("Frontend dependencies installed successfully.")
    else:
        log_success("Frontend dependencies are ready.")

    # 4. Setup Desktop Dependencies if in desktop mode
    if args.mode == "desktop":
        desktop_modules = os.path.join(desktop_dir, "node_modules")
        if not os.path.exists(desktop_modules):
            log("Installing desktop Electron dependencies...")
            npm_cmd = "npm.cmd" if is_win else "npm"
            subprocess.run([npm_cmd, "install"], cwd=desktop_dir, shell=is_win, check=True)
            log_success("Desktop dependencies installed.")

    if args.install_only:
        log_success("All KIDE Enterprise dependencies installed. Exiting (--install-only).")
        sys.exit(0)

    # 5. Launch Servers
    processes = []
    
    def kill_tree(p):
        if p and p.poll() is None:
            try:
                if is_win:
                    subprocess.run(["taskkill", "/F", "/T", "/PID", str(p.pid)], capture_output=True)
                else:
                    os.killpg(os.getpgid(p.pid), signal.SIGTERM)
            except Exception:
                try:
                    p.kill()
                except Exception:
                    pass

    def cleanup_all():
        log_warn("Shutting down KIDE Enterprise services...")
        for proc in processes:
            kill_tree(proc)
        log_success("All processes terminated.")

    def signal_handler(sig, frame):
        cleanup_all()
        sys.exit(0)

    signal.signal(signal.SIGINT, signal_handler)
    if not is_win:
        signal.signal(signal.SIGTERM, signal_handler)

    try:
        # Check port conflicts
        if not args.frontend_only and check_port_in_use(args.port, args.host):
            log_warn(f"Port {args.port} is already in use. Attempting to run backend anyway...")

        # Start Backend Server
        if not args.frontend_only:
            log(f"Starting Backend Uvicorn Server on {args.host}:{args.port}...")
            uvicorn_cmd = [
                python_exe, "-m", "uvicorn", "app.main:app",
                "--host", args.host,
                "--port", str(args.port)
            ]
            if not args.no_reload:
                uvicorn_cmd.append("--reload")

            popen_kwargs = {"cwd": backend_dir}
            if is_win:
                popen_kwargs["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
            else:
                popen_kwargs["preexec_fn"] = os.setsid

            backend_proc = subprocess.Popen(uvicorn_cmd, **popen_kwargs)
            processes.append(backend_proc)

        # Start Frontend Server
        if not args.backend_only and args.mode == "web":
            log(f"Starting Frontend Vite Server on port {args.frontend_port}...")
            npm_cmd = "npm.cmd" if is_win else "npm"
            frontend_cmd = [npm_cmd, "run", "dev", "--", "--port", str(args.frontend_port)]

            popen_kwargs = {"cwd": frontend_dir, "shell": is_win}
            if is_win:
                popen_kwargs["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
            else:
                popen_kwargs["preexec_fn"] = os.setsid

            frontend_proc = subprocess.Popen(frontend_cmd, **popen_kwargs)
            processes.append(frontend_proc)

        elif args.mode == "desktop":
            log("Starting Desktop Electron Application...")
            npm_cmd = "npm.cmd" if is_win else "npm"
            desktop_cmd = [npm_cmd, "run", "start:dev"]
            popen_kwargs = {"cwd": desktop_dir, "shell": is_win}
            if is_win:
                popen_kwargs["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
            else:
                popen_kwargs["preexec_fn"] = os.setsid

            desktop_proc = subprocess.Popen(desktop_cmd, **popen_kwargs)
            processes.append(desktop_proc)

        # Wait for services to respond
        backend_url = f"http://{args.host}:{args.port}"
        docs_url = f"{backend_url}/docs"
        frontend_url = f"http://localhost:{args.frontend_port}"

        if not args.frontend_only:
            log("Waiting for backend API health check...")
            if wait_for_service(f"{backend_url}/", timeout=12.0, name="Backend API"):
                log_success(f"Backend API is alive at {backend_url}")
            else:
                log_warn(f"Backend did not respond immediately, continuing startup...")

        if not args.backend_only and args.mode == "web":
            log("Waiting for frontend dev server...")
            if wait_for_service(frontend_url, timeout=12.0, name="Frontend"):
                log_success(f"Frontend is responsive at {frontend_url}")
            else:
                log_warn(f"Frontend taking longer to warm up, continuing...")

        print_banner(frontend_url, backend_url, docs_url, mode=args.mode)

        # Open Browser
        if not args.no_browser and not args.backend_only and args.mode == "web":
            try:
                time.sleep(1.0)
                webbrowser.open(frontend_url)
                log_success(f"Opened web browser at {frontend_url}")
            except Exception as e:
                log_warn(f"Could not open browser automatically: {e}")

        # Keep alive
        while True:
            for p in processes:
                code = p.poll()
                if code is not None:
                    log_error(f"A service terminated unexpectedly with exit code {code}.")
                    cleanup_all()
                    sys.exit(code or 1)
            time.sleep(1.0)

    except KeyboardInterrupt:
        cleanup_all()
        sys.exit(0)

if __name__ == "__main__":
    main()

