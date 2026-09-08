import React from 'react';
import Editor, { useMonaco } from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';

interface MonacoDslEditorProps {
  readOnly?: boolean;
}

const MonacoDslEditor: React.FC<MonacoDslEditorProps> = ({ readOnly = false }) => {
  const monaco = useMonaco();
  const { files, activeFileId, updateFileContent, validationErrors } = useEditorStore();

  const activeFile = files.find(f => f.id === activeFileId);

  React.useEffect(() => {
    if (monaco && validationErrors.length > 0) {
      // In a real app we would map validation errors back to specific lines based on the AST.
      // For now, we put an error at line 1.
      const model = monaco.editor.getModels()[0]; // Grab the active model
      if (model) {
        const markers = validationErrors.map(err => ({
          message: err.message,
          severity: monaco.MarkerSeverity.Error,
          startLineNumber: 1,
          startColumn: 1,
          endLineNumber: 1,
          endColumn: 1,
        }));
        monaco.editor.setModelMarkers(model, 'kide', markers);
      }
    } else if (monaco) {
      const model = monaco.editor.getModels()[0];
      if (model) {
        monaco.editor.setModelMarkers(model, 'kide', []);
      }
    }
  }, [monaco, validationErrors, activeFileId]);

  if (!activeFile) {
    return <div className="flex-1 flex items-center justify-center text-gray-500 bg-background">No file selected</div>;
  }

  // Get Monaco language from extension
  let editorLanguage = 'json';
  if (activeFile.name.endsWith('.dml')) editorLanguage = 'dmldsl';
  if (activeFile.name.endsWith('.operation')) editorLanguage = 'operationdsl';
  if (activeFile.name.endsWith('.capability')) editorLanguage = 'capabilitydsl';
  // Note: we haven't registered 'activitydsl' yet, so default to json for .activity
  if (activeFile.name.endsWith('.activity')) editorLanguage = 'json';

  return (
    <div className="flex-1 w-full h-full border-r border-[#0f3460] overflow-hidden">
      <Editor
        height="100%"
        language={editorLanguage}
        theme="vs-dark"
        value={activeFile.content}
        path={activeFile.name} // path helps Monaco separate models for different tabs
        onChange={(val) => updateFileContent(activeFile.id, val || '')}
        options={{
          minimap: { enabled: false },
          fontSize: 14,
          readOnly,
          wordWrap: 'on',
          scrollBeyondLastLine: false,
          formatOnPaste: true,
          padding: { top: 16 }
        }}
      />
    </div>
  );
};

export default MonacoDslEditor;
