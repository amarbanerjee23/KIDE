import React, { useState } from 'react';
import { Play, CheckCircle, Download, FileJson, Sparkles } from 'lucide-react';
import Button from '../common/Button';
import { useEditorStore } from '../../stores/editorStore';
import { transformApi } from '../../api/transform';
import { exportApi } from '../../api/export';
import { validateApi } from '../../api/validate';

const EditorToolbar = () => {
  const { files, activeFileId, setTransformResult, setValidationErrors, setIsTransforming, transformResult } = useEditorStore();
  const [isExporting, setIsExporting] = useState(false);

  const activeFile = files.find(f => f.id === activeFileId);

  const handleFormat = () => {
    if (!activeFile) return;
    if (activeFile.language === 'json' || activeFile.name.endsWith('.json')) {
      try {
        const parsed = JSON.parse(activeFile.content);
        useEditorStore.getState().updateFileContent(activeFile.id, JSON.stringify(parsed, null, 2));
        setValidationErrors([]);
      } catch (e) {
        setValidationErrors([{ field: 'json', message: 'Invalid JSON format' }]);
      }
    }
  };

  const handleValidate = async () => {
    try {
      const semanticErrors = await validateApi.validateSemantic(files);
      const mappedErrors = semanticErrors.filter(e => e.fileId === activeFileId).map(e => ({
        field: 'semantic',
        message: e.message
      }));
      setValidationErrors(mappedErrors);
    } catch (e) {
      console.error(e);
    }
  };

  const handleTransform = async () => {
    try {
      setIsTransforming(true);
      
      // Find the main activity file to transform
      const activityFile = files.find(f => f.name.endsWith('.activity'));
      if (!activityFile) {
        throw new Error("No .activity file found to transform.");
      }
      
      // Parse activity JSON (currently we only support JSON payload in backend)
      // If it's pure DSL, we would need a parser. Let's assume it's JSON for now or fall back.
      let parsed;
      try {
        parsed = JSON.parse(activityFile.content);
      } catch (e) {
         throw new Error("Activity file is not valid JSON. Ensure it is formatted correctly.");
      }

      // Build Knowledge Base from other files
      const capabilities = [];
      const operations = [];
      for (const f of files) {
        if (f.name.endsWith('.capability')) {
          try {
            capabilities.push(JSON.parse(f.content));
          } catch(e) {}
        }
        if (f.name.endsWith('.operation')) {
          try {
            operations.push(JSON.parse(f.content));
          } catch(e) {}
        }
      }

      const knowledgeBase = {
        capabilities,
        operations
      };

      const result = await transformApi.transformActivity(parsed, knowledgeBase);
      setTransformResult(result);
      setValidationErrors(result.validation_errors?.map((err: any) => ({ field: 'root', message: err })) || []);
    } catch (e: any) {
      setValidationErrors([{ field: 'transform', message: e.message || 'Transform failed' }]);
    } finally {
      setIsTransforming(false);
    }
  };

  const handleAIGenerate = () => {
    // Mock Gemini Generate
    alert("Gemini AI generation would open a prompt here to generate DSL.");
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
    <div className="h-14 bg-surface border-b border-accent flex items-center justify-between px-4 relative">
      {/* Gemini Branding Badge */}
      <div className="absolute left-1/2 transform -translate-x-1/2 flex items-center text-xs font-semibold text-gray-400 bg-[#161b22] px-3 py-1 rounded-full border border-gray-700 shadow-sm pointer-events-none">
        <Sparkles className="w-3 h-3 mr-1.5 text-blue-400" />
        POWERED BY GOOGLE GEMINI
      </div>

      <div className="flex items-center space-x-2 z-10">
        <Button variant="ghost" size="sm" onClick={handleFormat}>
          <FileJson className="w-4 h-4 mr-2" /> Format
        </Button>
        <Button variant="ghost" size="sm" onClick={handleValidate}>
          <CheckCircle className="w-4 h-4 mr-2 text-green-500" /> Validate
        </Button>
        <Button variant="ghost" size="sm" onClick={handleAIGenerate} className="text-blue-400 hover:text-blue-300">
          <Sparkles className="w-4 h-4 mr-2" /> AI Generate
        </Button>
      </div>
      <div className="flex items-center space-x-3 z-10">
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
