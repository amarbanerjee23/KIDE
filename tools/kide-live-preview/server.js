#!/usr/bin/env node
'use strict';

/**
 * KIDE live preview.
 *
 * Point it at a folder of .cap and .activity files and open the address it
 * prints. Every time a model is saved, the diagram in the browser redraws, so
 * the text and the picture stay side by side without launching Eclipse.
 *
 *   node tools/kide-live-preview/server.js demo/example-workspace
 */

const fs = require('fs');
const http = require('http');
const path = require('path');

const { parseFile, review } = require('./parser');
const { renderModels } = require('./render');

const watchDir = path.resolve(process.argv[2] || process.cwd());
const port = Number(process.env.PORT || 7654);

let version = Date.now();

function collectModels() {
  const models = [];
  const walk = (dir) => {
    for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
      if (entry.name.startsWith('.') || entry.name === 'node_modules') continue;
      const full = path.join(dir, entry.name);
      if (entry.isDirectory()) {
        walk(full);
      } else if (/\.(cap|activity)$/i.test(entry.name)) {
        const model = parseFile(entry.name, fs.readFileSync(full, 'utf8'));
        if (model) {
          model.path = path.relative(watchDir, full);
          models.push(model);
        }
      }
    }
  };
  walk(watchDir);
  return models;
}

function page() {
  const models = collectModels();
  const notes = review(models);
  const svg = renderModels(models);
  const noteList = notes.length
    ? notes
        .map(
          (note) =>
            `<li class="${note.level}"><span></span>${note.text.replace(/[&<>]/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;' }[c]))}</li>`
        )
        .join('')
    : '<li class="ok"><span></span>Everything here holds together.</li>';
  const files = models.map((m) => `<code>${m.path}</code>`).join(' ') || '<em>no models found yet</em>';

  return `<!doctype html>
<html lang="en"><head><meta charset="utf-8"/>
<title>KIDE live preview</title>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<style>
  :root { color-scheme: light; }
  body { margin: 0; font: 14px/1.5 'Segoe UI', system-ui, sans-serif; background: #f3f5f9; color: #1f2430; }
  header { padding: 18px 24px; background: #fff; border-bottom: 1px solid #e2e6ee; }
  h1 { margin: 0 0 4px; font-size: 18px; }
  header p { margin: 0; color: #606e85; }
  code { background: #eef1f7; border-radius: 4px; padding: 1px 6px; }
  main { padding: 24px; display: grid; gap: 20px; grid-template-columns: minmax(0,1fr) 320px; align-items: start; }
  svg { max-width: 100%; height: auto; }
  .panel { background: #fff; border: 1px solid #e2e6ee; border-radius: 12px; padding: 16px; overflow: auto; }
  ul { margin: 0; padding: 0; list-style: none; }
  li { display: flex; gap: 10px; padding: 8px 0; border-bottom: 1px solid #eef1f7; }
  li span { width: 8px; height: 8px; border-radius: 50%; margin-top: 7px; flex: none; }
  li.error span { background: #d0433a; } li.warning span { background: #e2982e; } li.ok span { background: #3ea676; }
  @media (max-width: 1100px) { main { grid-template-columns: 1fr; } }
</style></head>
<body>
<header><h1>KIDE live preview</h1><p>Watching ${watchDir} — ${files}</p></header>
<main>
  <div class="panel">${svg}</div>
  <div class="panel"><strong>Model review</strong><ul>${noteList}</ul></div>
</main>
<script>
  let current = '${version}';
  setInterval(async () => {
    try {
      const res = await fetch('/version');
      const next = await res.text();
      if (next !== current) location.reload();
    } catch (e) { /* server restarting */ }
  }, 700);
</script>
</body></html>`;
}

const server = http.createServer((req, res) => {
  if (req.url === '/version') {
    res.writeHead(200, { 'content-type': 'text/plain' });
    res.end(String(version));
    return;
  }
  res.writeHead(200, { 'content-type': 'text/html; charset=utf-8', 'cache-control': 'no-store' });
  res.end(page());
});

if (!fs.existsSync(watchDir)) {
  console.error(`Nothing to watch: ${watchDir} does not exist.`);
  process.exit(1);
}

fs.watch(watchDir, { recursive: true }, (_event, filename) => {
  if (!filename || /\.(cap|activity)$/i.test(String(filename))) {
    version = Date.now();
  }
});

server.listen(port, () => {
  console.log(`KIDE live preview watching ${watchDir}`);
  console.log(`Open http://localhost:${port}`);
});
