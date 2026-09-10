import React, { useEffect, useState } from 'react';
import { Play, CheckCircle, Code2, Save, Layers, Loader2 } from 'lucide-react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { parseText } from '../../api/parse';
import { transformData } from '../../api/transform';
import { validateSemantic } from '../../api/validate';
import { projectsApi } from '../../api/projects';

export const EditorToolbar: React.FC = () => {
  const activeFile = useActiveFile();
  const { 
    files, projectId, projectName, dirtyFileIds,
    setIsParsing, setParsedModel, 
    setIsTransforming, setTransformResult, 
    setValidationErrors, markFileSaved,
    isParsing, isTransforming
  } = useEditorStore();

  const [isSaving, setIsSaving] = useState(false);
  const isCurrentFileDirty = activeFile ? dirtyFileIds.includes(activeFile.id) : false;

  const handleSave = async () => {
    if (!activeFile) return;
    setIsSaving(true);
    try {
      if (projectId) {
        await projectsApi.updateFile(projectId, activeFile.id, {
          content: activeFile.content,
          filename: activeFile.name
        });
      }
      markFileSaved(activeFile.id);
    } catch (err) {
      console.error('Failed to save file:', err);
    } finally {
      setIsSaving(false);
    }
  };

  // Keyboard shortcut Ctrl+S
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if ((e.ctrlKey || e.metaKey) && e.key === 's') {
        e.preventDefault();
        handleSave();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [activeFile, projectId]);

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
    
    let payload: any = null;
    
    if (activeFile.language === 'json') {
      try {
        payload = JSON.parse(activeFile.content);
      } catch (e) {
        setValidationErrors(activeFile.id, [{ message: 'Invalid JSON', severity: 'error' }]);
        setIsTransforming(false);
        return;
      }
    } else if (activeFile.name.endsWith('.activity')) {
      const result = await parseText(activeFile.language, activeFile.content);
      setParsedModel(activeFile.id, result.ast);
      if (result.errors.some(e => e.severity === 'error')) {
        setValidationErrors(activeFile.id, result.errors);
        setIsTransforming(false);
        return;
      }
      payload = result.ast;
    } else {
      // If user is on .cap, .op, or .dml, find an activity diagram or do workspace transform
      const actFile = files.find(f => f.name.endsWith('.activity') || f.name.endsWith('.json'));
      if (actFile) {
        return handleTransformWorkspace();
      } else {
        setValidationErrors(activeFile.id, [{ 
          message: 'To synthesize an MNC-ML model, please select or create an Activity Diagram (.activity or .json), or run Workspace Transform.', 
          severity: 'warning' 
        }]);
        setIsTransforming(false);
        return;
      }
    }

    if (payload) {
      const result = await transformData(payload);
      setTransformResult(result);
      const errors = (result.validation_errors || []).map(e => ({ message: e, severity: 'error' as const }));
      const warnings = (result.warnings || []).map(w => ({ message: w, severity: 'warning' as const }));
      setValidationErrors(activeFile.id, [...errors, ...warnings]);
    }
    
    setIsTransforming(false);
  };

  const handleTransformWorkspace = async () => {
    if (files.length === 0) return;
    setIsTransforming(true);

    const workspacePayload: Record<string, string> = {};
    for (const f of files) {
      workspacePayload[f.name] = f.content;
    }

    const result = await transformData({ workspace: workspacePayload });
    setTransformResult(result);
    
    const errors = (result.validation_errors || []).map(e => ({ message: e, severity: 'error' as const }));
    const warnings = (result.warnings || []).map(w => ({ message: w, severity: 'warning' as const }));
    
    const targetFileId = activeFile ? activeFile.id : (files[0]?.id || '');
    if (targetFileId) {
      setValidationErrors(targetFileId, [...errors, ...warnings]);
    }

    setIsTransforming(false);
  };

  const handleValidate = async () => {
    const errors = await validateSemantic(files);
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
    <div className="flex items-center justify-between px-4 py-2 bg-[#161b22] border-b border-gray-800">
      {/* Left: Actions */}
      <div className="flex items-center gap-2">
        <button
          onClick={handleSave}
          disabled={!activeFile || isSaving}
          className={`flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-semibold rounded transition-all ${
            isCurrentFileDirty
              ? 'bg-amber-500 hover:bg-amber-400 text-gray-950 font-bold animate-pulse'
              : 'bg-gray-800 hover:bg-gray-700 text-gray-300'
          } disabled:opacity-40`}
          title="Save File (Ctrl+S)"
        >
          {isSaving ? <Loader2 size={14} className="animate-spin" /> : <Save size={14} />}
          <span>{isSaving ? 'Saving...' : isCurrentFileDirty ? 'Save *' : 'Saved'}</span>
        </button>

        <div className="h-4 w-[1px] bg-gray-700 mx-1" />

        <button
          onClick={handleParse}
          disabled={!activeFile || isParsing}
          className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-white bg-blue-600 rounded hover:bg-blue-500 disabled:opacity-40 transition-colors"
          title="Parse current DSL into Abstract Syntax Tree"
        >
          {isParsing ? <Loader2 size={14} className="animate-spin" /> : <Code2 size={14} />}
          <span>Parse</span>
        </button>

        <button
          onClick={handleTransform}
          disabled={!activeFile || isTransforming}
          className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-white bg-emerald-600 rounded hover:bg-emerald-500 disabled:opacity-40 shadow transition-colors"
          title="Synthesize Activity into Supervisory MNC Model"
        >
          {isTransforming ? <Loader2 size={14} className="animate-spin" /> : <Play size={14} />}
          <span>Transform</span>
        </button>

        <button
          onClick={handleTransformWorkspace}
          disabled={files.length === 0 || isTransforming}
          className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-purple-300 bg-purple-950/60 hover:bg-purple-900/60 border border-purple-800 rounded disabled:opacity-40 transition-colors"
          title="Transform all project DSL files together (Multi-DSL Synthesis)"
        >
          <Layers size={14} />
          <span>Workspace Transform</span>
        </button>

        <button
          onClick={handleValidate}
          className="flex items-center gap-1.5 px-2.5 py-1.5 text-xs font-medium text-gray-300 bg-gray-800 rounded hover:bg-gray-700 border border-gray-700 transition-colors"
          title="Cross-validate all project files"
        >
          <CheckCircle size={14} />
          <span>Validate All</span>
        </button>
      </div>

      {/* Right: Project / Active File Info */}
      <div className="flex items-center gap-2 text-xs text-gray-400">
        {projectName && (
          <>
            <span className="font-semibold text-gray-300">{projectName}</span>
            <span>/</span>
          </>
        )}
        {activeFile ? (
          <span className="text-gray-200 font-mono bg-gray-800/80 px-2 py-0.5 rounded">
            {activeFile.name}
          </span>
        ) : (
          <span className="italic text-gray-500">No file selected</span>
        )}
      </div>
    </div>
  );
};
