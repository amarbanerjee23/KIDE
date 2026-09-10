import React, { useRef, useEffect } from 'react';
import Editor, { useMonaco } from '@monaco-editor/react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { parseText } from '../../api/parse';

export const MonacoDslEditor: React.FC = () => {
  const activeFile = useActiveFile();
  const { updateFileContent, validationErrors, setParsedModel } = useEditorStore();
  const monaco = useMonaco();
  const editorRef = useRef<any>(null);
  const debounceTimerRef = useRef<any>(null);

  useEffect(() => {
    if (monaco) {
      monaco.editor.defineTheme('kide-dark', {
        base: 'vs-dark',
        inherit: true,
        rules: [],
        colors: {
          'editor.background': '#0a0a1a',
        }
      });
    }
  }, [monaco]);

  const parseDebounced = (content: string, language: string, fileId: string) => {
    if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current);
    debounceTimerRef.current = setTimeout(async () => {
      const result = await parseText(language, content);
      setParsedModel(fileId, result.ast);
    }, 300);
  };

  const handleEditorChange = (value: string | undefined) => {
    if (value !== undefined && activeFile) {
      updateFileContent(activeFile.id, value);
      if (activeFile.language !== 'json') {
        parseDebounced(value, activeFile.language, activeFile.id);
      }
    }
  };

  useEffect(() => {
    if (monaco && activeFile && editorRef.current) {
      const model = editorRef.current.getModel();
      const errors = validationErrors[activeFile.id] || [];
      const markers = errors.map(err => ({
        severity: err.severity === 'error' ? monaco.MarkerSeverity.Error : monaco.MarkerSeverity.Warning,
        message: err.message,
        startLineNumber: err.line || 1,
        startColumn: err.column || 1,
        endLineNumber: err.line || 1,
        endColumn: (err.column || 1) + 1,
      }));
      monaco.editor.setModelMarkers(model, 'kide', markers);
    }
  }, [monaco, activeFile, validationErrors]);

  if (!activeFile) {
    return <div className="h-full flex items-center justify-center text-gray-500 bg-background">Select a file to edit</div>;
  }

  return (
    <Editor
      height="100%"
      language={activeFile.language}
      theme="kide-dark"
      value={activeFile.content}
      onChange={handleEditorChange}
      onMount={(editor) => { editorRef.current = editor; }}
      options={{
        minimap: { enabled: false },
        fontSize: 14,
        fontFamily: "'Fira Code', 'JetBrains Mono', monospace",
        scrollBeyondLastLine: false,
        padding: { top: 16 }
      }}
    />
  );
};
