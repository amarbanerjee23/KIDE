/**
 * KIDE Enterprise — Electron Preload Script
 *
 * Exposes a safe bridge between the renderer (web) process
 * and the main (Node.js) process via contextBridge.
 */
const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("kideDesktop", {
  // ─── Platform Info ─────────────────────────────────────────
  isDesktop: true,
  platform: process.platform,

  // ─── Server Configuration ──────────────────────────────────
  getServerUrl: () => ipcRenderer.invoke("app:get-server-url"),
  setServerUrl: (url) => ipcRenderer.invoke("app:set-server-url", url),
  getUserDataPath: () => ipcRenderer.invoke("app:get-user-data-path"),

  // ─── Native File Dialogs ───────────────────────────────────
  saveFile: (defaultName, content) =>
    ipcRenderer.invoke("dialog:save-file", defaultName, content),
  openFile: () => ipcRenderer.invoke("dialog:open-file"),

  // ─── Menu Event Listeners ──────────────────────────────────
  onMenuNewProject: (callback) =>
    ipcRenderer.on("menu:new-project", () => callback()),
  onMenuOpenFile: (callback) =>
    ipcRenderer.on("menu:open-file", (_event, filePath) => callback(filePath)),
  onMenuSave: (callback) =>
    ipcRenderer.on("menu:save", () => callback()),
  onMenuExport: (callback) =>
    ipcRenderer.on("menu:export", (_event, format) => callback(format)),
  onMenuTransform: (callback) =>
    ipcRenderer.on("menu:transform", () => callback()),
  onMenuValidate: (callback) =>
    ipcRenderer.on("menu:validate", () => callback()),

  // ─── Cleanup ───────────────────────────────────────────────
  removeAllListeners: (channel) => ipcRenderer.removeAllListeners(channel),
});
