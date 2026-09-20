import * as monaco from "monaco-editor";
import { languageForPath, workspaceUri } from "./languageAssets";

interface RecordEntry {
  model: monaco.editor.ITextModel;
  subscription: monaco.IDisposable;
  suppress: boolean;
}

export class MonacoWorkspace {
  private readonly records = new Map<string, RecordEntry>();

  constructor(
    private readonly onChange: (path: string, value: string) => void
  ) {}

  ensure(path: string, content: string): monaco.editor.ITextModel {
    const current = this.records.get(path);
    const languageId = languageForPath(path)?.language_id ?? "plaintext";
    if (current) {
      if (current.model.getLanguageId() !== languageId) {
        monaco.editor.setModelLanguage(current.model, languageId);
      }
      this.sync(path, content);
      return current.model;
    }

    const model = monaco.editor.createModel(
      content,
      languageId,
      monaco.Uri.parse(workspaceUri(path))
    );
    const record: RecordEntry = {
      model,
      subscription: { dispose() {} },
      suppress: false
    };
    record.subscription = model.onDidChangeContent(() => {
      if (!record.suppress) this.onChange(path, model.getValue());
    });
    this.records.set(path, record);
    return model;
  }

  sync(path: string, content: string): void {
    const record = this.records.get(path);
    if (!record || record.model.getValue() === content) return;
    record.suppress = true;
    try {
      record.model.setValue(content);
    } finally {
      record.suppress = false;
    }
  }

  get(path: string): monaco.editor.ITextModel | undefined {
    return this.records.get(path)?.model;
  }

  getByUri(uri: string): monaco.editor.ITextModel | undefined {
    return monaco.editor.getModel(monaco.Uri.parse(uri)) ?? undefined;
  }

  text(path: string): string | undefined {
    return this.records.get(path)?.model.getValue();
  }

  dispose(): void {
    for (const record of this.records.values()) {
      record.subscription.dispose();
      record.model.dispose();
    }
    this.records.clear();
  }
}
