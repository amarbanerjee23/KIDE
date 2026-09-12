import React, { useState, useMemo, useRef, useEffect } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { buildWorkspaceSymbolIndex, navigateToDefinition } from '../../lib/monaco/xtextLanguageService';
import { SearchResultItem } from '../../types/navigator';
import { 
  Search, FileCode2, Database, Cpu, Settings, Activity, 
  Layers, X 
} from 'lucide-react';

export const ProjectSearch: React.FC = () => {
  const { files, setActiveFileId, setActiveView } = useEditorStore();
  const [query, setQuery] = useState('');
  const [selectedIndex, setSelectedIndex] = useState<number>(0);
  const inputRef = useRef<HTMLInputElement>(null);

  // Focus search input on mount
  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  // Execute fast project-local search across files, symbols, and content
  const results = useMemo<SearchResultItem[]>(() => {
    const q = query.trim().toLowerCase();
    if (!q) return [];

    const matches: SearchResultItem[] = [];
    const maxResults = 40;

    // 1. Filename matches
    for (const file of files) {
      if (file.name.toLowerCase().includes(q)) {
        matches.push({
          id: `file-${file.id}`,
          fileId: file.id,
          fileName: file.name,
          line: 1,
          matchText: file.name,
          matchType: 'filename'
        });
      }
    }

    // 2. Symbol matches (from Xtext symbol index)
    try {
      const symbols = buildWorkspaceSymbolIndex();
      for (const sym of symbols) {
        if (sym.name.toLowerCase().includes(q) || (sym.detail && sym.detail.toLowerCase().includes(q))) {
          matches.push({
            id: `sym-${sym.fileId}-${sym.line}-${sym.name}`,
            fileId: sym.fileId,
            fileName: sym.fileName,
            line: sym.line,
            matchText: `${sym.kind}: ${sym.name}`,
            matchType: 'symbol',
            symbolKind: sym.kind
          });
          if (matches.length >= maxResults) break;
        }
      }
    } catch {
      // ignore symbol extraction failure if any
    }

    // 3. Text content matches (line-by-line)
    if (matches.length < maxResults) {
      for (const file of files) {
        const lines = file.content.split('\n');
        for (let i = 0; i < lines.length; i++) {
          const lineText = lines[i];
          if (lineText.toLowerCase().includes(q)) {
            // Check if already matched as symbol on same line
            const alreadyMatched = matches.some(m => m.fileId === file.id && m.line === i + 1);
            if (!alreadyMatched) {
              matches.push({
                id: `line-${file.id}-${i + 1}`,
                fileId: file.id,
                fileName: file.name,
                line: i + 1,
                matchText: lineText.trim(),
                matchType: 'content'
              });
            }
            if (matches.length >= maxResults) break;
          }
        }
        if (matches.length >= maxResults) break;
      }
    }

    return matches;
  }, [query, files]);

  // Reset selected index when query changes
  useEffect(() => {
    setSelectedIndex(0);
  }, [query]);

  const handleSelectResult = (item: SearchResultItem) => {
    setActiveFileId(item.fileId);
    setActiveView('editor');
    if (item.line) {
      navigateToDefinition(item.fileId, item.line, 1);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (results.length === 0) return;

    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev + 1) % results.length);
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setSelectedIndex((prev) => (prev - 1 + results.length) % results.length);
    } else if (e.key === 'Enter') {
      e.preventDefault();
      if (results[selectedIndex]) {
        handleSelectResult(results[selectedIndex]);
      }
    } else if (e.key === 'Escape') {
      setQuery('');
    }
  };

  const getMatchIcon = (item: SearchResultItem) => {
    if (item.matchType === 'filename') {
      return <FileCode2 size={13} className="text-blue-400 shrink-0" />;
    }
    if (item.symbolKind === 'DataModel') {
      return <Database size={13} className="text-rose-400 shrink-0" />;
    }
    if (item.symbolKind === 'Capability') {
      return <Cpu size={13} className="text-purple-400 shrink-0" />;
    }
    if (item.symbolKind === 'Operation') {
      return <Settings size={13} className="text-amber-400 shrink-0" />;
    }
    if (item.symbolKind === 'Activity') {
      return <Activity size={13} className="text-emerald-400 shrink-0" />;
    }
    return <Layers size={13} className="text-gray-400 shrink-0" />;
  };

  return (
    <div className="flex-1 flex flex-col min-h-0 select-none">
      {/* Search Input */}
      <div className="p-2 border-b border-gray-800/80">
        <div className="relative flex items-center">
          <Search className="w-3.5 h-3.5 absolute left-2.5 text-gray-500 pointer-events-none" />
          <input
            ref={inputRef}
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Search artifacts, symbols..."
            className="w-full pl-8 pr-7 py-1.5 bg-[#161b22] border border-gray-800 rounded-md text-xs text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500 transition"
          />
          {query && (
            <button
              onClick={() => setQuery('')}
              className="absolute right-2 text-gray-500 hover:text-gray-300 p-0.5 rounded"
            >
              <X size={13} />
            </button>
          )}
        </div>
      </div>

      {/* Search Results List */}
      <div className="flex-1 overflow-y-auto py-1">
        {!query.trim() ? (
          <div className="p-4 text-center text-gray-500 text-xs">
            <p className="font-medium text-gray-400">Project Search</p>
            <p className="text-[11px] text-gray-500 mt-1">
              Search by artifact name, schema, capability, operation, or line content.
            </p>
          </div>
        ) : results.length === 0 ? (
          <div className="p-4 text-center text-gray-500 text-xs">
            <p>No results found for &ldquo;{query}&rdquo;</p>
          </div>
        ) : (
          <div className="space-y-0.5 px-1.5">
            <div className="px-2 py-1 text-[10px] uppercase font-semibold tracking-wider text-gray-500">
              {results.length} Matches Found
            </div>
            {results.map((item, idx) => {
              const isSelected = idx === selectedIndex;
              return (
                <div
                  key={item.id}
                  onClick={() => handleSelectResult(item)}
                  onMouseEnter={() => setSelectedIndex(idx)}
                  className={`flex items-start gap-2 px-2.5 py-1.5 rounded cursor-pointer transition-colors text-xs ${
                    isSelected
                      ? 'bg-blue-600/20 text-blue-200 border-l-2 border-blue-500'
                      : 'text-gray-300 hover:bg-gray-800/60 border-l-2 border-transparent'
                  }`}
                >
                  <div className="mt-0.5">{getMatchIcon(item)}</div>
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center justify-between gap-1">
                      <span className="font-medium truncate text-gray-200">
                        {item.matchType === 'filename' ? item.fileName : item.matchText}
                      </span>
                      <span className="text-[10px] text-gray-500 font-mono shrink-0">
                        L{item.line}
                      </span>
                    </div>
                    {item.matchType !== 'filename' && (
                      <p className="text-[10px] text-gray-500 truncate mt-0.5 font-mono">
                        {item.fileName}
                      </p>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
