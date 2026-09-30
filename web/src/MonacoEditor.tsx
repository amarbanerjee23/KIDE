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
}

export function MonacoEditor({
  value,
  path,
  workspace,
  lsp,
  readOnly = false,
  revealRange
}: Props) {
  const host = useRef<HTMLDivElement>(null);
  const editor = useRef<monaco.editor.IStandaloneCodeEditor | undefined>(undefined);

  useEffect(() => {
    if (!host.current) return;
    const model = workspace.ensure(path, value);
    const attachment = lsp?.attachModel(model);
    const instance = monaco.editor.create(host.current, {
      model,
      automaticLayout: true,
      minimap: { enabled: false },
      readOnly,
      fontSize: 14,
      tabSize: 2,
      wordWrap: "on",
      scrollBeyondLastLine: false,
      quickSuggestions: true,
      suggestOnTriggerCharacters: true,
      folding: true
    });
    editor.current = instance;
    return () => {
      attachment?.dispose();
      instance.dispose();
      editor.current = undefined;
    };
  }, [path, readOnly, workspace, lsp]);

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

  async function runEditorAction(actionId: string) {
    const instance = editor.current;
    if (!instance) return;
    await instance.getAction(actionId)?.run();
    instance.focus();
  }

  return (
    <div className="monaco-shell">
      <div className="monaco-commandbar" aria-label="Text editor commands">
        <button onClick={() => void runEditorAction("editor.action.triggerSuggest")}>
          Completion
        </button>
        <button onClick={() => void runEditorAction("editor.action.revealDefinition")}>
          Definition
        </button>
        <button onClick={() => void runEditorAction("editor.action.referenceSearch.trigger")}>
          References
        </button>
        <button
          disabled={readOnly}
          onClick={() => void runEditorAction("editor.action.rename")}
        >
          Rename
        </button>
        <button
          disabled={readOnly}
          onClick={() => void runEditorAction("editor.action.quickFix")}
        >
          Quick Fix
        </button>
        <button
          disabled={readOnly}
          onClick={() => void runEditorAction("editor.action.formatDocument")}
        >
          Format
        </button>
      </div>
      <div className="editor-host" data-testid="monaco-editor" ref={host} />
    </div>
  );
}
