import React, { useRef, useEffect, useState, useCallback } from 'react';
import Editor, { useMonaco } from '@monaco-editor/react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { registerKideLanguagePlatform } from '../../lib/kide-language/registry';
import { documentManager } from '../../lib/kide-language/documentManager';
import {
  kideLanguageService,
  registerNavigationHandler,
  BreadcrumbItem
} from '../../lib/kide-language/languageService';
import { Sparkles, CheckCircle2, AlertTriangle, XCircle, Code2, ChevronRight } from 'lucide-react';

export const MonacoDslEditor: React.FC = () => {
  const activeFile = useActiveFile();
  const fileCount = useEditorStore((state) => state.files.length);
  const projectId = useEditorStore((state) => state.projectId) || 1;
  const activeFileErrors = useEditorStore(
    (state) => (activeFile ? state.validationErrors[activeFile.id] : undefined)
  );
  const updateFileContent = useEditorStore((state) => state.updateFileContent);
  const setActiveFileId = useEditorStore((state) => state.setActiveFileId);
  const setSelectedNodeId = useEditorStore((state) => state.setSelectedNodeId);

  const monaco = useMonaco();
  const editorRef = useRef<any>(null);
  const [breadcrumbs, setBreadcrumbs] = useState<BreadcrumbItem[]>([]);
  const [cursorPos, setCursorPos] = useState({ line: 1, column: 1 });

  const activeFileRef = useRef(activeFile);
  useEffect(() => {
    activeFileRef.current = activeFile;
  }, [activeFile]);

  // 1. Initialize KIDE Language Platform & Theme
  useEffect(() => {
    if (monaco) {
      registerKideLanguagePlatform(monaco);

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
          { token: 'class', foreground: '4ec9b0', fontStyle: 'bold' },
          { token: 'type', foreground: '4ec9b0' },
          { token: 'annotation', foreground: 'c586c0' }
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

  // 2. Index workspace files on project load or file list change (NOT on every keystroke)
  useEffect(() => {
    const currentFiles = useEditorStore.getState().files;
    if (currentFiles && currentFiles.length > 0) {
      kideLanguageService.indexWorkspaceFiles(currentFiles);
    }
  }, [fileCount]);

  // 3. Register cross-file navigation handler
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

  // 4. Attach persistent model on active file change
  useEffect(() => {
    if (activeFile && editorRef.current && monaco) {
      documentManager.attachToEditor(
        editorRef.current,
        projectId,
        activeFile.id,
        activeFile.name,
        activeFile.content
      );

      // Trigger initial diagnostic check
      kideLanguageService.revalidateAndIndexFile(activeFile.id, activeFile.name, activeFile.content);

      // Compute initial breadcrumbs
      const initialBc = kideLanguageService.getBreadcrumbs(
        activeFile.language || activeFile.name,
        activeFile.content,
        1,
        1
      );
      setBreadcrumbs(initialBc);
    }
  }, [activeFile?.id, monaco, projectId]);

  // 5. Handle typing with incremental indexing and diagnostics (stable callback reference)
  const handleEditorChange = useCallback((value: string | undefined) => {
    const currentFile = activeFileRef.current;
    if (value !== undefined && currentFile && value !== currentFile.content) {
      updateFileContent(currentFile.id, value);
      kideLanguageService.onDocumentChanged(currentFile.id, currentFile.name, value);
    }
  }, [updateFileContent]);

  // 6. Manual Format Action
  const handleFormat = useCallback(() => {
    const currentFile = activeFileRef.current;
    if (!currentFile || !editorRef.current) return;
    const formatted = kideLanguageService.format(currentFile.content, currentFile.name);
    if (formatted && formatted !== currentFile.content) {
      updateFileContent(currentFile.id, formatted);
      const model = editorRef.current.getModel();
      if (model) {
        model.setValue(formatted);
      }
      kideLanguageService.revalidateAndIndexFile(currentFile.id, currentFile.name, formatted);
    }
  }, [updateFileContent]);

  if (!activeFile) {
    return (
      <div className="h-full flex flex-col items-center justify-center text-gray-500 bg-[#0d1117]">
        <Code2 className="w-12 h-12 mb-3 text-gray-600 animate-pulse" />
        <span className="text-sm">Select an artifact from the explorer or create a new file to open the editor.</span>
      </div>
    );
  }

  const activeErrors = activeFileErrors || [];
  const errorCount = activeErrors.filter((e: any) => e.severity === 'error').length;
  const warningCount = activeErrors.filter((e: any) => e.severity === 'warning').length;

  return (
    <div className="h-full flex flex-col bg-[#0d1117]">
      {/* Breadcrumb and Action Header */}
      <div className="h-8 border-b border-[#30363d] bg-[#161b22] px-3 flex items-center justify-between text-xs text-gray-400 select-none">
        {/* Breadcrumbs */}
        <div className="flex items-center space-x-1 overflow-hidden truncate">
          <span className="font-semibold text-gray-300">{activeFile.name}</span>
          {breadcrumbs.map((bc, idx) => (
            <React.Fragment key={idx}>
              <ChevronRight className="w-3.5 h-3.5 text-gray-600 flex-shrink-0" />
              <button
                className="hover:text-blue-400 transition-colors truncate"
                onClick={() => {
                  if (editorRef.current && bc.range) {
                    editorRef.current.revealLineInCenter(bc.range.startLine);
                    editorRef.current.setPosition({
                      lineNumber: bc.range.startLine,
                      column: bc.range.startColumn
                    });
                    editorRef.current.focus();
                  }
                }}
              >
                {bc.name}
              </button>
            </React.Fragment>
          ))}
        </div>

        {/* Toolbar status */}
        <div className="flex items-center space-x-3 flex-shrink-0">
          <button
            onClick={handleFormat}
            title="Format Document (Idempotent)"
            className="flex items-center space-x-1 px-2 py-0.5 rounded hover:bg-[#21262d] text-gray-300 hover:text-white transition-colors"
          >
            <Sparkles className="w-3.5 h-3.5 text-amber-400" />
            <span>Format</span>
          </button>

          {/* Diagnostics Status Indicator */}
          <div className="flex items-center space-x-2 pl-2 border-l border-[#30363d]">
            {errorCount === 0 && warningCount === 0 ? (
              <span className="flex items-center space-x-1 text-emerald-400">
                <CheckCircle2 className="w-3.5 h-3.5" />
                <span>Valid</span>
              </span>
            ) : (
              <span className="flex items-center space-x-2">
                {errorCount > 0 && (
                  <span className="flex items-center space-x-0.5 text-red-400">
                    <XCircle className="w-3.5 h-3.5" />
                    <span>{errorCount}</span>
                  </span>
                )}
                {warningCount > 0 && (
                  <span className="flex items-center space-x-0.5 text-amber-400">
                    <AlertTriangle className="w-3.5 h-3.5" />
                    <span>{warningCount}</span>
                  </span>
                )}
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Editor Body */}
      <div className="flex-1 min-h-0 relative">
        <Editor
          height="100%"
          language={activeFile.language}
          theme="kide-dark"
          path={documentManager.getUri(projectId, activeFile.id, activeFile.name).toString()}
          defaultValue={activeFile.content}
          keepCurrentModel={true}
          onChange={handleEditorChange}
          loading={
            <div className="h-full flex items-center justify-center text-gray-500 bg-[#0d1117] text-xs">
              Initializing KIDE Editor...
            </div>
          }
          onMount={(editor) => {
            editorRef.current = editor;

            // Attach persistent model
            documentManager.attachToEditor(editor, projectId, activeFile.id, activeFile.name, activeFile.content);

            // Re-validate document
            kideLanguageService.revalidateAndIndexFile(activeFile.id, activeFile.name, activeFile.content);

            // Cursor tracking with AST-exact node lookup
            let cursorDebounce: any = null;
            editor.onDidChangeCursorPosition((e) => {
              setCursorPos({ line: e.position.lineNumber, column: e.position.column });

              if (cursorDebounce) clearTimeout(cursorDebounce);
              cursorDebounce = setTimeout(() => {
                const currentFile = activeFileRef.current;
                if (!currentFile) return;
                const line = e.position.lineNumber;
                const col = e.position.column;

                // Exact AST lookup
                const node = kideLanguageService.findNodeAtPosition(
                  currentFile.language || currentFile.name,
                  currentFile.content,
                  line,
                  col
                );

                if (node && node.name) {
                  setSelectedNodeId(node.name);
                }

                // Update breadcrumb bar
                const bc = kideLanguageService.getBreadcrumbs(
                  currentFile.language || currentFile.name,
                  currentFile.content,
                  line,
                  col
                );
                setBreadcrumbs(bc);
              }, 150);
            });
          }}
          options={{
            minimap: { enabled: false },
            fontSize: 14,
            fontFamily: "'Fira Code', 'JetBrains Mono', 'Consolas', monospace",
            fontLigatures: true,
            scrollBeyondLastLine: false,
            padding: { top: 12, bottom: 12 },
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
      </div>

      {/* Editor Status Bar */}
      <div className="h-6 border-t border-[#30363d] bg-[#161b22] px-3 flex items-center justify-between text-[11px] text-gray-500 select-none">
        <div className="flex items-center space-x-3">
          <span>Ln {cursorPos.line}, Col {cursorPos.column}</span>
          <span>UTF-8</span>
          <span className="capitalize">{activeFile.language}</span>
        </div>
        <div className="flex items-center space-x-2">
          <span>KIDE Language Server (Native)</span>
        </div>
      </div>
    </div>
  );
};
