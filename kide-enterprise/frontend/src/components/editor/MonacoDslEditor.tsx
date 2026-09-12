import React, { useRef, useEffect, useCallback } from 'react';
import Editor, { useMonaco } from '@monaco-editor/react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { setupMonacoLanguages } from '../../lib/monaco/setup';
import { validateActiveModel, registerNavigationHandler } from '../../lib/monaco/xtextLanguageService';
import { extractOutlineSymbols } from '../../utils/outlineExtractor';

export const MonacoDslEditor: React.FC = () => {
  const activeFile = useActiveFile();
  const { updateFileContent, setActiveFileId, setSelectedNodeId } = useEditorStore();
  const monaco = useMonaco();
  const editorRef = useRef<any>(null);
  const debounceTimerRef = useRef<any>(null);

  // Setup Monaco custom theme and register languages
  useEffect(() => {
    if (monaco) {
      setupMonacoLanguages();

      monaco.editor.defineTheme('kide-dark', {
        base: 'vs-dark',
        inherit: true,
        rules: [
          { token: 'keyword', foreground: '569cd6', fontStyle: 'bold' },
          { token: 'identifier', foreground: '9cdcfe' },
          { token: 'string', foreground: 'ce9178' },
          { token: 'number', foreground: 'b5cea8' },
          { token: 'comment', foreground: '6a9955', fontStyle: 'italic' },
          { token: 'operator', foreground: 'd4d4d4' },
          { token: 'brackets', foreground: 'ffd700' },
        ],
        colors: {
          'editor.background': '#0d1117',
          'editor.foreground': '#c9d1d9',
          'editorCursor.foreground': '#58a6ff',
          'editorLineHighlightBackground': '#161b22',
          'editorLineNumber.foreground': '#484f58',
          'editorLineNumber.activeForeground': '#e6edf3',
        }
      });
    }
  }, [monaco]);

  // Run real-time syntax & semantic validation
  const runValidation = useCallback(async (fileId: string, content: string, language: string) => {
    if (!monaco || !editorRef.current) return;
    const model = editorRef.current.getModel();
    if (!model) return;

    await validateActiveModel(fileId, content, language, monaco, model);
  }, [monaco]);

  // Handle typing with debounced validation
  const handleEditorChange = (value: string | undefined) => {
    if (value !== undefined && activeFile) {
      updateFileContent(activeFile.id, value);

      if (debounceTimerRef.current) clearTimeout(debounceTimerRef.current);
      debounceTimerRef.current = setTimeout(() => {
        runValidation(activeFile.id, value, activeFile.language);
      }, 350);
    }
  };

  // Run validation when active file changes or mounts
  useEffect(() => {
    if (activeFile && monaco && editorRef.current) {
      runValidation(activeFile.id, activeFile.content, activeFile.language);
    }
  }, [activeFile?.id, monaco, runValidation]);

  // Register cross-file navigation handler
  useEffect(() => {
    registerNavigationHandler((targetFileId, line, column) => {
      setActiveFileId(targetFileId);
      setTimeout(() => {
        if (editorRef.current) {
          editorRef.current.revealLineInCenter(line);
          editorRef.current.setPosition({ lineNumber: line, column });
          editorRef.current.focus();
        }
      }, 100);
    });
  }, [setActiveFileId]);

  if (!activeFile) {
    return (
      <div className="h-full flex flex-col items-center justify-center text-gray-500 bg-[#0d1117]">
        <span className="text-sm">Select a file from the explorer or create a new one to begin editing.</span>
      </div>
    );
  }

  return (
    <Editor
      height="100%"
      language={activeFile.language}
      theme="kide-dark"
      value={activeFile.content}
      onChange={handleEditorChange}
      onMount={(editor) => {
        editorRef.current = editor;
        if (activeFile && monaco) {
          runValidation(activeFile.id, activeFile.content, activeFile.language);
        }

        let cursorDebounce: any = null;
        editor.onDidChangeCursorPosition((e) => {
          if (cursorDebounce) clearTimeout(cursorDebounce);
          cursorDebounce = setTimeout(() => {
            if (!activeFile) return;
            const line = e.position.lineNumber;
            const symbols = extractOutlineSymbols(activeFile.content, activeFile.name);
            const findSymbolAtLine = (items: typeof symbols): string | null => {
              for (const item of items) {
                if (item.line && Math.abs(item.line - line) <= 3) {
                  return item.name;
                }
                if (item.children) {
                  const m = findSymbolAtLine(item.children);
                  if (m) return m;
                }
              }
              return null;
            };
            const match = findSymbolAtLine(symbols);
            if (match) {
              setSelectedNodeId(match);
            }
          }, 300);
        });
      }}
      options={{
        minimap: { enabled: false },
        fontSize: 14,
        fontFamily: "'Fira Code', 'JetBrains Mono', 'Consolas', monospace",
        fontLigatures: true,
        scrollBeyondLastLine: false,
        padding: { top: 16, bottom: 16 },
        glyphMargin: true,
        lightbulb: { enabled: true as any },
        quickSuggestions: { other: true, comments: true, strings: true },
        suggestOnTriggerCharacters: true,
        bracketPairColorization: { enabled: true },
        formatOnPaste: true,
        cursorBlinking: 'smooth',
        smoothScrolling: true,
        wordWrap: 'off',
      }}
    />
  );
};
