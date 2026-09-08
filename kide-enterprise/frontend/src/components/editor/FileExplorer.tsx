import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { FileCode, FileJson, Plus, Trash2 } from 'lucide-react';

const FileExplorer = () => {
  const { files, activeFileId, setActiveFileId, addFile, removeFile } = useEditorStore();
  const [isCreating, setIsCreating] = useState(false);
  const [newFileName, setNewFileName] = useState('');

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newFileName.trim()) return;
    
    let ext = newFileName.split('.').pop() || '';
    let language = 'json';
    if (ext === 'activity') language = 'activitydsl';
    else if (ext === 'capability') language = 'capabilitydsl';
    else if (ext === 'operation') language = 'operationdsl';
    else if (ext === 'dml') language = 'dmldsl';

    const newFile = {
      id: Date.now().toString(),
      name: newFileName,
      language,
      content: '{\n  \n}'
    };
    
    addFile(newFile);
    setIsCreating(false);
    setNewFileName('');
  };

  return (
    <div className="h-full flex flex-col bg-surface border-r border-accent text-gray-300 select-none">
      <div className="flex items-center justify-between px-4 py-2 border-b border-accent text-xs font-semibold uppercase tracking-wider text-gray-400">
        <span>Workspace</span>
        <button onClick={() => setIsCreating(true)} className="hover:text-white transition-colors" title="New File">
          <Plus className="w-4 h-4" />
        </button>
      </div>
      
      <div className="flex-1 overflow-y-auto py-2">
        {isCreating && (
          <div className="px-4 py-1">
            <form onSubmit={handleCreate}>
              <input 
                autoFocus
                type="text" 
                value={newFileName}
                onChange={(e) => setNewFileName(e.target.value)}
                onBlur={() => setIsCreating(false)}
                className="w-full bg-background border border-accent rounded px-2 py-1 text-sm outline-none focus:border-blue-500 text-white"
                placeholder="filename.capability"
              />
            </form>
          </div>
        )}
        
        {files.map(file => (
          <div 
            key={file.id} 
            onClick={() => setActiveFileId(file.id)}
            className={`flex items-center justify-between px-4 py-1.5 cursor-pointer text-sm group ${activeFileId === file.id ? 'bg-[#2a2d3e] text-blue-400' : 'hover:bg-accent hover:text-white'}`}
          >
            <div className="flex items-center truncate">
              {file.name.endsWith('.json') ? (
                <FileJson className="w-4 h-4 mr-2 opacity-70" />
              ) : (
                <FileCode className="w-4 h-4 mr-2 opacity-70" />
              )}
              <span className="truncate">{file.name}</span>
            </div>
            {files.length > 1 && (
              <button 
                onClick={(e) => { e.stopPropagation(); removeFile(file.id); }}
                className="opacity-0 group-hover:opacity-100 hover:text-red-400"
              >
                <Trash2 className="w-3.5 h-3.5" />
              </button>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default FileExplorer;

