'use strict';

/** Draws the parsed models as one SVG, using the KIDE palette. */

const PALETTE = {
  capability: '#5284e2',
  capabilityLight: '#dbe6fa',
  activity: '#e2982e',
  activityLight: '#fceed6',
  data: '#3ea676',
  dataLight: '#dbf2e7',
  structure: '#606e85',
  edge: '#8a95a8',
  ink: '#1f2430',
  paper: '#fdfdff',
};

const escape = (value) =>
  String(value == null ? '' : value).replace(/[&<>"]/g, (c) =>
    ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c])
  );

function card(x, y, w, h, fill, stroke) {
  return `<rect x="${x}" y="${y}" width="${w}" height="${h}" rx="10" fill="${fill}" stroke="${stroke}" stroke-width="1.5"/>`;
}

function text(x, y, value, opts = {}) {
  const { size = 13, weight = 400, fill = PALETTE.ink, anchor = 'start' } = opts;
  return `<text x="${x}" y="${y}" font-family="'Segoe UI',system-ui,sans-serif" font-size="${size}" font-weight="${weight}" fill="${fill}" text-anchor="${anchor}">${escape(value)}</text>`;
}

function renderCapability(model, x, y, width) {
  const rows = [
    ['Commands', model.commands, PALETTE.capability],
    ['Events', model.events, PALETTE.activity],
    ['Alarms', model.alarms, PALETTE.structure],
    ['Data points', model.dataPoints, PALETTE.data],
    ['Outcomes', model.responses, PALETTE.data],
  ].filter(([, items]) => items.length > 0);
  const height = 54 + rows.length * 24;
  let svg = card(x, y, width, height, PALETTE.capabilityLight, PALETTE.capability);
  svg += text(x + 16, y + 26, model.name, { size: 15, weight: 700, fill: PALETTE.capability });
  svg += text(x + 16, y + 44, `capability${model.interfaces.length ? ' of ' + model.interfaces.join(', ') : ''}`, {
    size: 11,
    fill: PALETTE.structure,
  });
  rows.forEach(([label, items, colour], index) => {
    const rowY = y + 66 + index * 24;
    svg += `<circle cx="${x + 22}" cy="${rowY - 4}" r="4" fill="${colour}"/>`;
    svg += text(x + 34, y + 70 + index * 24, `${label}: ${items.join(', ')}`, { size: 12 });
  });
  return { svg, height };
}

function renderActivityDiagram(model, x, y, width) {
  const rowHeight = 74;
  const height = 52 + Math.max(model.activities.length, 1) * rowHeight;
  let svg = card(x, y, width, height, PALETTE.paper, PALETTE.structure);
  svg += text(x + 16, y + 28, model.name, { size: 15, weight: 700, fill: PALETTE.structure });
  const positions = new Map();
  model.activities.forEach((activity, index) => {
    const boxY = y + 46 + index * rowHeight;
    positions.set(activity.name, { y: boxY, height: 56 });
    svg += card(x + 20, boxY, width - 40, 56, PALETTE.activityLight, PALETTE.activity);
    svg += text(x + 36, boxY + 24, activity.name, { size: 14, weight: 700, fill: '#b07014' });
    const detail = [
      activity.capability ? `uses ${activity.capability}` : 'no capability required',
      activity.time ? `takes ${activity.time}` : null,
    ]
      .filter(Boolean)
      .join('  ·  ');
    svg += text(x + 36, boxY + 43, detail, { size: 11, fill: PALETTE.structure });
    if (activity.description) {
      svg += text(x + width - 36, boxY + 24, activity.description, {
        size: 11,
        fill: PALETTE.structure,
        anchor: 'end',
      });
    }
  });
  model.activities.forEach((activity) => {
    const from = positions.get(activity.name);
    const links = [
      ...(activity.next ? [{ target: activity.next, label: null }] : []),
      ...activity.branches.map((b) => ({ target: b.target, label: b.outcome })),
    ];
    links.forEach((link, i) => {
      const to = positions.get(link.target);
      if (!from || !to) return;
      const startX = x + 12;
      const y1 = from.y + from.height / 2;
      const y2 = to.y + to.height / 2;
      const bend = startX - 8 - i * 10;
      svg += `<path d="M ${x + 20} ${y1} H ${bend} V ${y2} H ${x + 20}" fill="none" stroke="${PALETTE.edge}" stroke-width="1.5" marker-end="url(#arrow)"/>`;
      if (link.label) {
        svg += text(bend - 6, (y1 + y2) / 2, link.label, { size: 10, fill: PALETTE.edge, anchor: 'end' });
      }
    });
  });
  return { svg, height };
}

function renderModels(models) {
  const width = 620;
  const gap = 24;
  // Room on the left for the hand-over arrows, which loop outside the diagram.
  const left = 150;
  const capabilities = models.filter((m) => m.kind === 'capability');
  const diagrams = models.filter((m) => m.kind === 'activityDiagram');

  let leftY = 24;
  let rightY = 24;
  let body = '';
  for (const model of diagrams) {
    const { svg, height } = renderActivityDiagram(model, left, leftY, width);
    body += svg;
    leftY += height + gap;
  }
  for (const model of capabilities) {
    const { svg, height } = renderCapability(model, left + width + gap, rightY, 420);
    body += svg;
    rightY += height + gap;
  }
  const totalWidth = left + width + gap + 420 + 24;
  const totalHeight = Math.max(leftY, rightY, 200) + 8;
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${totalWidth}" height="${totalHeight}" viewBox="0 0 ${totalWidth} ${totalHeight}">
  <defs>
    <marker id="arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" fill="${PALETTE.edge}"/>
    </marker>
  </defs>
  <rect width="100%" height="100%" fill="${PALETTE.paper}"/>
  ${body}
</svg>`;
}

module.exports = { renderModels, PALETTE };
