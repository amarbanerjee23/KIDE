import * as monaco from "monaco-editor";
import {
  INITIAL,
  Registry,
  parseRawGrammar,
  type IRawGrammar,
  type StateStack
} from "vscode-textmate";
import {
  OnigScanner,
  OnigString,
  loadWASM
} from "vscode-oniguruma";
import onigWasmUrl from "vscode-oniguruma/release/onig.wasm?url";
import { LANGUAGE_ASSETS, type LanguageAsset } from "./languageAssets";

let registration: Promise<void> | undefined;

export function ensureTextMateLanguageSupport(): Promise<void> {
  if (!registration) registration = register();
  return registration;
}

async function register(): Promise<void> {
  for (const asset of LANGUAGE_ASSETS) {
    monaco.languages.register({
      id: asset.language_id,
      extensions: ["." + asset.extension],
      aliases: [asset.display_name, asset.id]
    });
  }

  const wasm = await fetch(onigWasmUrl).then((response) => {
    if (!response.ok) throw new Error("Oniguruma WASM could not be loaded.");
    return response.arrayBuffer();
  });
  await loadWASM(wasm);

  const grammars = new Map(
    LANGUAGE_ASSETS.map((asset) => [asset.scope_name, grammarFor(asset)])
  );
  const registry = new Registry({
    onigLib: Promise.resolve({
      createOnigScanner: (sources) => new OnigScanner(sources),
      createOnigString: (value) => new OnigString(value)
    }),
    loadGrammar: async (scopeName) => {
      const raw = grammars.get(scopeName);
      if (!raw) return null;
      return parseRawGrammar(JSON.stringify(raw), scopeName + ".json");
    }
  });

  for (const asset of LANGUAGE_ASSETS) {
    const grammar = await registry.loadGrammar(asset.scope_name);
    if (!grammar) throw new Error("TextMate grammar is unavailable for " + asset.id);
    monaco.languages.setTokensProvider(asset.language_id, {
      getInitialState: () => new TextMateState(INITIAL),
      tokenize(line, state) {
        const previous = state instanceof TextMateState ? state.stack : INITIAL;
        const result = grammar.tokenizeLine(line, previous);
        return {
          endState: new TextMateState(result.ruleStack),
          tokens: result.tokens.map((token) => ({
            startIndex: token.startIndex,
            scopes: tokenClass(token.scopes)
          }))
        };
      }
    });
  }
}

class TextMateState implements monaco.languages.IState {
  constructor(readonly stack: StateStack) {}

  clone(): monaco.languages.IState {
    return new TextMateState(this.stack);
  }

  equals(other: monaco.languages.IState): boolean {
    return other instanceof TextMateState && this.stack.equals(other.stack);
  }
}

function grammarFor(asset: LanguageAsset): IRawGrammar {
  const escaped = asset.keywords
    .map((keyword) => keyword.replace(/[.*+?^$()|[\]{}\\]/g, "\\$&"))
    .join("|");
  const raw = {
    scopeName: asset.scope_name,
    patterns: [
      { include: "#comments" },
      { include: "#strings" },
      { include: "#numbers" },
      { include: "#keywords" }
    ],
    repository: {
      comments: {
        patterns: [
          { name: "comment.line.double-slash.kide", match: "//.*$" },
          {
            name: "comment.block.kide",
            begin: "/\\*",
            end: "\\*/"
          }
        ]
      },
      strings: {
        patterns: [
          {
            name: "string.quoted.double.kide",
            begin: "\"",
            end: "\"",
            patterns: [{ name: "constant.character.escape.kide", match: "\\\\." }]
          },
          {
            name: "string.quoted.single.kide",
            begin: "'",
            end: "'",
            patterns: [{ name: "constant.character.escape.kide", match: "\\\\." }]
          }
        ]
      },
      numbers: {
        patterns: [
          {
            name: "constant.numeric.kide",
            match: "(?<![A-Za-z0-9_])-?\\b(?:\\d+(?:\\.\\d+)?(?:[Ee]-?\\d+)?)\\b"
          }
        ]
      },
      keywords: {
        patterns: escaped
          ? [{ name: "keyword.control.kide", match: "\\b(?:" + escaped + ")\\b" }]
          : []
      }
    }
  };
  return raw as unknown as IRawGrammar;
}

function tokenClass(scopes: string[]): string {
  for (let index = scopes.length - 1; index >= 0; index -= 1) {
    const scope = scopes[index];
    if (scope.startsWith("comment")) return "comment";
    if (scope.startsWith("string")) return "string";
    if (scope.startsWith("constant.numeric")) return "number";
    if (scope.startsWith("keyword")) return "keyword";
  }
  return "";
}
