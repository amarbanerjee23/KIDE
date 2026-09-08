import React from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { X, FileCode } from 'lucide-react';

const EditorTabs = () => {
  const { files, activeFileId, setActiveFileId, removeFile } = useEditorStore();

  return (
    <div className="flex bg-[#1e1e1e] border-b border-accent overflow-x-auto no-scrollbar">
      {files.map(file => (
        <div 
          key={file.id} 
          onClick={() => setActiveFileId(file.id)}
          className={`flex items-center h-9 px-4 border-r border-accent cursor-pointer min-w-[120px] max-w-[200px] group select-none ${activeFileId === file.id ? 'bg-background text-blue-400 border-t-2 border-t-blue-500' : 'text-gray-400 hover:bg-[#2a2d3e] hover:text-gray-200 border-t-2 border-t-transparent'}`}
        >
          <FileCode className="w-3.5 h-3.5 mr-2 opacity-70 flex-shrink-0" />
          <span className="text-sm truncate flex-1">{file.name}</span>
          {files.length > 1 && (
            <div 
              onClick={(e) => { e.stopPropagation(); removeFile(file.id); }}
              className={`ml-2 p-0.5 rounded hover:bg-gray-700 ${activeFileId === file.id ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'}`}
            >
              <X className="w-3.5 h-3.5" />
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default EditorTabs;

