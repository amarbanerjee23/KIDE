import React, { useState, useEffect } from 'react';
import { knowledgeApi, CatalogItem } from '../../api/knowledge';
import { useEditorStore } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';
import { 
  BookOpen, Download, Cpu, Check, Layers, X, 
  Search, Loader2
} from 'lucide-react';

interface KnowledgeCatalogModalProps {
  isOpen: boolean;
  onClose: () => void;
  onImportSuccess?: () => void;
}

export const KnowledgeCatalogModal: React.FC<KnowledgeCatalogModalProps> = ({
  isOpen,
  onClose,
  onImportSuccess
}) => {
  const { projectId } = useEditorStore();
  const [catalog, setCatalog] = useState<CatalogItem[]>([]);
  const [selectedItem, setSelectedItem] = useState<CatalogItem | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isImporting, setIsImporting] = useState<boolean>(false);
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [importStatus, setImportStatus] = useState<string | null>(null);

  useEffect(() => {
    if (!isOpen) return;
    const fetchCatalog = async () => {
      setIsLoading(true);
      try {
        const items = await knowledgeApi.getCatalog();
        setCatalog(items);
        if (items.length > 0) {
          const detail = await knowledgeApi.getCatalogItem(items[0].id);
          setSelectedItem(detail);
        }
      } catch (err) {
        console.error('Failed to load knowledge catalog:', err);
      } finally {
        setIsLoading(false);
      }
    };
    fetchCatalog();
  }, [isOpen]);

  const handleSelectItem = async (item: CatalogItem) => {
    try {
      const detail = await knowledgeApi.getCatalogItem(item.id);
      setSelectedItem(detail);
      setImportStatus(null);
    } catch (err) {
      console.error('Failed to load item detail:', err);
    }
  };

  const handleImport = async () => {
    if (!projectId || !selectedItem) return;
    setIsImporting(true);
    setImportStatus(null);
    try {
      const res = await knowledgeApi.importKnowledge(projectId, selectedItem.id);
      setImportStatus(`Successfully imported ${res.imported_files.length} files into project!`);
      // Reload files in store
      const rawFiles = await projectsApi.listFiles(projectId);
      const mappedFiles = rawFiles.map(f => {
        const fname = f.filename || f.name || 'untitled';
        let lang = 'plaintext';
        if (fname.endsWith('.json')) lang = 'json';
        else if (fname.endsWith('.mnc')) lang = 'mncml';
        else if (fname.endsWith('.activity')) lang = 'activitydsl';
        else if (fname.endsWith('.cap') || fname.endsWith('.capability')) lang = 'capabilitydsl';
        else if (fname.endsWith('.op') || fname.endsWith('.operation')) lang = 'operationdsl';
        else if (fname.endsWith('.dml')) lang = 'dmldsl';
        return {
          id: String(f.id),
          name: fname,
          content: f.content,
          language: lang
        };
      });
      useEditorStore.getState().setFiles(mappedFiles);
      if (onImportSuccess) onImportSuccess();
    } catch (err: any) {
      setImportStatus(`Import failed: ${err.message || err}`);
    } finally {
      setIsImporting(false);
    }
  };

  if (!isOpen) return null;

  const categories = ['ALL', ...Array.from(new Set(catalog.map(c => c.category)))];

  const filteredItems = catalog.filter(item => {
    const matchesCat = selectedCategory === 'ALL' || item.category === selectedCategory;
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          item.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          item.tags.some(t => t.toLowerCase().includes(searchTerm.toLowerCase()));
    return matchesCat && matchesSearch;
  });

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      <div className="bg-slate-900 border border-slate-700 rounded-xl shadow-2xl w-full max-w-5xl h-[85vh] flex flex-col overflow-hidden text-slate-100">
        
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-slate-800 bg-slate-900/80">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-indigo-500/20 text-indigo-400 rounded-lg border border-indigo-500/30">
              <BookOpen className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-lg font-semibold flex items-center gap-2">
                External World Knowledge Hub & Equipment Catalog
                <span className="text-xs bg-indigo-500/20 text-indigo-300 px-2 py-0.5 rounded border border-indigo-500/30 font-mono">
                  PhD Thesis Ontologies
                </span>
              </h2>
              <p className="text-xs text-slate-400">
                Incorporate real-world device interfaces, capability ontologies, and operations into your project
              </p>
            </div>
          </div>
          <button 
            onClick={onClose}
            className="p-1.5 text-slate-400 hover:text-white hover:bg-slate-800 rounded-lg transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content Body */}
        <div className="flex-1 flex overflow-hidden">
          
          {/* Left Panel: Catalog List */}
          <div className="w-2/5 border-r border-slate-800 flex flex-col bg-slate-950/40">
            {/* Search & Filter */}
            <div className="p-4 border-b border-slate-800 space-y-3">
              <div className="relative">
                <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
                <input 
                  type="text"
                  placeholder="Search devices, sensors, actuators..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-full pl-9 pr-3 py-1.5 text-xs bg-slate-800 border border-slate-700 rounded-lg text-slate-200 placeholder-slate-500 focus:outline-none focus:border-indigo-500"
                />
              </div>

              {/* Category Pills */}
              <div className="flex flex-wrap gap-1.5">
                {categories.map(cat => (
                  <button
                    key={cat}
                    onClick={() => setSelectedCategory(cat)}
                    className={`text-[11px] px-2 py-1 rounded transition ${
                      selectedCategory === cat 
                        ? 'bg-indigo-600 text-white font-medium' 
                        : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                    }`}
                  >
                    {cat}
                  </button>
                ))}
              </div>
            </div>

            {/* List */}
            <div className="flex-1 overflow-y-auto p-3 space-y-2">
              {isLoading ? (
                <div className="flex items-center justify-center h-32 text-slate-500 gap-2">
                  <Loader2 className="w-4 h-4 animate-spin text-indigo-400" />
                  <span className="text-xs">Loading device catalog...</span>
                </div>
              ) : filteredItems.length === 0 ? (
                <div className="text-center py-8 text-xs text-slate-500">No matching equipment profiles found.</div>
              ) : (
                filteredItems.map(item => (
                  <div
                    key={item.id}
                    onClick={() => handleSelectItem(item)}
                    className={`p-3 rounded-lg border cursor-pointer transition ${
                      selectedItem?.id === item.id
                        ? 'bg-indigo-950/40 border-indigo-500/50 shadow-sm'
                        : 'bg-slate-900/50 border-slate-800 hover:border-slate-700'
                    }`}
                  >
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-xs font-semibold text-slate-200">{item.name}</span>
                      <span className="text-[10px] text-slate-500 font-mono">{item.file_count} files</span>
                    </div>
                    <p className="text-[11px] text-slate-400 line-clamp-2 mb-2">{item.description}</p>
                    <div className="flex flex-wrap gap-1">
                      {item.tags.slice(0, 3).map(tag => (
                        <span key={tag} className="text-[10px] bg-slate-800 text-slate-400 px-1.5 py-0.5 rounded">
                          {tag}
                        </span>
                      ))}
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          {/* Right Panel: Selected Item Details */}
          <div className="flex-1 flex flex-col overflow-y-auto p-6 bg-slate-900/40">
            {selectedItem ? (
              <div className="space-y-6">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <span className="text-xs font-medium text-indigo-400 bg-indigo-950/60 px-2 py-0.5 rounded border border-indigo-800/40">
                      {selectedItem.category}
                    </span>
                    <span className="text-xs text-slate-500 font-mono">
                      Thesis Ref: {selectedItem.thesis_reference}
                    </span>
                  </div>
                  <h3 className="text-xl font-bold text-white mb-2">{selectedItem.name}</h3>
                  <p className="text-sm text-slate-300 leading-relaxed">{selectedItem.description}</p>
                </div>

                {/* Modeled Devices */}
                <div className="bg-slate-950/60 border border-slate-800 rounded-lg p-4">
                  <h4 className="text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-2">
                    <Cpu className="w-3.5 h-3.5 text-indigo-400" />
                    Integrated Hardware Components
                  </h4>
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-2">
                    {selectedItem.devices.map(d => (
                      <div key={d} className="text-xs bg-slate-900 border border-slate-800 rounded p-2 text-slate-300">
                        {d}
                      </div>
                    ))}
                  </div>
                </div>

                {/* Generated DSL Files */}
                <div>
                  <h4 className="text-xs font-semibold text-slate-300 uppercase tracking-wider mb-2 flex items-center gap-2">
                    <Layers className="w-3.5 h-3.5 text-indigo-400" />
                    Formal Specifications in this Knowledge Package
                  </h4>
                  <div className="space-y-2">
                    {selectedItem.files?.map(f => (
                      <div key={f.filename} className="border border-slate-800 rounded-lg overflow-hidden bg-slate-950/50">
                        <div className="flex items-center justify-between px-3 py-1.5 bg-slate-900 border-b border-slate-800 text-xs">
                          <span className="font-mono text-indigo-300 font-semibold">{f.filename}</span>
                          <span className="text-[10px] text-slate-400 uppercase font-mono">{f.file_type} DSL</span>
                        </div>
                        <pre className="p-3 text-[11px] font-mono text-slate-300 overflow-x-auto max-h-36 bg-slate-950/80">
                          {f.content}
                        </pre>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Import Status Alert */}
                {importStatus && (
                  <div className={`p-3 rounded-lg text-xs flex items-center gap-2 border ${
                    importStatus.includes('Successfully')
                      ? 'bg-emerald-950/40 border-emerald-500/50 text-emerald-300'
                      : 'bg-rose-950/40 border-rose-500/50 text-rose-300'
                  }`}>
                    {importStatus.includes('Successfully') ? <Check className="w-4 h-4 text-emerald-400" /> : <X className="w-4 h-4 text-rose-400" />}
                    {importStatus}
                  </div>
                )}

                {/* Action Bar */}
                <div className="pt-4 border-t border-slate-800 flex items-center justify-between">
                  <span className="text-xs text-slate-400">
                    Importing will generate <code className="text-indigo-300">.dml</code>, <code className="text-indigo-300">.cap</code>, and <code className="text-indigo-300">.op</code> files.
                  </span>
                  <button
                    onClick={handleImport}
                    disabled={isImporting || !projectId}
                    className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold flex items-center gap-2 shadow-lg shadow-indigo-500/20 transition"
                  >
                    {isImporting ? (
                      <>
                        <Loader2 className="w-4 h-4 animate-spin" />
                        Importing into Project...
                      </>
                    ) : (
                      <>
                        <Download className="w-4 h-4" />
                        Import into Active Project
                      </>
                    )}
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex flex-col items-center justify-center h-full text-slate-500 text-xs">
                Select an equipment profile from the left catalog to inspect its formal DSL models.
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
