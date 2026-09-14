/**
 * Hover Provider for KIDE Enterprise.
 * Implements Monaco's HoverProvider for all KIDE DSLs.
 * Displays rich markdown with entity kind, signature, parameters, container,
 * declaration location, and documentation.
 */

import * as monaco from 'monaco-editor';
import { projectSymbolIndex, IndexedSymbol } from './symbolIndex';
import { getLanguageForArtifact } from './languages';

export class KideHoverProvider implements monaco.languages.HoverProvider {
  public provideHover(
    model: monaco.editor.ITextModel,
    position: monaco.Position,
    _token: monaco.CancellationToken
  ): monaco.languages.ProviderResult<monaco.languages.Hover> {
    const wordInfo = model.getWordAtPosition(position);
    if (!wordInfo) return null;

    const word = wordInfo.word;
    const currentPath = model.uri.path || model.uri.fsPath;
    const lang = getLanguageForArtifact(currentPath);

    // First try exact matches in the current file
    const allSymbols = projectSymbolIndex.getAllSymbols();
    const matchingSymbols = allSymbols.filter(s => s.name === word);

    if (matchingSymbols.length === 0) {
      // Check for built-in keywords
      const keywordHover = this.getKeywordHover(word, lang);
      if (keywordHover) {
        return {
          range: new monaco.Range(position.lineNumber, wordInfo.startColumn, position.lineNumber, wordInfo.endColumn),
          contents: [{ value: keywordHover }]
        };
      }
      return null;
    }

    // Prioritize symbol from the same file, or the primary match
    const primary = matchingSymbols.find(s => s.fileName && currentPath.includes(s.fileName)) || matchingSymbols[0];

    const markdown = this.formatSymbolMarkdown(primary, matchingSymbols);

    return {
      range: new monaco.Range(position.lineNumber, wordInfo.startColumn, position.lineNumber, wordInfo.endColumn),
      contents: [{ value: markdown }]
    };
  }

  private formatSymbolMarkdown(sym: IndexedSymbol, allMatches: IndexedSymbol[]): string {
    const parts: string[] = [];

    // Header badge
    parts.push(`### \`${sym.kind}\` **${sym.name}**`);

    // Container / Interface info
    if (sym.containerName) {
      parts.push(`*Member of:* \`${sym.containerName}\``);
    }

    // Declaring file link
    parts.push(`*Declared in:* \`${sym.fileName}\` (line ${sym.range.startLine})`);

    // Detail / signature
    if (sym.detail) {
      parts.push('```kide\n' + sym.detail + '\n```');
    }

    // Documentation
    if (sym.documentation) {
      parts.push(sym.documentation);
    }

    // Multiple definitions hint
    if (allMatches.length > 1) {
      parts.push(`\n---\n*Note: Found ${allMatches.length} declarations with name \`${sym.name}\` across workspace.*`);
    }

    return parts.join('\n\n');
  }

  private getKeywordHover(word: string, lang: string): string | null {
    const KEYWORD_DOCS: Record<string, Record<string, string>> = {
      capabilitydsl: {
        'Capability': '**Capability** declares an industrial automation device or subsystem capability contract.',
        'compatible': '**compatible component interface** binds this capability to concrete MNC-ML component interfaces.',
        'Init': '**Init** block defines initialization actions (firing commands, subscribing to alarms/events/data).',
        'providesControlCapabilities': '**providesControlCapabilities** declares commands, events, alarms, and datapoints exposed by this capability.',
        'providesOutcomes': '**providesOutcomes** declares expected responses, events, and alarms resulting from executing this capability.'
      },
      activitydsl: {
        'ActivityDiagram': '**ActivityDiagram** defines a supervisory workflow execution graph.',
        'on': '**on context** specifies the shared domain DataModel context for the workflow.',
        'Activity': '**Activity** declares an individual execution node within the supervisory workflow.',
        'requireCapability': '**requireCapability** binds the activity to a specific device capability contract.',
        'requireOperation': '**requireOperation** binds the activity to an executable low-level script/operation.',
        'nextActivity': '**nextActivity** specifies the unconditional successor activity.',
        'conditions': '**conditions** specifies rule-based branch transitions depending on outcomes.'
      },
      operationdsl: {
        'Operation': '**Operation** defines a parameterized low-level device control routine or executable script.',
        'execute': '**execute** defines the shell, python, or device script to invoke.',
        'return': '**return** specifies the typed return parameter.'
      },
      dmldsl: {
        'Package': '**Package** organizes data models into a logical namespace.',
        'DataModel': '**DataModel** defines structured entities with typed primitives and composite associations.',
        'primitives': '**primitives** defines scalar fields (int, boolean, float, string, object, date).',
        'composites': '**composites** defines references to other DataModels.'
      },
      mncml: {
        'Model': '**Model** root container for MNC-ML interfaces and control nodes.',
        'InterfaceDescription': '**InterfaceDescription** specifies the hardware/software communication boundary.',
        'ControlNode': '**ControlNode** defines a deterministic hierarchical state machine implementing an interface.',
        'implements': '**implements interface** links the control node to its interface contract.',
        'operatingStates': '**operatingStates** defines the operational finite state set (startStates, endStates).'
      }
    };

    const langDocs = KEYWORD_DOCS[lang];
    if (langDocs && langDocs[word]) {
      return langDocs[word];
    }
    return null;
  }
}

export const kideHoverProvider = new KideHoverProvider();

