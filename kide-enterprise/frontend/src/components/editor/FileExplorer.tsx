import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { File, FileJson, Plus, Trash2, Activity, Settings, Cpu, Database } from 'lucide-react';

const uuidv4 = () => Math.random().toString(36).substring(2, 9) + Date.now().toString(36);

export const FileExplorer: React.FC = () => {
  const { files, activeFileId, setActiveFileId, addFile, removeFile } = useEditorStore();
  const [isAdding, setIsAdding] = useState(false);
  const [newFileName, setNewFileName] = useState('');

  const getLanguage = (filename: string) => {
    if (filename.endsWith('.json')) return 'json';
    if (filename.endsWith('.mnc')) return 'mncml';
    if (filename.endsWith('.activity')) return 'activitydsl';
    if (filename.endsWith('.cap')) return 'capabilitydsl';
    if (filename.endsWith('.op')) return 'operationdsl';
    if (filename.endsWith('.dml')) return 'dmldsl';
    return 'text';
  };

  const getTemplate = (ext: string) => {
    switch(ext) {
      case '.activity': return `activityDiagram {\n  name "NewActivity"\n}`;
      case '.cap': return `controlCapabilities {\n  name "NewCaps"\n}`;
      case '.op': return `operationDescriptions {\n  name "NewOps"\n}`;
      case '.dml': return `dataPackage {\n  name "NewData"\n}`;
      case '.mnc': return `model "NewModel" {\n  interface {\n    name "Interface"\n  }\n  control {\n    name "Control"\n  }\n}`;
      case '.json': return `{\n  "name": "NewModel"\n}`;
      default: return '';
    }
  };

  const getIcon = (filename: string) => {
    if (filename.endsWith('.json')) return <FileJson size={16} className="text-yellow-500" />;
    if (filename.endsWith('.mnc')) return <Settings size={16} className="text-blue-400" />;
    if (filename.endsWith('.activity')) return <Activity size={16} className="text-green-400" />;
    if (filename.endsWith('.cap')) return <Cpu size={16} className="text-purple-400" />;
    if (filename.endsWith('.op')) return <Settings size={16} className="text-orange-400" />;
    if (filename.endsWith('.dml')) return <Database size={16} className="text-red-400" />;
    return <File size={16} className="text-gray-400" />;
  };

  const handleAdd = () => {
    if (!newFileName) return;
    const extMatch = newFileName.match(/\.[0-9a-z]+$/i);
    const ext = extMatch ? extMatch[0] : '.txt';
    const finalName = extMatch ? newFileName : `${newFileName}.mnc`;
    
    addFile({
      id: uuidv4(),
      name: finalName,
      content: getTemplate(extMatch ? ext : '.mnc'),
      language: getLanguage(finalName)
    });
    setNewFileName('');
    setIsAdding(false);
  };

  return (
    <div className="w-64 bg-[#0a0a1a] border-r border-accent flex flex-col">
      <div className="p-3 border-b border-accent flex justify-between items-center bg-surface">
        <h2 className="text-sm font-semibold text-gray-200 uppercase tracking-wider">Explorer</h2>
        <button onClick={() => setIsAdding(true)} className="text-gray-400 hover:text-white">
          <Plus size={16} />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto py-2">
        {isAdding && (
          <div className="px-4 py-2">
            <input
              autoFocus
              type="text"
              className="w-full bg-background border border-accent text-gray-200 text-sm rounded px-2 py-1 outline-none focus:border-blue-500"
              placeholder="filename.mnc"
              value={newFileName}
              onChange={(e) => setNewFileName(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === 'Enter') handleAdd();
                if (e.key === 'Escape') setIsAdding(false);
              }}
              onBlur={() => setIsAdding(false)}
            />
          </div>
        )}

        {files.map(file => (
          <div
            key={file.id}
            onClick={() => setActiveFileId(file.id)}
            className={`flex items-center justify-between px-4 py-1.5 cursor-pointer text-sm group ${
              activeFileId === file.id ? 'bg-blue-900/30 text-white border-l-2 border-blue-500' : 'text-gray-400 hover:bg-surface border-l-2 border-transparent'
            }`}
          >
            <div className="flex items-center gap-2 overflow-hidden">
              {getIcon(file.name)}
              <span className="truncate">{file.name}</span>
            </div>
            <button
              onClick={(e) => {
                e.stopPropagation();
                removeFile(file.id);
              }}
              className="opacity-0 group-hover:opacity-100 text-gray-500 hover:text-red-400"
            >
              <Trash2 size={14} />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};
