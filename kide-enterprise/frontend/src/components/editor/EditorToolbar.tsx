import React, { useState } from 'react';
import { Play, CheckCircle, Download, FileJson } from 'lucide-react';
import Button from '../common/Button';
import { useEditorStore } from '../../stores/editorStore';
import { transformApi } from '../../api/transform';
import { exportApi } from '../../api/export';

const EditorToolbar = () => {
  const { activityJson, setTransformResult, setValidationErrors, setIsTransforming, transformResult } = useEditorStore();
  const [isExporting, setIsExporting] = useState(false);

  const handleFormat = () => {
    try {
      const parsed = JSON.parse(activityJson);
      useEditorStore.getState().setActivityJson(JSON.stringify(parsed, null, 2));
      setValidationErrors([]);
    } catch (e) {
      setValidationErrors([{ field: 'json', message: 'Invalid JSON format' }]);
    }
  };

  const handleValidate = () => {
    try {
      JSON.parse(activityJson);
      setValidationErrors([]);
    } catch (e) {
      setValidationErrors([{ field: 'json', message: 'Invalid JSON format' }]);
    }
  };

  const handleTransform = async () => {
    try {
      setIsTransforming(true);
      const parsed = JSON.parse(activityJson);
      const result = await transformApi.transformActivity(parsed);
      setTransformResult(result);
      setValidationErrors(result.validation_errors.map(err => ({ field: 'root', message: err })));
    } catch (e: any) {
      setValidationErrors([{ field: 'transform', message: e.message || 'Transform failed' }]);
    } finally {
      setIsTransforming(false);
    }
  };

  const handleExport = async (type: 'json' | 'dsl' | 'python') => {
    if (!transformResult?.model) return;
    setIsExporting(true);
    try {
      let content = '';
      if (type === 'json') content = await exportApi.exportJson(transformResult.model);
      else if (type === 'dsl') content = await exportApi.exportDsl(transformResult.model);
      else if (type === 'python') content = await exportApi.exportPython(transformResult.model);
      
      const blob = new Blob([content], { type: 'text/plain' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export.${type}`;
      a.click();
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <div className="h-14 bg-surface border-b border-accent flex items-center justify-between px-4">
      <div className="flex items-center space-x-2">
        <Button variant="ghost" size="sm" onClick={handleFormat}>
          <FileJson className="w-4 h-4 mr-2" /> Format
        </Button>
        <Button variant="ghost" size="sm" onClick={handleValidate}>
          <CheckCircle className="w-4 h-4 mr-2 text-green-500" /> Validate
        </Button>
      </div>
      <div className="flex items-center space-x-3">
        <Button size="sm" onClick={handleTransform}>
          <Play className="w-4 h-4 mr-2" /> Transform
        </Button>
        <div className="relative group">
          <Button variant="secondary" size="sm" disabled={!transformResult || isExporting}>
            <Download className="w-4 h-4 mr-2" /> Export
          </Button>
          {transformResult && !isExporting && (
            <div className="absolute right-0 mt-1 w-32 bg-surface border border-accent rounded-md shadow-lg hidden group-hover:block z-50">
              <button onClick={() => handleExport('json')} className="w-full text-left px-4 py-2 text-sm hover:bg-accent hover:text-white">JSON</button>
              <button onClick={() => handleExport('dsl')} className="w-full text-left px-4 py-2 text-sm hover:bg-accent hover:text-white">DSL</button>
              <button onClick={() => handleExport('python')} className="w-full text-left px-4 py-2 text-sm hover:bg-accent hover:text-white">Python</button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default EditorToolbar;

