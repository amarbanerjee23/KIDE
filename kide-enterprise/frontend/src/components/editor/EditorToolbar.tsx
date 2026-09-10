import React from 'react';
import { Play, CheckCircle, Code2 } from 'lucide-react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { parseText } from '../../api/parse';
import { transformData } from '../../api/transform';
import { validateSemantic } from '../../api/validate';

export const EditorToolbar: React.FC = () => {
  const activeFile = useActiveFile();
  const { files, setIsParsing, setParsedModel, setIsTransforming, setTransformResult, setValidationErrors } = useEditorStore();

  const handleParse = async () => {
    if (!activeFile) return;
    setIsParsing(true);
    const result = await parseText(activeFile.language, activeFile.content);
    setParsedModel(activeFile.id, result.ast);
    setValidationErrors(activeFile.id, result.errors);
    setIsParsing(false);
  };

  const handleTransform = async () => {
    if (!activeFile) return;
    setIsTransforming(true);
    
    let payload = null;
    
    if (activeFile.language === 'json') {
      try {
        payload = JSON.parse(activeFile.content);
      } catch (e) {
        setValidationErrors(activeFile.id, [{ message: 'Invalid JSON', severity: 'error' }]);
        setIsTransforming(false);
        return;
      }
    } else {
      const result = await parseText(activeFile.language, activeFile.content);
      setParsedModel(activeFile.id, result.ast);
      if (result.errors.some(e => e.severity === 'error')) {
        setValidationErrors(activeFile.id, result.errors);
        setIsTransforming(false);
        return; // Don't transform if parsing failed
      }
      payload = result.ast;
    }

    if (payload) {
      const result = await transformData(payload);
      setTransformResult(result);
      const errors = result.validation_errors.map(e => ({ message: e, severity: 'error' as const }));
      const warnings = result.warnings.map(w => ({ message: w, severity: 'warning' as const }));
      setValidationErrors(activeFile.id, [...errors, ...warnings]);
    }
    
    setIsTransforming(false);
  };

  const handleValidate = async () => {
    const errors = await validateSemantic(files);
    // Group by fileId
    const grouped: Record<string, any[]> = {};
    errors.forEach(e => {
      const id = e.fileId || (activeFile ? activeFile.id : '');
      if (!grouped[id]) grouped[id] = [];
      grouped[id].push(e);
    });
    
    Object.keys(grouped).forEach(id => {
      setValidationErrors(id, grouped[id]);
    });
  };

  return (
    <div className="flex items-center justify-between px-4 py-2 bg-surface border-b border-accent">
      <div className="flex gap-2">
        <button
          onClick={handleParse}
          disabled={!activeFile}
          className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-white bg-blue-600 rounded hover:bg-blue-700 disabled:opacity-50"
        >
          <Code2 size={16} />
          Parse
        </button>
        <button
          onClick={handleTransform}
          disabled={!activeFile}
          className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-white bg-green-600 rounded hover:bg-green-700 disabled:opacity-50"
        >
          <Play size={16} />
          Transform
        </button>
        <button
          onClick={handleValidate}
          className="flex items-center gap-2 px-3 py-1.5 text-sm font-medium text-gray-300 bg-gray-700 rounded hover:bg-gray-600"
        >
          <CheckCircle size={16} />
          Validate All
        </button>
      </div>
    </div>
  );
};
