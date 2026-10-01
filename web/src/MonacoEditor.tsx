import { useEffect, useRef } from "react";
import * as monaco from "monaco-editor";
import EditorWorker from "monaco-editor/esm/vs/editor/editor.worker?worker";
import type { MonacoLspController } from "./monacoLsp";
import type { MonacoWorkspace } from "./monacoWorkspace";

(self as unknown as {
  MonacoEnvironment?: { getWorker(): Worker };
}).MonacoEnvironment = {
  getWorker: () => new EditorWorker()
};

interface Props {
  value: string;
  path: string;
  workspace: MonacoWorkspace;
  lsp?: MonacoLspController;
  readOnly?: boolean;
  revealRange?: monaco.Range;
  theme?: "vs" | "vs-dark";
  onCursorChange?(line: number, column: number): void;
}

export function MonacoEditor({
  value,
  path,
  workspace,
  lsp,
  readOnly = false,
  revealRange,
  theme = "vs",
  onCursorChange
}: Props) {
  const host = useRef<HTMLDivElement>(null);
  const editor = useRef<monaco.editor.IStandaloneCodeEditor | undefined>(undefined);

  useEffect(() => {
    if (!host.current) return;
    const model = workspace.ensure(path, value);
    const attachment = lsp?.attachModel(model);
    const instance = monaco.editor.create(host.current, {
      model,
      theme,
      automaticLayout: true,
      readOnly,
      fontFamily:
        '"SFMono-Regular", Consolas, "Liberation Mono", Menlo, monospace',
      fontSize: 14,
      fontLigatures: true,
      lineHeight: 21,
      tabSize: 2,
      insertSpaces: true,
      detectIndentation: true,
      wordWrap: "off",
      wordWrapColumn: 120,
      scrollBeyondLastLine: false,
      smoothScrolling: true,
      mouseWheelZoom: true,
      cursorBlinking: "smooth",
      cursorSmoothCaretAnimation: "on",
      renderLineHighlight: "all",
      renderWhitespace: "selection",
      renderControlCharacters: false,
      glyphMargin: true,
      lineNumbersMinChars: 4,
      minimap: {
        enabled: true,
        showSlider: "mouseover",
        renderCharacters: false,
        maxColumn: 100
      },
      stickyScroll: { enabled: true },
      folding: true,
      foldingHighlight: true,
      showFoldingControls: "mouseover",
      bracketPairColorization: { enabled: true },
      guides: {
        indentation: true,
        highlightActiveIndentation: true,
        bracketPairs: true,
        bracketPairsHorizontal: true,
        highlightActiveBracketPair: true
      },
      quickSuggestions: {
        other: true,
        comments: false,
        strings: false
      },
      suggestOnTriggerCharacters: true,
      acceptSuggestionOnEnter: "on",
      tabCompletion: "on",
      snippetSuggestions: "inline",
      parameterHints: { enabled: true },
      inlineSuggest: { enabled: true },
      hover: { enabled: true, delay: 250 },
      links: true,
      codeLens: true,
      contextmenu: true,
      matchBrackets: "always",
      selectionHighlight: true,
      formatOnPaste: true,
      formatOnType: true,
      multiCursorModifier: "alt",
      renderValidationDecorations: "on",
      accessibilitySupport: "auto",
      ariaLabel: `KIDE editor for ${path}`
    });
    editor.current = instance;

    const cursorSubscription = instance.onDidChangeCursorPosition((event) => {
      onCursorChange?.(event.position.lineNumber, event.position.column);
    });
    const initial = instance.getPosition();
    if (initial) onCursorChange?.(initial.lineNumber, initial.column);

    return () => {
      cursorSubscription.dispose();
      attachment?.dispose();
      instance.dispose();
      editor.current = undefined;
    };
  }, [path, readOnly, workspace, lsp]);

  useEffect(() => {
    monaco.editor.setTheme(theme);
  }, [theme]);

  useEffect(() => {
    workspace.sync(path, value);
  }, [path, value, workspace]);

  useEffect(() => {
    const instance = editor.current;
    if (!instance || !revealRange) return;
    instance.setSelection(revealRange);
    instance.revealRangeInCenter(revealRange);
    instance.focus();
  }, [revealRange]);

  useEffect(() => {
    const listener = (event: Event) => {
      const actionId = (event as CustomEvent<{ actionId?: string }>).detail?.actionId;
      const instance = editor.current;
      if (!actionId || !instance) return;
      void instance.getAction(actionId)?.run().then(() => instance.focus());
    };
    window.addEventListener("kide:editor-action", listener);
    return () => window.removeEventListener("kide:editor-action", listener);
  }, []);

  return (
    <div className="monaco-shell">
      <div className="editor-host" data-testid="monaco-editor" ref={host} />
    </div>
  );
}
