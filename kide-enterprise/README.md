# KIDE Enterprise

**Knowledge-Integrated DSL Engineering Workspace** — Enterprise-grade tooling for automating industrial control software development.

KIDE Enterprise transforms high-level **Activity Diagrams** (operational workflow descriptions) into fully synthesized **MNC (Monitoring & Control) models** — hierarchical control nodes with commands, events, alarms, data points, and operating state machines.

---

## Key Features

- **Visual Activity Diagram Editor** — Drag-and-drop flow editor powered by React Flow
- **Textual DSL Editor** — Monaco-based JSON editor with syntax highlighting and validation
- **Automated Synthesis** — One-click transformation from activity models to supervisory control architectures
- **Multi-Format Export** — JSON, MNC-ML textual DSL, Python control stubs
- **Project Management** — Organize models into projects with versioning
- **Multi-Tenant** — Organizations, teams, role-based access control
- **Desktop + Web** — Same experience in the browser or as a native desktop application

---

## Quick Start

### Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| Python | 3.11+ | Backend API |
| Node.js | 20+ LTS | Frontend build |
| PostgreSQL | 16+ | Database (optional — SQLite used by default) |

### 1. Backend

```bash
cd kide-enterprise/backend

# Create virtual environment
python -m venv .venv
# Windows:
.venv\Scripts\activate
# macOS/Linux:
# source .venv/bin/activate

# Install dependencies
pip install -e .

# Copy environment config
cp ../.env.example .env

# Start the server
uvicorn app.main:app --reload --port 8000
```

The API is now running at `http://localhost:8000`. Visit `http://localhost:8000/docs` for interactive Swagger documentation.

### 2. Frontend

```bash
cd kide-enterprise/frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

The web app is now running at `http://localhost:5173`.

### 3. Desktop App (Optional)

```bash
cd kide-enterprise/desktop

# Install dependencies
npm install

# Run in dev mode (connects to local backend + frontend)
npm run start:dev

# Build distributable installer
npm run build:win    # Windows .exe/.msi
npm run build:mac    # macOS .dmg
npm run build:linux  # Linux .AppImage/.deb
```

### 4. Docker (Production)

```bash
cd kide-enterprise

# Start all services
docker-compose up -d

# Web app available at http://localhost:3000
# API available at http://localhost:8000
```

---

## Architecture

```
┌────────────────────────────────────────────────────────┐
│             Frontend (React + TypeScript)               │
│  Monaco DSL Editor │ React Flow Diagrams │ Dashboard   │
├────────────────────────────────────────────────────────┤
│              REST API (FastAPI + Python)                │
│  Auth │ Projects │ Transform Engine │ Export            │
├────────────────────────────────────────────────────────┤
│           PostgreSQL / SQLite Database                  │
└────────────────────────────────────────────────────────┘
```

### Core Transformation Pipeline

```
Activity Diagram (JSON)
        │
        ▼
   ┌─────────┐
   │ Validate │ ── Check names, transitions, types
   └────┬────┘
        ▼
   ┌───────────┐
   │ Synthesize │ ── Extract commands, events, alarms, datapoints
   └─────┬─────┘     Generate lifecycle blocks (INIT, Started, etc.)
         ▼
   ┌─────────┐
   │ Compose  │ ── Assemble ControlNode with all blocks + states
   └────┬────┘
        ▼
   MNC Model (JSON / DSL / Python)
```

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user + organization |
| POST | `/api/v1/auth/login` | Login, receive JWT tokens |
| GET | `/api/v1/projects/` | List projects |
| POST | `/api/v1/projects/` | Create project |
| POST | `/api/v1/transform/` | Transform activity → MNC |
| POST | `/api/v1/export/json` | Export model as JSON |
| POST | `/api/v1/export/dsl` | Export model as MNC-ML DSL |
| POST | `/api/v1/export/python` | Export model as Python stubs |

Full interactive docs: `http://localhost:8000/docs`

---

## Project Structure

```
kide-enterprise/
├── backend/                 # Python FastAPI backend
│   ├── app/
│   │   ├── auth/           # JWT authentication
│   │   ├── models/         # SQLAlchemy ORM models
│   │   ├── routers/        # API endpoint handlers
│   │   ├── schemas/        # Pydantic request/response schemas
│   │   ├── services/       # Core business logic
│   │   │   ├── synthesis.py    # Interface block synthesis
│   │   │   ├── composition.py  # Control node composition
│   │   │   ├── transformer.py  # Orchestration pipeline
│   │   │   ├── exporter.py     # Multi-format export
│   │   │   └── validator.py    # Model validation
│   │   └── main.py         # App entry point
│   └── tests/              # Pytest test suite
├── frontend/                # React + TypeScript + Vite
│   └── src/
│       ├── components/     # UI components
│       │   ├── editor/     # Monaco DSL editor
│       │   ├── flow/       # React Flow visual editor
│       │   └── output/     # Transform output panels
│       ├── pages/          # Route pages
│       ├── stores/         # Zustand state management
│       └── api/            # API client
├── desktop/                 # Electron desktop wrapper
├── docker-compose.yml       # Production deployment
└── legacy/                  # Original Eclipse/Xtext workspace
    └── (com.mncml, com.smr.activity, etc.)
```

---

## License

Proprietary — © 2024-2026 KIDE Enterprise. All rights reserved.
