import React, { useState, useEffect, useMemo } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';
import { FileItem } from '../../types/models';
import { 
  ChevronRight, ChevronDown, Database, Cpu, Settings, 
  Activity, FileCode2, Pin, PinOff, MoreVertical, Trash2, 
  Columns, Network, AlertTriangle, AlertCircle,
  ExternalLink, Sparkles
} from 'lucide-react';

interface ProjectExplorerProps {
  onOpenNewArtifactModal: () => void;
}

export const ProjectExplorer: React.FC<ProjectExplorerProps> = ({ onOpenNewArtifactModal }) => {
  const { 
    files, activeFileId, dirtyFileIds, validationErrors, projectId,
    setActiveFileId, setActiveView, setSplitMode, removeFile
  } = useEditorStore();

  // Collapsible section state persisted per project
  const storageKey = `kide_explorer_expanded_${projectId || 'default'}`;
  const [expandedSections, setExpandedSections] = useState<Record<string, boolean>>(() => {
    try {
      const saved = localStorage.getItem(storageKey);
      if (saved) return JSON.parse(saved);
    } catch {}
    return {
      'data-models': true,
      'capabilities-ops': true,
      'supervisory-workflow': true,
      'state-machines': true,
      'generated': false,
      'knowledge': false,
      'pinned': true
    };
  });

  // Pinned files persisted per project
  const pinnedStorageKey = `kide_pinned_${projectId || 'default'}`;
  const [pinnedFileIds, setPinnedFileIds] = useState<string[]>(() => {
    try {
      const saved = localStorage.getItem(pinnedStorageKey);
      if (saved) return JSON.parse(saved);
    } catch {}
    return [];
  });

  // Context menu state
  const [contextMenu, setContextMenu] = useState<{
    file: FileItem;
    x: number;
    y: number;
  } | null>(null);

  // Delete confirm modal state
  const [deleteConfirmFile, setDeleteConfirmFile] = useState<FileItem | null>(null);

  useEffect(() => {
    try {
      localStorage.setItem(storageKey, JSON.stringify(expandedSections));
    } catch {}
  }, [expandedSections, storageKey]);

  useEffect(() => {
    try {
      localStorage.setItem(pinnedStorageKey, JSON.stringify(pinnedFileIds));
    } catch {}
  }, [pinnedFileIds, pinnedStorageKey]);

  // Close context menu on outside click
  useEffect(() => {
    const handleOutsideClick = () => setContextMenu(null);
    window.addEventListener('click', handleOutsideClick);
    return () => window.removeEventListener('click', handleOutsideClick);
  }, []);

  const toggleSection = (sectionId: string) => {
    setExpandedSections(prev => ({
      ...prev,
      [sectionId]: !prev[sectionId]
    }));
  };

  const togglePin = (fileId: string) => {
    setPinnedFileIds(prev => 
      prev.includes(fileId) ? prev.filter(id => id !== fileId) : [...prev, fileId]
    );
    setContextMenu(null);
  };

  // Group files by domain model
  const { dataModels, capabilitiesOps, workflows, stateMachines, otherFiles } = useMemo(() => {
    const dm: FileItem[] = [];
    const co: FileItem[] = [];
    const wf: FileItem[] = [];
    const sm: FileItem[] = [];
    const ot: FileItem[] = [];

    for (const f of files) {
      const name = f.name.toLowerCase();
      if (name.endsWith('.dml')) {
        dm.push(f);
      } else if (name.endsWith('.cap') || name.endsWith('.capability') || name.endsWith('.op') || name.endsWith('.operation')) {
        co.push(f);
      } else if (name.endsWith('.activity') || name.endsWith('.json')) {
        wf.push(f);
      } else if (name.endsWith('.mnc')) {
        sm.push(f);
      } else {
        ot.push(f);
      }
    }
    return { dataModels: dm, capabilitiesOps: co, workflows: wf, stateMachines: sm, otherFiles: ot };
  }, [files]);

  const pinnedFiles = useMemo(() => {
    return files.filter(f => pinnedFileIds.includes(f.id));
  }, [files, pinnedFileIds]);

  const handleSelectArtifact = (file: FileItem) => {
    setActiveFileId(file.id);
    const name = file.name.toLowerCase();
    if (name.endsWith('.activity') || name.endsWith('.json')) {
      setActiveView('workflow');
    } else if (name.endsWith('.mnc')) {
      setActiveView('statemachine');
    } else {
      setActiveView('editor');
    }
  };

  const handleOpenInSplit = (file: FileItem) => {
    setActiveFileId(file.id);
    setSplitMode(true);
    setContextMenu(null);
  };

  const handleShowDependencies = (file: FileItem) => {
    setActiveFileId(file.id);
    setActiveView('knowledgegraph');
    setContextMenu(null);
  };

  const handleDeleteFile = async (file: FileItem) => {
    if (!projectId) return;
    try {
      await projectsApi.deleteFile(projectId, file.id);
      removeFile(file.id);
      setDeleteConfirmFile(null);
    } catch (err) {
      console.error('Failed to delete file:', err);
    }
  };

  const getFileIcon = (filename: string) => {
    const lower = filename.toLowerCase();
    if (lower.endsWith('.dml')) return <Database size={13} className="text-rose-400 shrink-0" />;
    if (lower.endsWith('.cap') || lower.endsWith('.capability')) return <Cpu size={13} className="text-purple-400 shrink-0" />;
    if (lower.endsWith('.op') || lower.endsWith('.operation')) return <Settings size={13} className="text-amber-400 shrink-0" />;
    if (lower.endsWith('.activity') || lower.endsWith('.json')) return <Activity size={13} className="text-emerald-400 shrink-0" />;
    if (lower.endsWith('.mnc')) return <Settings size={13} className="text-blue-400 shrink-0" />;
    return <FileCode2 size={13} className="text-gray-400 shrink-0" />;
  };

  const getValidationIcon = (fileId: string) => {
    const errs = validationErrors[fileId] || [];
    if (errs.length === 0) return null;
    const hasError = errs.some(e => e.severity === 'error');
    if (hasError) {
      return (
        <span title={`${errs.length} errors`} className="text-rose-400 flex items-center">
          <AlertCircle size={11} />
        </span>
      );
    }
    return (
      <span title={`${errs.length} warnings`} className="text-amber-400 flex items-center">
        <AlertTriangle size={11} />
      </span>
    );
  };

  const renderFileRow = (file: FileItem, isPinnedContext: boolean = false) => {
    const isActive = activeFileId === file.id;
    const isDirty = dirtyFileIds.includes(file.id);
    const isPinned = pinnedFileIds.includes(file.id);

    return (
      <div
        key={`${isPinnedContext ? 'pinned-' : ''}${file.id}`}
        onClick={() => handleSelectArtifact(file)}
        onContextMenu={(e) => {
          e.preventDefault();
          setContextMenu({ file, x: e.clientX, y: e.clientY });
        }}
        className={`group flex items-center justify-between h-[30px] px-2.5 rounded cursor-pointer transition-colors text-xs select-none ${
          isActive
            ? 'bg-blue-600/20 text-blue-200 border-l-2 border-blue-500 font-medium'
            : 'text-gray-300 hover:bg-gray-800/60 border-l-2 border-transparent'
        }`}
        title={`${file.name}${isDirty ? ' (Unsaved changes)' : ''}`}
      >
        <div className="flex items-center gap-2 overflow-hidden flex-1 min-w-0">
          {getFileIcon(file.name)}
          <span className="truncate text-gray-200 group-hover:text-white">
            {file.name}
          </span>
          {isPinned && !isPinnedContext && (
            <span title="Pinned artifact" className="flex items-center">
              <Pin size={10} className="text-amber-400/70 shrink-0" />
            </span>
          )}
          {isDirty && (
            <span
              className="w-1.5 h-1.5 rounded-full bg-amber-400 shrink-0 ml-0.5"
              title="Unsaved changes"
              aria-label="Unsaved changes"
            />
          )}
        </div>

        <div className="flex items-center gap-1.5 shrink-0 ml-1">
          {getValidationIcon(file.id)}
          
          {/* Action Menu Button */}
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              const rect = e.currentTarget.getBoundingClientRect();
              setContextMenu({ file, x: rect.right, y: rect.bottom });
            }}
            className="p-0.5 text-gray-500 hover:text-gray-200 rounded opacity-0 group-hover:opacity-100 transition-opacity"
            title="Artifact Actions"
          >
            <MoreVertical size={12} />
          </button>
        </div>
      </div>
    );
  };

  const renderSectionHeader = (id: string, label: string, count: number, icon?: React.ReactNode) => {
    const isExpanded = Boolean(expandedSections[id]);
    return (
      <button
        type="button"
        onClick={() => toggleSection(id)}
        className="w-full h-[28px] flex items-center justify-between px-2 text-[11px] font-semibold text-gray-400 hover:text-gray-200 hover:bg-gray-800/40 rounded transition-colors select-none"
      >
        <div className="flex items-center gap-1.5 overflow-hidden">
          {isExpanded ? <ChevronDown size={13} className="text-gray-500" /> : <ChevronRight size={13} className="text-gray-500" />}
          {icon}
          <span className="truncate uppercase tracking-wider">{label}</span>
        </div>
        <span className="text-[10px] text-gray-400 font-mono px-1.5 py-0.2 rounded bg-gray-800/80 shrink-0">
          {count}
        </span>
      </button>
    );
  };

  const generatedTargets = [
    { name: 'Python Controller', target: 'python', ext: '.py' },
    { name: 'ROS2 Node', target: 'ros2', ext: '.py' },
    { name: 'Java Supervisor', target: 'java', ext: '.java' },
    { name: 'PLC (IEC 61131-3)', target: 'plc_st', ext: '.st' },
    { name: 'Embedded C++', target: 'cpp', ext: '.hpp' },
  ];

  return (
    <div className="flex-1 overflow-y-auto px-1.5 py-1.5 space-y-1 select-none">
      {/* Pinned Section (if any pinned files exist) */}
      {pinnedFiles.length > 0 && (
        <div className="space-y-0.5 border-b border-gray-800/60 pb-1 mb-1">
          {renderSectionHeader('pinned', 'Pinned', pinnedFiles.length, <Pin size={12} className="text-amber-400" />)}
          {expandedSections['pinned'] && (
            <div className="space-y-0.5 pl-2">
              {pinnedFiles.map(f => renderFileRow(f, true))}
            </div>
          )}
        </div>
      )}

      {/* Data Models */}
      {dataModels.length > 0 && (
        <div className="space-y-0.5">
          {renderSectionHeader('data-models', 'Data Models', dataModels.length)}
          {expandedSections['data-models'] && (
            <div className="space-y-0.5 pl-2">
              {dataModels.map(f => renderFileRow(f))}
            </div>
          )}
        </div>
      )}

      {/* Capabilities & Operations */}
      {capabilitiesOps.length > 0 && (
        <div className="space-y-0.5">
          {renderSectionHeader('capabilities-ops', 'Capabilities & Ops', capabilitiesOps.length)}
          {expandedSections['capabilities-ops'] && (
            <div className="space-y-0.5 pl-2">
              {capabilitiesOps.map(f => renderFileRow(f))}
            </div>
          )}
        </div>
      )}

      {/* Supervisory Workflow */}
      {workflows.length > 0 && (
        <div className="space-y-0.5">
          {renderSectionHeader('supervisory-workflow', 'Supervisory Workflow', workflows.length)}
          {expandedSections['supervisory-workflow'] && (
            <div className="space-y-0.5 pl-2">
              {workflows.map(f => renderFileRow(f))}
            </div>
          )}
        </div>
      )}

      {/* State Machines */}
      {stateMachines.length > 0 && (
        <div className="space-y-0.5">
          {renderSectionHeader('state-machines', 'State Machines', stateMachines.length)}
          {expandedSections['state-machines'] && (
            <div className="space-y-0.5 pl-2">
              {stateMachines.map(f => renderFileRow(f))}
            </div>
          )}
        </div>
      )}

      {/* Other Project Files */}
      {otherFiles.length > 0 && (
        <div className="space-y-0.5">
          {renderSectionHeader('other-files', 'Other Files', otherFiles.length)}
          {expandedSections['other-files'] && (
            <div className="space-y-0.5 pl-2">
              {otherFiles.map(f => renderFileRow(f))}
            </div>
          )}
        </div>
      )}

      {/* Generated Artifacts Targets */}
      <div className="space-y-0.5 pt-1 border-t border-gray-800/60">
        {renderSectionHeader('generated', 'Generated Targets', generatedTargets.length, <Sparkles size={12} className="text-blue-400" />)}
        {expandedSections['generated'] && (
          <div className="space-y-0.5 pl-2">
            {generatedTargets.map(g => (
              <div
                key={g.target}
                onClick={() => {
                  setActiveView('codegen');
                }}
                className="group flex items-center justify-between h-[28px] px-2.5 rounded cursor-pointer transition-colors text-xs text-gray-300 hover:bg-gray-800/60 hover:text-white"
              >
                <div className="flex items-center gap-2 overflow-hidden">
                  <FileCode2 size={12} className="text-cyan-400 shrink-0" />
                  <span className="truncate">{g.name}</span>
                </div>
                <span className="text-[10px] text-gray-500 font-mono">{g.ext}</span>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Knowledge Section */}
      <div className="space-y-0.5 pt-1">
        {renderSectionHeader('knowledge', 'Knowledge Sources', 2, <Network size={12} className="text-emerald-400" />)}
        {expandedSections['knowledge'] && (
          <div className="space-y-0.5 pl-2">
            <div
              onClick={() => setActiveView('knowledgegraph')}
              className="flex items-center justify-between h-[28px] px-2.5 rounded cursor-pointer transition-colors text-xs text-gray-300 hover:bg-gray-800/60 hover:text-white"
            >
              <span className="truncate">Project Knowledge Graph</span>
              <ExternalLink size={11} className="text-gray-500" />
            </div>
            <div
              onClick={() => setActiveView('knowledgegraph')}
              className="flex items-center justify-between h-[28px] px-2.5 rounded cursor-pointer transition-colors text-xs text-gray-300 hover:bg-gray-800/60 hover:text-white"
            >
              <span className="truncate">Global Domain Catalog</span>
              <ExternalLink size={11} className="text-gray-500" />
            </div>
          </div>
        )}
      </div>

      {/* Empty State if Project has 0 files */}
      {files.length === 0 && (
        <div className="p-4 text-center text-gray-500 text-xs">
          <p className="font-semibold text-gray-300 mb-1">No Artifacts Yet</p>
          <p className="text-[11px] text-gray-500 mb-3">Create your first model or load a reference template.</p>
          <button
            onClick={onOpenNewArtifactModal}
            className="w-full py-1.5 px-3 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-medium transition"
          >
            + New Artifact
          </button>
        </div>
      )}

      {/* Floating Context Menu */}
      {contextMenu && (
        <div
          style={{ top: `${contextMenu.y}px`, left: `${contextMenu.x}px` }}
          className="fixed z-50 w-48 bg-[#161b22] border border-gray-700 rounded-md shadow-xl py-1 text-xs text-gray-200"
          onClick={(e) => e.stopPropagation()}
        >
          <div className="px-3 py-1 font-semibold text-[10px] text-gray-400 uppercase tracking-wider border-b border-gray-800 truncate">
            {contextMenu.file.name}
          </div>
          <button
            onClick={() => {
              handleSelectArtifact(contextMenu.file);
              setContextMenu(null);
            }}
            className="w-full text-left px-3 py-1.5 hover:bg-blue-600/20 hover:text-white flex items-center gap-2"
          >
            <FileCode2 size={13} /> Open
          </button>
          <button
            onClick={() => handleOpenInSplit(contextMenu.file)}
            className="w-full text-left px-3 py-1.5 hover:bg-blue-600/20 hover:text-white flex items-center gap-2"
          >
            <Columns size={13} /> Open in Split View
          </button>
          <button
            onClick={() => togglePin(contextMenu.file.id)}
            className="w-full text-left px-3 py-1.5 hover:bg-blue-600/20 hover:text-white flex items-center gap-2"
          >
            {pinnedFileIds.includes(contextMenu.file.id) ? (
              <>
                <PinOff size={13} /> Unpin Artifact
              </>
            ) : (
              <>
                <Pin size={13} /> Pin to Top
              </>
            )}
          </button>
          <button
            onClick={() => handleShowDependencies(contextMenu.file)}
            className="w-full text-left px-3 py-1.5 hover:bg-blue-600/20 hover:text-white flex items-center gap-2"
          >
            <Network size={13} /> Show Dependencies
          </button>
          <div className="border-t border-gray-800 my-1" />
          <button
            onClick={() => {
              setDeleteConfirmFile(contextMenu.file);
              setContextMenu(null);
            }}
            className="w-full text-left px-3 py-1.5 hover:bg-rose-950/40 text-rose-400 flex items-center gap-2"
          >
            <Trash2 size={13} /> Delete File
          </button>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deleteConfirmFile && (
        <div className="fixed inset-0 z-50 bg-black/70 flex items-center justify-center p-4">
          <div className="bg-[#161b22] border border-gray-800 rounded-lg p-5 max-w-sm w-full text-left shadow-2xl">
            <h3 className="text-sm font-bold text-gray-100 mb-2">Delete Artifact</h3>
            <p className="text-xs text-gray-400 mb-4">
              Are you sure you want to permanently delete <span className="font-semibold text-gray-200">{deleteConfirmFile.name}</span>? This operation cannot be undone.
            </p>
            <div className="flex justify-end gap-2">
              <button
                type="button"
                onClick={() => setDeleteConfirmFile(null)}
                className="px-3 py-1.5 rounded text-xs text-gray-300 hover:bg-gray-800 transition"
              >
                Cancel
              </button>
              <button
                type="button"
                onClick={() => handleDeleteFile(deleteConfirmFile)}
                className="px-3 py-1.5 rounded text-xs bg-rose-600 hover:bg-rose-500 text-white font-medium transition"
              >
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
