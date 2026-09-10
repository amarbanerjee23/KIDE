import React from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { X } from 'lucide-react';

export const EditorTabs: React.FC = () => {
  const { files, activeFileId, setActiveFileId, removeFile } = useEditorStore();

  if (files.length === 0) return null;

  const getBadge = (language: string) => {
    switch (language) {
      case 'activitydsl': return { text: 'ACT', color: 'bg-green-900/50 text-green-400' };
      case 'capabilitydsl': return { text: 'CAP', color: 'bg-purple-900/50 text-purple-400' };
      case 'operationdsl': return { text: 'OP', color: 'bg-orange-900/50 text-orange-400' };
      case 'dmldsl': return { text: 'DML', color: 'bg-red-900/50 text-red-400' };
      case 'mncml': return { text: 'MNC', color: 'bg-blue-900/50 text-blue-400' };
      case 'json': return { text: 'JSON', color: 'bg-yellow-900/50 text-yellow-400' };
      default: return { text: 'TXT', color: 'bg-gray-800 text-gray-400' };
    }
  };

  return (
    <div className="flex bg-[#0a0a1a] border-b border-accent overflow-x-auto scrollbar-none">
      {files.map(file => {
        const badge = getBadge(file.language);
        return (
          <div
            key={file.id}
            onClick={() => setActiveFileId(file.id)}
            className={`flex items-center gap-2 px-3 py-2 cursor-pointer border-r border-accent min-w-[120px] group
              ${activeFileId === file.id ? 'bg-surface text-gray-100 border-t-2 border-t-blue-500' : 'text-gray-400 hover:bg-gray-800 border-t-2 border-t-transparent'}`}
          >
            <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${badge.color}`}>
              {badge.text}
            </span>
            <span className="text-sm truncate max-w-[150px]">{file.name}</span>
            <button
              onClick={(e) => {
                e.stopPropagation();
                removeFile(file.id);
              }}
              className="ml-auto opacity-0 group-hover:opacity-100 hover:text-red-400"
            >
              <X size={14} />
            </button>
          </div>
        );
      })}
    </div>
  );
};
