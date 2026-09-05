import React from 'react';
import Editor, { useMonaco } from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';

interface MonacoDslEditorProps {
  value: string;
  onChange: (value: string) => void;
  readOnly?: boolean;
}

const MonacoDslEditor: React.FC<MonacoDslEditorProps> = ({ value, onChange, readOnly = false }) => {
  const monaco = useMonaco();
  const validationErrors = useEditorStore(state => state.validationErrors);

  React.useEffect(() => {
    if (monaco && validationErrors.length > 0) {
      const markers = validationErrors.map(err => ({
        message: err.message,
        severity: monaco.MarkerSeverity.Error,
        startLineNumber: 1,
        startColumn: 1,
        endLineNumber: 1,
        endColumn: 1,
      }));
      // mock apply markers
    }
  }, [monaco, validationErrors]);

  return (
    <div className="w-full h-full border-r border-[#0f3460]">
      <Editor
        height="100%"
        defaultLanguage="json"
        theme="vs-dark"
        value={value}
        onChange={(val) => onChange(val || '')}
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

