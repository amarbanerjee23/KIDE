import os
import subprocess
import sys
import argparse
import time

def run_command(command, cwd=None, shell=True):
    print(f"Running: {command} in {cwd or os.getcwd()}")
    return subprocess.Popen(command, cwd=cwd, shell=shell)

def main():
    parser = argparse.ArgumentParser(description="Run KIDE Enterprise Application")
    parser.add_argument("--mode", choices=["web", "desktop"], default="web", help="Mode to run the application (web or desktop)")
    args = parser.parse_args()

    base_dir = os.path.dirname(os.path.abspath(__file__))
    kide_enterprise_dir = os.path.join(base_dir, "kide-enterprise")

    if not os.path.exists(kide_enterprise_dir):
        print(f"Error: kide-enterprise directory not found at {kide_enterprise_dir}")
        sys.exit(1)

    backend_dir = os.path.join(kide_enterprise_dir, "backend")
    frontend_dir = os.path.join(kide_enterprise_dir, "frontend")
    desktop_dir = os.path.join(kide_enterprise_dir, "desktop")

    # 1. Setup Backend
    print("--- Setting up Backend ---")
    venv_dir = os.path.join(backend_dir, ".venv")
    if not os.path.exists(venv_dir):
        print("Creating virtual environment...")
        subprocess.run([sys.executable, "-m", "venv", ".venv"], cwd=backend_dir, check=True)
        pip_exe = os.path.join(venv_dir, "Scripts", "pip") if os.name == "nt" else os.path.join(venv_dir, "bin", "pip")
        print("Installing backend dependencies...")
        subprocess.run([pip_exe, "install", "-e", "."], cwd=backend_dir, check=True)
    
    env_example = os.path.join(kide_enterprise_dir, ".env.example")
    env_file = os.path.join(backend_dir, ".env")
    if not os.path.exists(env_file) and os.path.exists(env_example):
        print("Copying .env file...")
        with open(env_example, 'r') as src, open(env_file, 'w') as dst:
            dst.write(src.read())

    # 2. Setup Frontend
    print("--- Setting up Frontend ---")
    if not os.path.exists(os.path.join(frontend_dir, "node_modules")):
        print("Installing frontend dependencies...")
        subprocess.run(["npm", "install"], cwd=frontend_dir, shell=True, check=True)

    # 3. Setup Desktop (if needed)
    if args.mode == "desktop":
        print("--- Setting up Desktop ---")
        if not os.path.exists(os.path.join(desktop_dir, "node_modules")):
            print("Installing desktop dependencies...")
            subprocess.run(["npm", "install"], cwd=desktop_dir, shell=True, check=True)

    # Start Processes
    processes = []
    
    try:
        # Start Frontend
        print("Starting Frontend Server...")
        p_frontend = run_command("npm run dev", cwd=frontend_dir)
        processes.append(p_frontend)

        if args.mode == "web":
            # Start Backend
            print("Starting Backend Server...")
            uvicorn_exe = os.path.join(venv_dir, "Scripts", "uvicorn") if os.name == "nt" else os.path.join(venv_dir, "bin", "uvicorn")
            p_backend = run_command(f"{uvicorn_exe} app.main:app --reload --port 8000", cwd=backend_dir)
            processes.append(p_backend)
            print("\n>>> Web application running at http://localhost:5173 <<<")
            print(">>> API running at http://localhost:8000 <<<\n")
        
        elif args.mode == "desktop":
            # Wait a bit for frontend to start
            time.sleep(3)
            # Start Desktop (Desktop main.js automatically starts the backend in dev mode)
            print("Starting Desktop App...")
            p_desktop = run_command("npm run start:dev", cwd=desktop_dir)
            processes.append(p_desktop)
            print("\n>>> Desktop application running! <<<\n")

        # Keep running until user stops
        for p in processes:
            p.wait()

    except KeyboardInterrupt:
        print("\nStopping all processes...")
        for p in processes:
            p.terminate()
        sys.exit(0)

if __name__ == "__main__":
    main()

