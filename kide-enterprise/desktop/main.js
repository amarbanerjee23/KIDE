/**
 * KIDE Enterprise — Electron Main Process
 *
 * Creates the desktop application window and optionally spawns
 * an embedded FastAPI backend for fully offline operation.
 */
const { app, BrowserWindow, Menu, dialog, shell, ipcMain } = require("electron");
const path = require("path");
const { spawn } = require("child_process");
const Store = require("electron-store");

const store = new Store({
  defaults: {
    serverUrl: "http://localhost:8000",
    embeddedBackend: true,
    windowBounds: { width: 1400, height: 900 },
    lastProject: null,
  },
});

let mainWindow = null;
let backendProcess = null;
const isDev = process.env.KIDE_DEV === "1";

// ─── Backend Lifecycle ─────────────────────────────────────────

function getBackendPath() {
  if (isDev) {
    return path.join(__dirname, "..", "backend");
  }
  return path.join(process.resourcesPath, "backend");
}

function startEmbeddedBackend() {
  if (!store.get("embeddedBackend")) return Promise.resolve();

  return new Promise((resolve, reject) => {
    let backendDir = getBackendPath();
    let cmd, args;

    if (isDev) {
      cmd = process.platform === "win32" ? "python" : "python3";
      args = ["-m", "uvicorn", "app.main:app", "--host", "127.0.0.1", "--port", "8000"];
    } else {
      // In production, run the packaged pyinstaller executable
      const exeName = process.platform === "win32" ? "kide-backend.exe" : "kide-backend";
      cmd = path.join(backendDir, exeName);
      args = [];
    }

    backendProcess = spawn(
      cmd,
      args,
      {
        cwd: backendDir,
        env: {
          ...process.env,
          DATABASE_URL: `sqlite+aiosqlite:///${path.join(app.getPath("userData"), "kide_data.db")}`,
          SECRET_KEY: "desktop-embedded-key-" + Date.now(),
          DEBUG: "false",
          CORS_ORIGINS: '["http://localhost:5173"]',
        },
        stdio: ["ignore", "pipe", "pipe"],
      }
    );

    let started = false;

    backendProcess.stdout.on("data", (data) => {
      const msg = data.toString();
      console.log("[backend]", msg);
      if (!started && msg.includes("Uvicorn running")) {
        started = true;
        resolve();
      }
    });

    backendProcess.stderr.on("data", (data) => {
      const msg = data.toString();
      console.error("[backend]", msg);
      if (!started && msg.includes("Uvicorn running")) {
        started = true;
        resolve();
      }
    });

    backendProcess.on("error", (err) => {
      console.error("Failed to start backend:", err);
      if (!started) reject(err);
    });

    backendProcess.on("exit", (code) => {
      console.log("Backend exited with code:", code);
      backendProcess = null;
    });

    // Timeout: if backend doesn't start within 15 seconds, continue anyway
    setTimeout(() => {
      if (!started) {
        started = true;
        console.warn("Backend start timeout — continuing without embedded server");
        resolve();
      }
    }, 15000);
  });
}

function stopEmbeddedBackend() {
  if (backendProcess) {
    backendProcess.kill();
    backendProcess = null;
  }
}

// ─── Window Creation ───────────────────────────────────────────

function createMainWindow() {
  const { width, height } = store.get("windowBounds");

  mainWindow = new BrowserWindow({
    width,
    height,
    minWidth: 1024,
    minHeight: 700,
    title: "KIDE Enterprise",
    backgroundColor: "#1a1a2e",
    webPreferences: {
      preload: path.join(__dirname, "preload.js"),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: true,
    },
  });

  // Load the frontend
  if (isDev) {
    mainWindow.loadURL("http://localhost:5173");
    mainWindow.webContents.openDevTools({ mode: "detach" });
  } else {
    mainWindow.loadFile(path.join(__dirname, "frontend-dist", "index.html"));
  }

  // Save window bounds on resize
  mainWindow.on("resize", () => {
    const bounds = mainWindow.getBounds();
    store.set("windowBounds", { width: bounds.width, height: bounds.height });
  });

  mainWindow.on("closed", () => {
    mainWindow = null;
  });
}

// ─── Application Menu ──────────────────────────────────────────

function createMenu() {
  const template = [
    {
      label: "File",
      submenu: [
        {
          label: "New Project",
          accelerator: "CmdOrCtrl+N",
          click: () => mainWindow?.webContents.send("menu:new-project"),
        },
        {
          label: "Open Project...",
          accelerator: "CmdOrCtrl+O",
          click: async () => {
            const result = await dialog.showOpenDialog(mainWindow, {
              properties: ["openFile"],
              filters: [
                { name: "Activity Diagrams", extensions: ["json"] },
                { name: "All Files", extensions: ["*"] },
              ],
            });
            if (!result.canceled && result.filePaths.length > 0) {
              mainWindow?.webContents.send("menu:open-file", result.filePaths[0]);
            }
          },
        },
        {
          label: "Save",
          accelerator: "CmdOrCtrl+S",
          click: () => mainWindow?.webContents.send("menu:save"),
        },
        {
          label: "Export As...",
          submenu: [
            { label: "JSON", click: () => mainWindow?.webContents.send("menu:export", "json") },
            { label: "MNC-ML DSL", click: () => mainWindow?.webContents.send("menu:export", "dsl") },
            { label: "Python Stubs", click: () => mainWindow?.webContents.send("menu:export", "python") },
          ],
        },
        { type: "separator" },
        { role: "quit" },
      ],
    },
    {
      label: "Edit",
      submenu: [
        { role: "undo" },
        { role: "redo" },
        { type: "separator" },
        { role: "cut" },
        { role: "copy" },
        { role: "paste" },
        { role: "selectAll" },
      ],
    },
    {
      label: "View",
      submenu: [
        { role: "reload" },
        { role: "forceReload" },
        { role: "toggleDevTools" },
        { type: "separator" },
        { role: "resetZoom" },
        { role: "zoomIn" },
        { role: "zoomOut" },
        { type: "separator" },
        { role: "togglefullscreen" },
      ],
    },
    {
      label: "Transform",
      submenu: [
        {
          label: "Run Transformation",
          accelerator: "CmdOrCtrl+Enter",
          click: () => mainWindow?.webContents.send("menu:transform"),
        },
        {
          label: "Validate Model",
          accelerator: "CmdOrCtrl+Shift+V",
          click: () => mainWindow?.webContents.send("menu:validate"),
        },
      ],
    },
    {
      label: "Help",
      submenu: [
        {
          label: "Documentation",
          click: () => shell.openExternal("https://kide.dev/docs"),
        },
        {
          label: "Report Issue",
          click: () => shell.openExternal("https://github.com/amarbanerjee23/KIDE/issues"),
        },
        { type: "separator" },
        {
          label: "About KIDE Enterprise",
          click: () => {
            dialog.showMessageBox(mainWindow, {
              type: "info",
              title: "About KIDE Enterprise",
              message: "KIDE Enterprise v1.0.0",
              detail:
                "Knowledge-Integrated DSL Engineering Workspace\n\n" +
                "Automated control software synthesis from\n" +
                "activity diagrams using capability-driven\n" +
                "model transformations.\n\n" +
                "© 2024-2026 KIDE Enterprise",
            });
          },
        },
      ],
    },
  ];

  Menu.setApplicationMenu(Menu.buildFromTemplate(template));
}

// ─── IPC Handlers ──────────────────────────────────────────────

function setupIPC() {
  ipcMain.handle("app:get-server-url", () => store.get("serverUrl"));
  ipcMain.handle("app:set-server-url", (_event, url) => store.set("serverUrl", url));
  ipcMain.handle("app:get-user-data-path", () => app.getPath("userData"));

  ipcMain.handle("dialog:save-file", async (_event, defaultName, content) => {
    const result = await dialog.showSaveDialog(mainWindow, {
      defaultPath: defaultName,
      filters: [
        { name: "JSON Files", extensions: ["json"] },
        { name: "MNC-ML Files", extensions: ["mnc"] },
        { name: "Python Files", extensions: ["py"] },
        { name: "All Files", extensions: ["*"] },
      ],
    });
    if (!result.canceled && result.filePath) {
      const fs = require("fs");
      fs.writeFileSync(result.filePath, content, "utf-8");
      return result.filePath;
    }
    return null;
  });

  ipcMain.handle("dialog:open-file", async () => {
    const result = await dialog.showOpenDialog(mainWindow, {
      properties: ["openFile"],
      filters: [
        { name: "Activity Diagrams", extensions: ["json"] },
        { name: "All Files", extensions: ["*"] },
      ],
    });
    if (!result.canceled && result.filePaths.length > 0) {
      const fs = require("fs");
      const content = fs.readFileSync(result.filePaths[0], "utf-8");
      return { path: result.filePaths[0], content };
    }
    return null;
  });
}

// ─── App Lifecycle ─────────────────────────────────────────────

app.whenReady().then(async () => {
  try {
    await startEmbeddedBackend();
  } catch (err) {
    console.warn("Embedded backend failed to start:", err.message);
  }

  createMenu();
  setupIPC();
  createMainWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) {
      createMainWindow();
    }
  });
});

app.on("window-all-closed", () => {
  stopEmbeddedBackend();
  if (process.platform !== "darwin") {
    app.quit();
  }
});

app.on("before-quit", () => {
  stopEmbeddedBackend();
});
