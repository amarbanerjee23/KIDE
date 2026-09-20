import { useEffect, useRef } from "react";
import * as monaco from "monaco-editor";
import EditorWorker from "monaco-editor/esm/vs/editor/editor.worker?worker";

(self as unknown as {
  MonacoEnvironment?: { getWorker(): Worker };
}).MonacoEnvironment = {
  getWorker: () => new EditorWorker()
};

interface Props {
  value: string;
  path: string;
  readOnly?: boolean;
  onChange(value: string): void;
}

export function MonacoEditor({ value, path, readOnly = false, onChange }: Props) {
  const host = useRef<HTMLDivElement>(null);
  const editor = useRef<monaco.editor.IStandaloneCodeEditor | undefined>(undefined);
  const onChangeRef = useRef(onChange);
  onChangeRef.current = onChange;

  useEffect(() => {
    if (!host.current) return;
    const model = monaco.editor.createModel(
      value,
      "plaintext",
      monaco.Uri.parse(`file:///${path}`)
    );
    const instance = monaco.editor.create(host.current, {
      model,
      automaticLayout: true,
      minimap: { enabled: false },
      readOnly,
      fontSize: 14,
      tabSize: 2,
      wordWrap: "on",
      scrollBeyondLastLine: false
    });
    const subscription = instance.onDidChangeModelContent(() => {
      onChangeRef.current(instance.getValue());
    });
    editor.current = instance;
    return () => {
      subscription.dispose();
      instance.dispose();
      model.dispose();
      editor.current = undefined;
    };
  }, [path, readOnly]);

  useEffect(() => {
    const instance = editor.current;
    if (instance && instance.getValue() !== value) instance.setValue(value);
  }, [value]);

  return <div className="editor-host" data-testid="monaco-editor" ref={host} />;
}
