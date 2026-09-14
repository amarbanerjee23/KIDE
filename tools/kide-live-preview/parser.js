'use strict';

/**
 * A tolerant reader for KIDE textual models.
 *
 * It is deliberately not the Xtext parser: it never fails, it just reports what
 * it could make out, so the preview keeps drawing while a model is half-typed.
 */

function stripComments(source) {
  return source
    .replace(/\/\*[\s\S]*?\*\//g, '')
    .split('\n')
    .map((line) => line.replace(/\/\/.*$/, ''))
    .join('\n');
}

function listAfter(text, keyword) {
  const match = new RegExp(`${keyword}\\s*:?\\s*\\[?([^\\]\\n{}]*)`, 'i').exec(text);
  if (!match) return [];
  return match[1]
    .split(/[,\s]+/)
    .map((item) => item.replace(/\(\s*\)/g, '').trim())
    .filter((item) => item && !/^[{}\[\]:]+$/.test(item));
}

/** Reads a .cap file into a capability with its control items and outcomes. */
function parseCapability(source, fileName) {
  const text = stripComments(source);
  const header = /Capability\s+(\w+)/i.exec(text);
  const iface = /component\s+interface\s+([\w,\s]+)\{/i.exec(text);
  return {
    kind: 'capability',
    file: fileName,
    name: header ? header[1] : fileName.replace(/\.cap$/i, ''),
    interfaces: iface ? iface[1].split(/[,\s]+/).filter(Boolean) : [],
    commands: listAfter(text, 'fireable\\s+commands'),
    events: listAfter(text, 'receivable\\s+events'),
    alarms: listAfter(text, 'raised\\s+alarms'),
    dataPoints: listAfter(text, 'subscribable\\s+DataPoints'),
    responses: listAfter(text, 'receivable\\s+responses'),
  };
}

/** Reads an .activity file into a diagram with its activities and links. */
function parseActivityDiagram(source, fileName) {
  const text = stripComments(source);
  const header = /ActivityDiagram\s+(\w+)/i.exec(text);
  const activities = [];
  const activityRe = /Activity\s+(\w+)\s*\{/g;
  let match;
  while ((match = activityRe.exec(text)) !== null) {
    const start = match.index + match[0].length;
    let depth = 1;
    let i = start;
    while (i < text.length && depth > 0) {
      if (text[i] === '{') depth += 1;
      else if (text[i] === '}') depth -= 1;
      i += 1;
    }
    const body = text.slice(start, i - 1);
    const capability = /requireCapability\s*:?\s*(\w+)/i.exec(body);
    const next = /nextActivity\s*:?\s*(\w+)/i.exec(body);
    const time = /time\s*:?\s*([\d.]+)\s*(\w+)?/i.exec(body);
    const description = /description\s*:?\s*"([^"]*)"/i.exec(body);
    const branches = [];
    const branchRe = /from\s+(\w+)\s*=>\s*nextActivity\s*:?\s*(\w+)/gi;
    let branch;
    while ((branch = branchRe.exec(body)) !== null) {
      branches.push({ outcome: branch[1], target: branch[2] });
    }
    activities.push({
      name: match[1],
      capability: capability ? capability[1] : null,
      next: next ? next[1] : null,
      branches,
      time: time ? `${time[1]} ${time[2] || ''}`.trim() : null,
      description: description ? description[1] : null,
    });
  }
  return {
    kind: 'activityDiagram',
    file: fileName,
    name: header ? header[1] : fileName.replace(/\.activity$/i, ''),
    activities,
  };
}

/** Plain-language remarks about a model, in the same voice as the validators. */
function review(models) {
  const notes = [];
  const capabilities = new Set(
    models.filter((m) => m.kind === 'capability').map((m) => m.name)
  );
  for (const model of models) {
    if (model.kind !== 'activityDiagram') continue;
    const names = new Set(model.activities.map((a) => a.name));
    if (model.activities.length === 0) {
      notes.push({ level: 'warning', text: `${model.name} has no activities yet.` });
    }
    for (const activity of model.activities) {
      if (!activity.capability) {
        notes.push({
          level: 'warning',
          text: `${activity.name} does not require a capability, so nothing will happen when it runs.`,
        });
      } else if (capabilities.size > 0 && !capabilities.has(activity.capability)) {
        notes.push({
          level: 'error',
          text: `${activity.name} requires the capability ${activity.capability}, which is not among the capabilities being watched.`,
        });
      }
      const targets = [activity.next, ...activity.branches.map((b) => b.target)].filter(Boolean);
      for (const target of targets) {
        if (!names.has(target)) {
          notes.push({
            level: 'error',
            text: `${activity.name} hands over to ${target}, but no activity of that name exists in ${model.name}.`,
          });
        }
      }
    }
  }
  return notes;
}

function parseFile(fileName, source) {
  if (/\.cap$/i.test(fileName)) return parseCapability(source, fileName);
  if (/\.activity$/i.test(fileName)) return parseActivityDiagram(source, fileName);
  return null;
}

module.exports = { parseFile, parseCapability, parseActivityDiagram, review };
