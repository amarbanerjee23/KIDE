/**
 * Code Actions & Quick Fix Provider for KIDE Enterprise.
 * Implements Monaco's CodeActionProvider (Ctrl+.).
 * Provides deterministic quick fixes for unresolved references, missing symbols,
 * and automated stubs.
 */

import * as monaco from 'monaco-editor';
import { projectSymbolIndex } from './symbolIndex';
import { formatKideDsl } from './formatting';

export class KideCodeActionProvider implements monaco.languages.CodeActionProvider {
  public provideCodeActions(
    model: monaco.editor.ITextModel,
    _range: monaco.Range,
    context: monaco.languages.CodeActionContext,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.CodeActionList> {
    const actions: monaco.languages.CodeAction[] = [];

    // Check diagnostics / markers provided in context
    for (const marker of context.markers) {
      const msg = marker.message;

      // 1. Unresolved Capability
      const capMatch = msg.match(/Capability "([A-Za-z0-9_]+)" not found/);
      if (capMatch) {
        const unknownName = capMatch[1];
        const allCaps = projectSymbolIndex.findSymbolsByKind('Capability');

        // Find closest match
        const closest = this.findClosestMatch(unknownName, allCaps.map(c => c.name));
        if (closest) {
          actions.push({
            title: `Replace with known Capability '${closest}'`,
            kind: 'quickfix',
            diagnostics: [marker],
            isPreferred: true,
            edit: {
              edits: [{
                resource: model.uri,
                textEdit: {
                  range: new monaco.Range(marker.startLineNumber, marker.startColumn, marker.endLineNumber, marker.endColumn),
                  text: closest
                },
                versionId: undefined
              }]
            }
          });
        }
      }

      // 2. Unresolved Operation
      const opMatch = msg.match(/Operation "([A-Za-z0-9_]+)" not found/);
      if (opMatch) {
        const unknownName = opMatch[1];
        const allOps = projectSymbolIndex.findSymbolsByKind('Operation');
        const closest = this.findClosestMatch(unknownName, allOps.map(o => o.name));
        if (closest) {
          actions.push({
            title: `Replace with known Operation '${closest}'`,
            kind: 'quickfix',
            diagnostics: [marker],
            isPreferred: true,
            edit: {
              edits: [{
                resource: model.uri,
                textEdit: {
                  range: new monaco.Range(marker.startLineNumber, marker.startColumn, marker.endLineNumber, marker.endColumn),
                  text: closest
                },
                versionId: undefined
              }]
            }
          });
        }
      }

      // 3. Unresolved DataModel context
      const dmMatch = msg.match(/DataModel "([A-Za-z0-9_]+)" not found/);
      if (dmMatch) {
        const unknownName = dmMatch[1];
        const allDms = projectSymbolIndex.findSymbolsByKind('DataModel');
        const closest = this.findClosestMatch(unknownName, allDms.map(d => d.name));
        if (closest) {
          actions.push({
            title: `Replace with known DataModel '${closest}'`,
            kind: 'quickfix',
            diagnostics: [marker],
            isPreferred: true,
            edit: {
              edits: [{
                resource: model.uri,
                textEdit: {
                  range: new monaco.Range(marker.startLineNumber, marker.startColumn, marker.endLineNumber, marker.endColumn),
                  text: closest
                },
                versionId: undefined
              }]
            }
          });
        }
      }

      // 4. Unresolved Activity transition target
      const actMatch = msg.match(/Activity "([A-Za-z0-9_]+)" is not defined in this diagram/);
      if (actMatch) {
        const missingAct = actMatch[1];
        // Action to append missing Activity stub to current diagram
        const lastLine = model.getLineCount();
        const stubText = `\n    Activity ${missingAct} {\n        description: "Auto-generated step for ${missingAct}"\n    }\n`;

        actions.push({
          title: `Create missing Activity stub '${missingAct}'`,
          kind: 'quickfix',
          diagnostics: [marker],
          edit: {
            edits: [{
              resource: model.uri,
              textEdit: {
                // Insert before the last closing brace
                range: new monaco.Range(lastLine, 1, lastLine, 1),
                text: stubText
              },
              versionId: undefined
            }]
          }
        });
      }
    }

    // Always provide Format Document action if in range
    actions.push({
      title: 'Format Document (Idempotent)',
      kind: 'source.fixAll',
      edit: {
        edits: [{
          resource: model.uri,
          textEdit: {
            range: model.getFullModelRange(),
            text: formatKideDsl(model.getValue())
          },
          versionId: undefined
        }]
      }
    });

    return {
      actions,
      dispose: () => {}
    };
  }

  private findClosestMatch(target: string, candidates: string[]): string | null {
    if (candidates.length === 0) return null;
    const lowerTarget = target.toLowerCase();

    // 1. Case-insensitive exact or substring match
    const sub = candidates.find(c => c.toLowerCase() === lowerTarget || c.toLowerCase().includes(lowerTarget) || lowerTarget.includes(c.toLowerCase()));
    if (sub) return sub;

    // 2. Levenshtein distance
    let bestDist = Infinity;
    let bestCand = candidates[0];

    for (const cand of candidates) {
      const dist = this.levenshtein(target, cand);
      if (dist < bestDist) {
        bestDist = dist;
        bestCand = cand;
      }
    }

    // Return best if distance is reasonably small relative to length
    if (bestDist <= Math.max(3, target.length / 2)) {
      return bestCand;
    }

    return null;
  }

  private levenshtein(a: string, b: string): number {
    const m = a.length;
    const n = b.length;
    const dp: number[][] = Array.from({ length: m + 1 }, () => Array(n + 1).fill(0));

    for (let i = 0; i <= m; i++) dp[i][0] = i;
    for (let j = 0; j <= n; j++) dp[0][j] = j;

    for (let i = 1; i <= m; i++) {
      for (let j = 1; j <= n; j++) {
        const cost = a[i - 1] === b[j - 1] ? 0 : 1;
        dp[i][j] = Math.min(
          dp[i - 1][j] + 1,
          dp[i][j - 1] + 1,
          dp[i - 1][j - 1] + cost
        );
      }
    }

    return dp[m][n];
  }
}

export const kideCodeActionProvider = new KideCodeActionProvider();
