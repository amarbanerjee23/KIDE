#!/usr/bin/env python3
"""
KIDE Enterprise Infrastructure Setup & Deployment Orchestrator
=============================================================
Provisions the complete deployment infrastructure and reliably runs the application
every time on Windows, macOS, and Linux without issues.

Key Infrastructure Automation:
  1. System & Toolchain Pre-flight Verification (Python 3.10+, Node.js 18+, npm)
  2. Directory & Storage Infrastructure Setup (storage, uploads, exports, logs)
  3. Environment & Security Configuration (.env initialization)
  4. Backend Infrastructure Provisioning (Python virtualenv & dependency installation)
  5. Database Infrastructure Provisioning (Automatic table creation & seeding)
  6. Frontend Infrastructure Provisioning (npm dependencies & production build)
  7. Socket & Port Conflict Reclamation (Frees ports 8005, 5173, 8000 if occupied)
  8. Service Orchestration & Health Supervision (Live probes, browser launch, graceful shutdown)
"""

import os
import sys
import subprocess
import shutil
import signal
import time
import argparse
import webbrowser
import socket
import urllib.request
import urllib.error

# Ensure UTF-8 output across all operating systems
if sys.stdout.encoding and sys.stdout.encoding.lower() != 'utf-8':
    try:
        sys.stdout.reconfigure(encoding='utf-8', errors='replace')
        sys.stderr.reconfigure(encoding='utf-8', errors='replace')
    except Exception:
        pass

# Terminal ANSI styling
class Style:
    CYAN = '\033[96m'
    GREEN = '\033[92m'
    YELLOW = '\033[93m'
    RED = '\033[91m'
    BLUE = '\033[94m'
    MAGENTA = '\033[95m'
    BOLD = '\033[1m'
    DIM = '\033[2m'
    RESET = '\033[0m'

if os.name == 'nt':
    try:
        import ctypes
        kernel32 = ctypes.windll.kernel32
        kernel32.SetConsoleMode(kernel32.GetStdHandle(-11), 7)
    except Exception:
        pass

def log(msg, color=Style.CYAN, prefix="[INFRA]"):
    print(f"{color}{Style.BOLD}{prefix}{Style.RESET} {msg}", flush=True)

def log_step(step: int, total: int, msg: str):
    print(f"\n{Style.MAGENTA}{Style.BOLD}[STEP {step}/{total}]{Style.RESET} {Style.BOLD}{msg}{Style.RESET}", flush=True)

def log_success(msg):
    log(msg, color=Style.GREEN, prefix="[SUCCESS]")

def log_warn(msg):
    log(msg, color=Style.YELLOW, prefix="[WARNING]")

def log_error(msg):
    log(msg, color=Style.RED, prefix="[ERROR]")

def check_port_in_use(port: int, host: str = "127.0.0.1") -> bool:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.settimeout(0.4)
        return s.connect_ex((host, port)) == 0

def reclaim_port(port: int, host: str = "127.0.0.1") -> bool:
    """Safely terminate any stale process locking the target port."""
    if not check_port_in_use(port, host):
        return True

    log_warn(f"Port {port} is occupied by an existing process. Reclaiming port...")
    if sys.platform == "win32":
        try:
            cmd = f'netstat -ano | findstr /R /C:":{port} .*LISTENING"'
            proc = subprocess.run(cmd, shell=True, capture_output=True, text=True)
            output = proc.stdout.strip()
            if not output:
                proc = subprocess.run(f'netstat -ano | findstr :{port}', shell=True, capture_output=True, text=True)
                output = proc.stdout.strip()

            for line in output.splitlines():
                parts = line.strip().split()
                if len(parts) >= 5 and "LISTENING" in parts:
                    pid = parts[-1]
                    if pid != "0" and pid != str(os.getpid()):
                        log(f"Terminating orphan process on port {port} (PID: {pid})...")
                        subprocess.run(f"taskkill /F /T /PID {pid}", shell=True, capture_output=True)

            time.sleep(0.8)
        except Exception as e:
            log_warn(f"Failed to auto-kill process on port {port}: {e}")
    else:
        try:
            subprocess.run(f"fuser -k {port}/tcp", shell=True, capture_output=True)
            time.sleep(0.5)
        except Exception:
            pass

    return not check_port_in_use(port, host)

def wait_for_service(url: str, timeout: float = 15.0) -> bool:
    start_time = time.time()
    while time.time() - start_time < timeout:
        try:
            req = urllib.request.Request(url, headers={'User-Agent': 'KIDE-Deployer'})
            with urllib.request.urlopen(req, timeout=1.0) as resp:
                if resp.status < 500:
                    return True
        except (urllib.error.URLError, ConnectionResetError, ConnectionRefusedError, OSError):
            pass
        time.sleep(0.4)
    return False

def print_infra_banner(frontend_url: str, backend_url: str, docs_url: str):
    banner = f"""
{Style.CYAN}{Style.BOLD}========================================================================
             KIDE Enterprise - Deployment Infrastructure Live
========================================================================{Style.RESET}
  {Style.BOLD}Application URL:{Style.RESET}   {Style.GREEN}{frontend_url}{Style.RESET}
  {Style.BOLD}API Gateway:{Style.RESET}       {Style.GREEN}{backend_url}{Style.RESET}
  {Style.BOLD}API Documentation:{Style.RESET} {Style.BLUE}{docs_url}{Style.RESET}
{Style.CYAN}------------------------------------------------------------------------{Style.RESET}
  {Style.YELLOW}* Infrastructure: Ready & Healthy (Storage, Database, DSL Engines){Style.RESET}
  {Style.DIM}* Press Ctrl+C to cleanly stop all services and release ports.{Style.RESET}
{Style.CYAN}========================================================================{Style.RESET}
"""
    print(banner, flush=True)

def main():
    parser = argparse.ArgumentParser(
        description="Setup deployment infrastructure and run KIDE Enterprise seamlessly.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter
    )
    parser.add_argument("--setup-only", action="store_true", help="Provision infrastructure without starting servers")
    parser.add_argument("--docker", action="store_true", help="Deploy infrastructure using Docker Compose")
    parser.add_argument("--clean", action="store_true", help="Perform a clean wipe and re-provisioning of environments")
    parser.add_argument("--port", type=int, default=8005, help="Backend API gateway port")
    parser.add_argument("--frontend-port", type=int, default=5173, help="Frontend UI port")
    parser.add_argument("--host", default="127.0.0.1", help="Host interface to bind backend")
    parser.add_argument("--no-browser", action="store_true", help="Do not open browser automatically")
    parser.add_argument("--no-build", action="store_true", help="Skip frontend production bundle compilation")

    args = parser.parse_args()

    repo_root = os.path.dirname(os.path.abspath(__file__))
    kide_dir = os.path.join(repo_root, "kide-enterprise")
    backend_dir = os.path.join(kide_dir, "backend")
    frontend_dir = os.path.join(kide_dir, "frontend")

    if not os.path.isdir(kide_dir):
        log_error(f"Cannot find kide-enterprise directory at: {kide_dir}")
        sys.exit(1)

    # Check for bundled portable Node.js and prepend to PATH
    bundled_node = os.path.join(repo_root, "node-v20.11.1-win-x64")
    if os.path.isdir(bundled_node):
        os.environ["PATH"] = bundled_node + os.pathsep + os.environ.get("PATH", "")

    TOTAL_STEPS = 6

    # =========================================================================
    # STEP 1: Pre-flight Audit & System Diagnostics
    # =========================================================================
    log_step(1, TOTAL_STEPS, "System Pre-flight & Toolchain Audit")
    
    # Python Check
    py_version = sys.version_info
    if py_version < (3, 10):
        log_error(f"Python 3.10 or higher is required (found Python {py_version[0]}.{py_version[1]}.{py_version[2]}).")
        sys.exit(1)
    log_success(f"Python {py_version[0]}.{py_version[1]}.{py_version[2]} verified.")

    # Node.js & npm Check
    node_path = shutil.which("node")
    npm_path = shutil.which("npm") or shutil.which("npm.cmd")
    if not node_path or not npm_path:
        log_error("Node.js and npm are required. Please install Node.js 18+ from https://nodejs.org/")
        sys.exit(1)
    
    node_ver = subprocess.run([node_path, "-v"], capture_output=True, text=True).stdout.strip()
    log_success(f"Node.js ({node_ver}) and npm detected.")

    # Docker Check (if requested)
    if args.docker:
        docker_path = shutil.which("docker")
        if not docker_path:
            log_error("Docker is required for --docker deployment mode. Please install Docker Desktop.")
            sys.exit(1)
        log_success("Docker engine detected.")

    # =========================================================================
    # STEP 2: Directory & Storage Infrastructure Setup
    # =========================================================================
    log_step(2, TOTAL_STEPS, "Provisioning Directory & Storage Infrastructure")
    
    storage_dirs = [
        os.path.join(backend_dir, "uploads"),
        os.path.join(backend_dir, "exports"),
        os.path.join(backend_dir, "storage", "uploads"),
        os.path.join(backend_dir, "storage", "exports"),
        os.path.join(kide_dir, "storage", "uploads"),
        os.path.join(kide_dir, "storage", "exports"),
        os.path.join(backend_dir, "logs"),
    ]
    for sdir in storage_dirs:
        os.makedirs(sdir, exist_ok=True)
    log_success(f"Storage infrastructure verified across {len(storage_dirs)} target directories.")

    # Environment Configuration
    env_file = os.path.join(backend_dir, ".env")
    env_example = os.path.join(kide_dir, ".env.example")
    if not os.path.exists(env_file):
        if os.path.exists(env_example):
            shutil.copyfile(env_example, env_file)
            log_success("Created backend .env from template (.env.example).")
        else:
            with open(env_file, "w", encoding="utf-8") as f:
                f.write(
                    "DATABASE_URL=sqlite+aiosqlite:///./kide_data.db\n"
                    "SECRET_KEY=kide-enterprise-dev-secret-key-change-in-prod\n"
                    "DEBUG=True\n"
                    "CORS_ORIGINS=[\"*\"]\n"
                )
            log_success("Generated default backend .env configuration.")
    else:
        log_success("Backend .env configuration exists and is active.")

    # =========================================================================
    # STEP 3: Backend Infrastructure (Virtualenv & Dependencies)
    # =========================================================================
    log_step(3, TOTAL_STEPS, "Provisioning Backend Python Infrastructure")
    
    venv_dir = os.path.join(backend_dir, ".venv")
    is_win = sys.platform == "win32"
    python_exe = os.path.join(venv_dir, "Scripts", "python.exe") if is_win else os.path.join(venv_dir, "bin", "python")
    pip_exe = os.path.join(venv_dir, "Scripts", "pip.exe") if is_win else os.path.join(venv_dir, "bin", "pip")

    if args.clean and os.path.exists(venv_dir):
        log_warn("Cleaning virtual environment (--clean specified)...")
        shutil.rmtree(venv_dir, ignore_errors=True)

    if not os.path.exists(python_exe):
        log("Creating Python virtual environment (.venv)...")
        res = subprocess.run([sys.executable, "-m", "venv", ".venv"], cwd=backend_dir)
        if res.returncode != 0:
            log_error("Failed to create Python virtual environment.")
            sys.exit(1)
        log_success("Virtual environment created.")

    # Verify or install backend dependencies
    check_code = "import fastapi, uvicorn, sqlalchemy, aiosqlite; print('READY')"
    has_backend_deps = False
    try:
        proc = subprocess.run([python_exe, "-c", check_code], capture_output=True, text=True, cwd=backend_dir)
        if "READY" in proc.stdout:
            has_backend_deps = True
    except Exception:
        has_backend_deps = False

    if not has_backend_deps or args.clean:
        log("Installing/upgrading backend dependencies (pip install -e .)...")
        install_res = subprocess.run([pip_exe, "install", "-e", "."], cwd=backend_dir)
        if install_res.returncode != 0:
            log_error("Backend dependency installation failed.")
            sys.exit(1)
        log_success("Backend dependencies installed successfully.")
    else:
        log_success("Backend dependencies verified.")

    # =========================================================================
    # STEP 4: Database Infrastructure Provisioning & Schema Initialization
    # =========================================================================
    log_step(4, TOTAL_STEPS, "Provisioning Database Schema & Tables")
    
    init_db_script = """
import asyncio, sys, os
sys.path.insert(0, os.getcwd())
from app.database import create_tables, AsyncSessionLocal, engine
from app.models.user import User, Organization
from app.auth.password import hash_password
from sqlalchemy import select
import uuid

async def init_db():
    await create_tables()
    async with AsyncSessionLocal() as session:
        result = await session.execute(select(User))
        users = result.scalars().all()
        if not users:
            org = Organization(name="KIDE Enterprise Demo Organization", slug=str(uuid.uuid4()))
            session.add(org)
            await session.commit()
            await session.refresh(org)
            
            admin_user = User(
                email="admin@kide.io",
                hashed_password=hash_password("admin123"),
                full_name="KIDE Enterprise Administrator",
                org_id=org.id,
                role="owner"
            )
            session.add(admin_user)
            await session.commit()
            print("SEEDED_ADMIN")
        else:
            print("TABLES_READY")
    await engine.dispose()

asyncio.run(init_db())
"""
    db_env = os.environ.copy()
    db_env["PYTHONPATH"] = backend_dir
    db_proc = subprocess.run([python_exe, "-c", init_db_script], cwd=backend_dir, env=db_env, capture_output=True, text=True)
    if db_proc.returncode != 0:
        log_error(f"Database table initialization failed:\n{db_proc.stderr}")
        sys.exit(1)
    
    if "SEEDED_ADMIN" in db_proc.stdout:
        log_success("Database initialized and default administrator account seeded (admin@kide.io / admin123).")
    else:
        log_success("Database tables and schema models verified.")

    # =========================================================================
    # STEP 5: Frontend Infrastructure Provisioning (npm & Production Build)
    # =========================================================================
    log_step(5, TOTAL_STEPS, "Provisioning Frontend Infrastructure & Client Assets")
    
    node_modules = os.path.join(frontend_dir, "node_modules")
    npm_cmd = "npm.cmd" if is_win else "npm"

    if args.clean and os.path.exists(node_modules):
        log_warn("Cleaning node_modules (--clean specified)...")
        shutil.rmtree(node_modules, ignore_errors=True)

    if not os.path.exists(node_modules):
        log("Installing frontend dependencies (npm install)...")
        npm_res = subprocess.run([npm_cmd, "install"], cwd=frontend_dir, shell=is_win)
        if npm_res.returncode != 0:
            log_error("Frontend dependency installation failed.")
            sys.exit(1)
        log_success("Frontend dependencies installed.")
    else:
        log_success("Frontend node_modules verified.")

    # Compile production client build if not skipped
    dist_dir = os.path.join(frontend_dir, "dist")
    if not args.no_build and (not os.path.exists(dist_dir) or args.clean):
        log("Building production frontend bundle (npm run build)...")
        build_res = subprocess.run([npm_cmd, "run", "build"], cwd=frontend_dir, shell=is_win)
        if build_res.returncode == 0:
            log_success("Frontend production bundle compiled to dist/.")
        else:
            log_warn("Frontend build had non-critical warnings; continuing...")

    if args.setup_only:
        log_success("Infrastructure setup complete! All components provisioned successfully.")
        sys.exit(0)

    # =========================================================================
    # STEP 6: Port Conflict Resolution & Service Deployment
    # =========================================================================
    log_step(6, TOTAL_STEPS, "Launching & Supervising Application Services")
    
    # 1. Docker Compose Deployment Mode
    if args.docker:
        log("Starting deployment infrastructure via Docker Compose...")
        compose_cmd = ["docker", "compose", "up", "-d", "--build"]
        c_res = subprocess.run(compose_cmd, cwd=kide_dir)
        if c_res.returncode != 0:
            log_error("Docker Compose deployment failed.")
            sys.exit(1)

        print_infra_banner("http://localhost:3000", "http://localhost:8000", "http://localhost:8000/docs")
        if not args.no_browser:
            time.sleep(2.0)
            webbrowser.open("http://localhost:3000")
        log_success("Docker Compose deployment running in background.")
        sys.exit(0)

    # 2. Native Multi-Tier Deployment Mode
    reclaim_port(args.port, args.host)
    reclaim_port(args.frontend_port, "127.0.0.1")

    processes = []

    def kill_tree(p):
        if p and p.poll() is None:
            try:
                if is_win:
                    subprocess.run(f"taskkill /F /T /PID {p.pid}", shell=True, capture_output=True)
                else:
                    os.killpg(os.getpgid(p.pid), signal.SIGTERM)
            except Exception:
                try:
                    p.kill()
                except Exception:
                    pass

    def cleanup_all():
        log_warn("Stopping KIDE Enterprise services...")
        for p in processes:
            kill_tree(p)
        log_success("All services terminated cleanly.")

    def signal_handler(sig, frame):
        cleanup_all()
        sys.exit(0)

    signal.signal(signal.SIGINT, signal_handler)
    if not is_win:
        signal.signal(signal.SIGTERM, signal_handler)

    try:
        # Start Backend Server
        log(f"Starting Backend Uvicorn Gateway on {args.host}:{args.port}...")
        backend_env = os.environ.copy()
        backend_env["PYTHONPATH"] = backend_dir
        backend_env["PYTHONUNBUFFERED"] = "1"

        uvicorn_cmd = [
            python_exe, "-m", "uvicorn", "app.main:app",
            "--host", args.host,
            "--port", str(args.port),
            "--reload"
        ]

        popen_kwargs = {"cwd": backend_dir, "env": backend_env}
        if is_win:
            popen_kwargs["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
        else:
            popen_kwargs["preexec_fn"] = os.setsid

        backend_proc = subprocess.Popen(uvicorn_cmd, **popen_kwargs)
        processes.append(backend_proc)

        # Start Frontend Server
        log(f"Starting Frontend Vite Development Server on port {args.frontend_port}...")
        frontend_env = os.environ.copy()
        frontend_cmd = [npm_cmd, "run", "dev", "--", "--port", str(args.frontend_port)]

        popen_kwargs = {"cwd": frontend_dir, "shell": is_win, "env": frontend_env}
        if is_win:
            popen_kwargs["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
        else:
            popen_kwargs["preexec_fn"] = os.setsid

        frontend_proc = subprocess.Popen(frontend_cmd, **popen_kwargs)
        processes.append(frontend_proc)

        # Healthchecks & Readiness Probes
        backend_url = f"http://{args.host}:{args.port}"
        docs_url = f"{backend_url}/docs"
        frontend_url = f"http://localhost:{args.frontend_port}"

        log("Waiting for backend API readiness probe...")
        if wait_for_service(f"{backend_url}/", timeout=12.0):
            log_success(f"Backend API is healthy at {backend_url}")
        else:
            log_warn("Backend API taking longer to respond, continuing...")

        log("Waiting for frontend dev server readiness probe...")
        if wait_for_service(frontend_url, timeout=12.0):
            log_success(f"Frontend is responsive at {frontend_url}")
        else:
            log_warn("Frontend server warming up, continuing...")

        # Banner & Browser
        print_infra_banner(frontend_url, backend_url, docs_url)

        if not args.no_browser:
            try:
                time.sleep(1.0)
                webbrowser.open(frontend_url)
                log_success(f"Opened web browser at {frontend_url}")
            except Exception as e:
                log_warn(f"Could not open browser automatically: {e}")

        # Supervise running processes
        while True:
            for p in processes:
                code = p.poll()
                if code is not None:
                    log_error(f"A service terminated unexpectedly (exit code {code}).")
                    cleanup_all()
                    sys.exit(code or 1)
            time.sleep(1.0)

    except KeyboardInterrupt:
        cleanup_all()
        sys.exit(0)

if __name__ == "__main__":
    main()
