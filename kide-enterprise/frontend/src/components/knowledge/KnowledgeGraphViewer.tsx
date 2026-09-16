import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { 
  ReactFlow, Background, Controls, MiniMap, 
  Node, Handle, Position, NodeProps 
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { useEditorStore } from '../../stores/editorStore';
import { 
  knowledgeApi, 
  KnowledgeGraphNode, 
  KnowledgeGraphEdge, 
  CapabilityMatch, 
  StoreSummary 
} from '../../api/knowledge';
import { 
  computeGraphLayout, 
  GraphLayoutType, 
  GraphDensityMode 
} from '../../lib/graphLayout';
import { FileItem } from '../../types/models';
import { 
  Network, Cpu, Zap, Wrench, Database, 
  Activity, GitMerge, Search, Download, Sparkles, 
  Layers, X, Loader2, RefreshCw, CheckCircle2,
  FileCode2, BookOpen, ArrowRight, ArrowLeft, Plus,
  ChevronRight, ExternalLink
} from 'lucide-react';

const getLanguageForFilename = (filename: string): string => {
  if (filename.endsWith('.dml')) return 'dmldsl';
  if (filename.endsWith('.cap')) return 'capabilitydsl';
  if (filename.endsWith('.op')) return 'operationdsl';
  if (filename.endsWith('.mnc')) return 'mncdsl';
  if (filename.endsWith('.activity')) return 'activitydsl';
  return 'plaintext';
};

// Custom Knowledge Node Component
const KnowledgeNodeComponent: React.FC<NodeProps> = ({ data, selected }) => {
  const node = data.node as KnowledgeGraphNode;
  const isCompact = Boolean(data.compact);
  const type = node.type;

  const getTypeStyle = () => {
    switch (type) {
      case 'domain':
      case 'repository':
      case 'project':
        return {
          border: 'border-purple-500/80',
          bg: 'bg-purple-950/40',
          text: 'text-purple-300',
          badgeBg: 'bg-purple-900/60',
          icon: Network
        };
      case 'device':
        return {
          border: 'border-cyan-500/80',
          bg: 'bg-cyan-950/40',
          text: 'text-cyan-300',
          badgeBg: 'bg-cyan-900/60',
          icon: Cpu
        };
      case 'interface':
        return {
          border: 'border-emerald-500/80',
          bg: 'bg-emerald-950/40',
          text: 'text-emerald-300',
          badgeBg: 'bg-emerald-900/60',
          icon: Network
        };
      case 'capability':
        return {
          border: 'border-indigo-500/80',
          bg: 'bg-indigo-950/40',
          text: 'text-indigo-300',
          badgeBg: 'bg-indigo-900/60',
          icon: Zap
        };
      case 'command':
        return {
          border: 'border-cyan-500/80',
          bg: 'bg-cyan-950/40',
          text: 'text-cyan-300',
          badgeBg: 'bg-cyan-900/60',
          icon: Zap
        };
      case 'event':
        return {
          border: 'border-amber-500/80',
          bg: 'bg-amber-950/40',
          text: 'text-amber-300',
          badgeBg: 'bg-amber-900/60',
          icon: Activity
        };
      case 'alarm':
        return {
          border: 'border-rose-500/80',
          bg: 'bg-rose-950/40',
          text: 'text-rose-300',
          badgeBg: 'bg-rose-900/60',
          icon: GitMerge
        };
      case 'datapoint':
        return {
          border: 'border-teal-500/80',
          bg: 'bg-teal-950/40',
          text: 'text-teal-300',
          badgeBg: 'bg-teal-900/60',
          icon: Database
        };
      case 'parameter':
        return {
          border: 'border-violet-500/80',
          bg: 'bg-violet-950/40',
          text: 'text-violet-300',
          badgeBg: 'bg-violet-900/60',
          icon: Wrench
        };
      case 'operation':
        return {
          border: 'border-amber-500/80',
          bg: 'bg-amber-950/40',
          text: 'text-amber-300',
          badgeBg: 'bg-amber-900/60',
          icon: Wrench
        };
      case 'datamodel':
        return {
          border: 'border-sky-500/80',
          bg: 'bg-sky-950/40',
          text: 'text-sky-300',
          badgeBg: 'bg-sky-900/60',
          icon: Database
        };
      case 'activity':
      case 'workflow':
        return {
          border: 'border-orange-500/80',
          bg: 'bg-orange-950/40',
          text: 'text-orange-300',
          badgeBg: 'bg-orange-900/60',
          icon: Activity
        };
      case 'operating_state':
        return {
          border: 'border-rose-500/80',
          bg: 'bg-rose-950/40',
          text: 'text-rose-300',
          badgeBg: 'bg-rose-900/60',
          icon: GitMerge
        };
      default:
        return {
          border: 'border-gray-600',
          bg: 'bg-gray-900/60',
          text: 'text-gray-300',
          badgeBg: 'bg-gray-800',
          icon: Layers
        };
    }
  };

  const style = getTypeStyle();
  const Icon = style.icon;

  if (isCompact) {
    return (
      <div className={`px-2.5 py-1.5 rounded-lg border-2 transition-all cursor-pointer flex items-center gap-2 ${
        selected ? 'ring-2 ring-blue-400 shadow-blue-500/20 scale-105' : ''
      } ${style.border} ${style.bg} backdrop-blur-md shadow-md min-w-[130px] max-w-[190px]`}>
        <Handle type="target" position={Position.Left} id="left-in" className="!bg-gray-400 !w-1.5 !h-1.5" />
        <Handle type="source" position={Position.Right} id="right-out" className="!bg-gray-400 !w-1.5 !h-1.5" />
        <Handle type="target" position={Position.Top} id="top-in" className="!bg-gray-400 !w-1.5 !h-1.5" />
        <Handle type="source" position={Position.Bottom} id="bottom-out" className="!bg-gray-400 !w-1.5 !h-1.5" />

        <Icon className={`w-3.5 h-3.5 shrink-0 ${style.text}`} />
        <span className="font-semibold text-[11px] text-gray-100 truncate" title={node.name}>
          {node.name}
        </span>
        <span className={`text-[8px] uppercase font-bold px-1 rounded ml-auto ${style.badgeBg} ${style.text}`}>
          {type.slice(0, 3)}
        </span>
      </div>
    );
  }

  return (
    <div className={`px-3 py-2.5 rounded-xl border-2 transition-all cursor-pointer ${
      selected ? 'ring-2 ring-blue-400 shadow-blue-500/20 scale-105' : ''
    } ${style.border} ${style.bg} backdrop-blur-md shadow-lg min-w-[150px] max-w-[220px]`}>
      <Handle type="target" position={Position.Left} id="left-in" className="!bg-gray-400 !w-2 !h-2" />
      <Handle type="source" position={Position.Right} id="right-out" className="!bg-gray-400 !w-2 !h-2" />
      <Handle type="target" position={Position.Top} id="top-in" className="!bg-gray-400 !w-2 !h-2" />
      <Handle type="source" position={Position.Bottom} id="bottom-out" className="!bg-gray-400 !w-2 !h-2" />
      
      <div className="flex items-center gap-1.5 mb-1">
        <Icon className={`w-3.5 h-3.5 ${style.text}`} />
        <span className={`text-[9px] uppercase font-bold tracking-wider px-1.5 py-0.5 rounded ${style.badgeBg} ${style.text}`}>
          {type}
        </span>
      </div>

      <div className="font-semibold text-xs text-gray-100 truncate" title={node.name}>
        {node.name}
      </div>

      <div className="text-[10px] text-gray-400 truncate mt-0.5">
        {node.category || node.properties?.parent_domain || 'Ontology Entity'}
      </div>

      {node.source_file && (
        <div className="mt-1 text-[9px] font-mono text-blue-400/80 truncate flex items-center gap-1">
          <FileCode2 className="w-2.5 h-2.5" />
          <span>{node.source_file}</span>
        </div>
      )}
    </div>
  );
};

const nodeTypes = {
  knowledgeNode: KnowledgeNodeComponent
};

export const KnowledgeGraphViewer: React.FC = () => {
  const [searchParams] = useSearchParams();
  const initialDomain = searchParams.get('domain') || 'all';

  const { 
    projectId, 
    projectName, 
    files,
    addFile,
    updateFileContent,
    setActiveFileId,
    setActiveView,
    graphScope, 
    setGraphScope, 
    graphData, 
    setGraphData, 
    selectedGraphNode,
    setSelectedGraphNode 
  } = useEditorStore();
  
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [filterType, setFilterType] = useState<string>('all');
  const [selectedDomain, setSelectedDomain] = useState<string>(initialDomain);
  const [layoutType, setLayoutType] = useState<GraphLayoutType>('dagre-lr');
  const [densityMode, setDensityMode] = useState<GraphDensityMode>('standard');
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [storeSummary, setStoreSummary] = useState<StoreSummary | null>(null);

  // Reusability action feedback state
  const [importingEntityId, setImportingEntityId] = useState<string | null>(null);
  const [importSuccessMsg, setImportSuccessMsg] = useState<string | null>(null);

  // Semantic capability matching modal state
  const [showMatchModal, setShowMatchModal] = useState<boolean>(false);
  const [matchQuery, setMatchQuery] = useState<string>('');
  const [matchingResults, setMatchingResults] = useState<CapabilityMatch[]>([]);
  const [isMatching, setIsMatching] = useState<boolean>(false);

  // Load Knowledge Store Summary
  useEffect(() => {
    knowledgeApi.getStoreSummary()
      .then(res => setStoreSummary(res))
      .catch(err => console.warn('Could not load knowledge store summary:', err));
  }, []);

  // Fetch Graph Data based on Scope
  const fetchGraph = useCallback(async () => {
    setIsLoading(true);
    try {
      if (graphScope === 'project' && projectId) {
        const data = await knowledgeApi.getProjectGraph(projectId);
        setGraphData(data);
      } else {
        const data = await knowledgeApi.getGlobalGraph();
        setGraphData(data);
      }
    } catch (err) {
      console.error('Failed to load knowledge graph:', err);
    } finally {
      setIsLoading(false);
    }
  }, [graphScope, projectId, setGraphData]);

  useEffect(() => {
    fetchGraph();
  }, [fetchGraph]);

  // Handle single entity import into the current project workspace
  const handleImportEntity = async (entity: KnowledgeGraphNode) => {
    if (!projectId) {
      alert('Please open or create a project workspace to import this entity.');
      return;
    }

    setImportingEntityId(entity.id);
    setImportSuccessMsg(null);
    try {
      const res = await knowledgeApi.importEntity(projectId, entity.id);
      const imported = res.imported_file;
      if (imported) {
        const existing = files.find(f => f.id === String(imported.id) || f.name === imported.filename);
        const fileItem: FileItem = {
          id: String(imported.id),
          name: imported.filename,
          content: imported.content || '',
          language: getLanguageForFilename(imported.filename)
        };

        if (existing) {
          updateFileContent(existing.id, imported.content || '');
          setActiveFileId(existing.id);
        } else {
          addFile(fileItem);
          setActiveFileId(fileItem.id);
        }

        setImportSuccessMsg(`Imported ${imported.filename} into project workspace!`);
        // Refresh graph if in project scope
        if (graphScope === 'project') {
          fetchGraph();
        }
      }
    } catch (err: any) {
      alert(`Import failed: ${err.message || err}`);
    } finally {
      setImportingEntityId(null);
    }
  };

  // Handle importing full domain package (all 4 domain artifacts)
  const handleImportDomain = async (catalogId: string, domainName: string) => {
    if (!projectId) {
      alert('Please open or create a project workspace to import this domain package.');
      return;
    }

    setImportingEntityId(catalogId);
    setImportSuccessMsg(null);
    try {
      const res = await knowledgeApi.importKnowledge(projectId, catalogId);
      if (res.imported_files && res.imported_files.length > 0) {
        for (const imported of res.imported_files) {
          const existing = files.find(f => f.id === String(imported.id) || f.name === imported.filename);
          const fileItem: FileItem = {
            id: String(imported.id),
            name: imported.filename,
            content: imported.content || '',
            language: getLanguageForFilename(imported.filename)
          };
          if (existing) {
            updateFileContent(existing.id, imported.content || '');
          } else {
            addFile(fileItem);
          }
        }
        const first = res.imported_files[0];
        if (first) setActiveFileId(String(first.id));
        setImportSuccessMsg(`Imported complete ${domainName} bundle (${res.imported_files.length} artifacts)!`);
        if (graphScope === 'project') {
          fetchGraph();
        }
      }
    } catch (err: any) {
      alert(`Import failed: ${err.message || err}`);
    } finally {
      setImportingEntityId(null);
    }
  };

  // Generate topological layered layout
  const { nodes, edges } = useMemo(() => {
    if (!graphData) return { nodes: [], edges: [] };

    const q = searchQuery.toLowerCase().trim();
    const selectedDomainObj = storeSummary?.domains.find(d => d.id === selectedDomain);

    const filteredNodesList = graphData.nodes.filter((n: any) => {
      const matchSearch = !q || 
        n.name.toLowerCase().includes(q) || 
        (n.category && n.category.toLowerCase().includes(q)) ||
        (n.type && n.type.toLowerCase().includes(q));

      const matchType = filterType === 'all' || 
        n.type === filterType || 
        (filterType === 'activity' && ['activity', 'workflow'].includes(n.type));

      const matchDomain = selectedDomain === 'all' ||
        n.id === `cat_${selectedDomain}` ||
        n.id.startsWith(`dev_${selectedDomain}_`) ||
        n.id.startsWith(`dm_${selectedDomain}_`) ||
        n.id.startsWith(`cap_${selectedDomain}_`) ||
        n.id.startsWith(`op_${selectedDomain}_`) ||
        n.id.startsWith(`wf_${selectedDomain}_`) ||
        n.id.startsWith(`act_${selectedDomain}_`) ||
        n.id.startsWith(`cmd_${selectedDomain}_`) ||
        n.id.startsWith(`evt_${selectedDomain}_`) ||
        n.id.startsWith(`alm_${selectedDomain}_`) ||
        n.id.startsWith(`dp_${selectedDomain}_`) ||
        n.id.startsWith(`param_${selectedDomain}_`) ||
        n.properties?.raw_id === selectedDomain ||
        (selectedDomainObj && n.properties?.parent_domain === selectedDomainObj.name);

      return matchSearch && matchType && matchDomain;
    });

    return computeGraphLayout({
      layoutType,
      density: densityMode,
      filteredNodes: filteredNodesList,
      filteredEdges: graphData.edges,
    });
  }, [graphData, filterType, selectedDomain, searchQuery, storeSummary, layoutType, densityMode]);

  const handleNodeClick = (_: any, n: Node) => {
    const raw = graphData?.nodes.find((item: any) => item.id === n.id);
    if (raw) {
      setSelectedGraphNode(raw);
      setImportSuccessMsg(null);
    }
  };

  // Connected edges for the currently selected node
  const selectedNodeRelations = useMemo(() => {
    if (!selectedGraphNode || !graphData) return { incoming: [], outgoing: [] };
    const incoming = graphData.edges
      .filter((e: KnowledgeGraphEdge) => e.target === selectedGraphNode.id)
      .map((e: KnowledgeGraphEdge) => ({
        edge: e,
        node: graphData.nodes.find((n: KnowledgeGraphNode) => n.id === e.source)
      }))
      .filter((item: any) => item.node !== undefined);

    const outgoing = graphData.edges
      .filter((e: KnowledgeGraphEdge) => e.source === selectedGraphNode.id)
      .map((e: KnowledgeGraphEdge) => ({
        edge: e,
        node: graphData.nodes.find((n: KnowledgeGraphNode) => n.id === e.target)
      }))
      .filter((item: any) => item.node !== undefined);

    return { incoming, outgoing };
  }, [selectedGraphNode, graphData]);

  const handleExportRdf = async () => {
    if (!projectId) return;
    try {
      const rdf = await knowledgeApi.exportGraph(projectId, 'turtle');
      const blob = new Blob([rdf], { type: 'text/turtle' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${projectName || 'Project'}_ontology.ttl`;
      a.click();
      URL.revokeObjectURL(url);
    } catch (err) {
      alert(`Export failed: ${err}`);
    }
  };

  const handleExportJson = () => {
    if (!graphData) return;
    const jsonStr = JSON.stringify(graphData, null, 2);
    const blob = new Blob([jsonStr], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${graphScope === 'project' ? (projectName || 'Project') : 'KIDE_Global_Knowledge_Store'}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const handleRunMatch = async () => {
    if (!matchQuery.trim()) return;
    setIsMatching(true);
    try {
      const matches = await knowledgeApi.matchCapabilities(matchQuery);
      setMatchingResults(matches);
    } catch (err) {
      console.error('Capability match failed:', err);
    } finally {
      setIsMatching(false);
    }
  };

  return (
    <div className="w-full h-full flex flex-col bg-[#0b0f19] relative overflow-hidden select-none">
      
      {/* Top Controls & Navigation Bar */}
      <div className="h-12 bg-[#111622] border-b border-gray-800 px-4 flex items-center justify-between text-xs gap-3 z-10">
        
        {/* Left: Scope Switcher, Domain Selector & Search */}
        <div className="flex items-center gap-2.5">
          <div className="flex bg-[#1a2333] p-0.5 rounded-lg border border-gray-700/60">
            <button
              onClick={() => {
                setGraphScope('project');
                setSelectedGraphNode(null);
              }}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition ${
                graphScope === 'project' ? 'bg-blue-600 text-white shadow-sm' : 'text-gray-400 hover:text-white'
              }`}
            >
              <span>📁 Project Graph</span>
            </button>
            <button
              onClick={() => {
                setGraphScope('global');
                setSelectedGraphNode(null);
              }}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition ${
                graphScope === 'global' ? 'bg-purple-600 text-white shadow-sm' : 'text-gray-400 hover:text-white'
              }`}
              title="Explore the global industrial equipment & metamodel ontology repository"
            >
              <span>🌐 Knowledge Store</span>
            </button>
          </div>

          {/* Domain Selector (Visible in Global Scope) */}
          {graphScope === 'global' && storeSummary?.domains && (
            <div className="hidden lg:flex items-center gap-1.5 bg-gray-900 border border-gray-700/80 rounded-md px-2 py-0.5">
              <span className="text-[10px] text-gray-400 font-semibold uppercase tracking-wider">Domain:</span>
              <select
                value={selectedDomain}
                onChange={(e) => setSelectedDomain(e.target.value)}
                className="bg-transparent text-gray-200 text-xs font-medium focus:outline-none cursor-pointer pr-1"
              >
                <option value="all" className="bg-gray-900 text-gray-200">
                  All Domains ({storeSummary.domains.length})
                </option>
                {storeSummary.domains.map(d => (
                  <option key={d.id} value={d.id} className="bg-gray-900 text-gray-200">
                    {d.name}
                  </option>
                ))}
              </select>
            </div>
          )}

          {/* Search Bar */}
          <div className="relative">
            <Search className="w-3.5 h-3.5 absolute left-2.5 top-2 text-gray-500" />
            <input
              type="text"
              placeholder="Search entities, capabilities..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="bg-gray-900 border border-gray-700 rounded-md pl-8 pr-3 py-1 text-xs text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500 w-36 md:w-52"
            />
          </div>
        </div>

        {/* Center: Filter Type Pills */}
        <div className="hidden xl:flex items-center gap-1 overflow-x-auto max-w-[45vw] py-0.5">
          {[
            { id: 'all', label: 'All' },
            { id: 'capability', label: 'Capabilities' },
            { id: 'datamodel', label: 'Data Models' },
            { id: 'activity', label: 'Activities' },
            { id: 'operation', label: 'Operations' },
            { id: 'device', label: 'Devices' },
            { id: 'operating_state', label: 'States' },
            { id: 'command', label: 'Commands' },
            { id: 'event', label: 'Events' },
            { id: 'alarm', label: 'Alarms' },
            { id: 'datapoint', label: 'DataPoints' },
            { id: 'parameter', label: 'Parameters' },
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setFilterType(tab.id)}
              className={`px-2 py-0.5 whitespace-nowrap rounded-md text-[11px] font-medium transition ${
                filterType === tab.id
                  ? 'bg-gray-800 text-blue-400 border border-blue-500/30 font-semibold'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/40'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Right: Actions & Stats */}
        <div className="flex items-center gap-2">
          {/* Standard Layout Engine Selector */}
          <div className="flex items-center bg-gray-900 border border-gray-700/80 rounded-md px-2 py-0.5">
            <span className="text-[10px] text-gray-400 font-semibold uppercase tracking-wider mr-1.5 hidden sm:inline">
              Layout:
            </span>
            <select
              value={layoutType}
              onChange={(e) => setLayoutType(e.target.value as GraphLayoutType)}
              className="bg-transparent text-gray-200 text-xs font-medium focus:outline-none cursor-pointer pr-1"
              title="Switch standard graph visualization layout"
            >
              <option value="dagre-lr" className="bg-gray-900 text-gray-200">
                Hierarchical (Dagre LR)
              </option>
              <option value="dagre-tb" className="bg-gray-900 text-gray-200">
                Hierarchical (Dagre TB)
              </option>
              <option value="force" className="bg-gray-900 text-gray-200">
                Force-Directed (Organic)
              </option>
              <option value="radial" className="bg-gray-900 text-gray-200">
                Concentric / Radial
              </option>
              <option value="layered" className="bg-gray-900 text-gray-200">
                Metamodel Layers
              </option>
            </select>
          </div>

          {/* Density Toggle */}
          <button
            onClick={() => setDensityMode(densityMode === 'standard' ? 'compact' : 'standard')}
            className={`px-2 py-1 rounded-md text-[11px] font-semibold border transition flex items-center gap-1 ${
              densityMode === 'compact'
                ? 'bg-blue-600/20 border-blue-500/40 text-blue-300'
                : 'bg-gray-900 border-gray-700 text-gray-400 hover:text-gray-200'
            }`}
            title="Toggle between standard detailed cards and compact nodes"
          >
            <Layers className="w-3.5 h-3.5" />
            <span className="hidden md:inline">{densityMode === 'compact' ? 'Compact' : 'Standard'}</span>
          </button>

          {graphData && (
            <span className="hidden md:inline-flex text-[11px] text-gray-400 font-mono bg-gray-900 px-2.5 py-1 rounded-md border border-gray-800">
              {nodes.length} Nodes &bull; {edges.length} Edges
            </span>
          )}

          <button
            onClick={() => window.open('/knowledge-graph', '_blank')}
            className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white rounded-md text-xs font-semibold border border-gray-700 shadow-sm transition"
            title="Open full Knowledge Graph in a dedicated new browser tab"
          >
            <ExternalLink className="w-3.5 h-3.5 text-blue-400" />
            <span className="hidden sm:inline">Open in New Tab ↗</span>
          </button>

          <button
            onClick={() => setShowMatchModal(true)}
            className="flex items-center gap-1.5 px-2.5 py-1 bg-indigo-600 hover:bg-indigo-500 text-white rounded-md text-xs font-semibold shadow-sm transition"
            title="Search for device capabilities to match your process activities"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span className="hidden sm:inline">Match Capabilities</span>
          </button>

          {/* Export Graph Dropdown / Button */}
          <div className="flex items-center bg-gray-800 rounded-md border border-gray-700 overflow-hidden">
            {graphScope === 'project' && (
              <button
                onClick={handleExportRdf}
                className="flex items-center gap-1 px-2.5 py-1 hover:bg-gray-700 text-gray-300 text-xs border-r border-gray-700 transition"
                title="Export as W3C RDF Turtle ontology"
              >
                <Download className="w-3.5 h-3.5 text-blue-400" />
                <span className="hidden sm:inline">Turtle (.ttl)</span>
              </button>
            )}
            <button
              onClick={handleExportJson}
              className="flex items-center gap-1 px-2.5 py-1 hover:bg-gray-700 text-gray-300 text-xs transition"
              title="Export complete graph in JSON format"
            >
              <Download className="w-3.5 h-3.5 text-amber-400" />
              <span className="hidden sm:inline">JSON</span>
            </button>
          </div>

          <button
            onClick={fetchGraph}
            className="p-1.5 text-gray-400 hover:text-white hover:bg-gray-800 rounded-md transition"
            title="Refresh Knowledge Graph"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isLoading ? 'animate-spin' : ''}`} />
          </button>
        </div>
      </div>

      {/* Main Canvas Area */}
      <div className="flex-1 relative flex">
        {isLoading ? (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-[#0b0f19] text-gray-400 gap-3 z-10">
            <Loader2 className="w-8 h-8 animate-spin text-blue-500" />
            <span className="text-xs">Building Knowledge Graph from models...</span>
          </div>
        ) : null}

        <div className="flex-1 h-full relative">
          <ReactFlow
            key={`${layoutType}-${densityMode}`}
            nodes={nodes}
            edges={edges}
            nodeTypes={nodeTypes}
            onNodeClick={handleNodeClick}
            onPaneClick={() => setSelectedGraphNode(null)}
            fitView
            attributionPosition="bottom-left"
            className="bg-[#0b0f19]"
          >
            <Background color="#1e293b" gap={24} size={1} />
            <Controls className="!bg-[#111827] !border-gray-800 !fill-gray-300 [&>button]:!border-gray-800" />
            <MiniMap
              className="!bg-[#0f172a] !border-gray-800"
              nodeColor={(n) => {
                const node = n.data?.node as KnowledgeGraphNode;
                if (node?.type === 'capability') return '#6366f1';
                if (node?.type === 'device') return '#06b6d4';
                if (node?.type === 'command') return '#0891b2';
                if (node?.type === 'event') return '#f59e0b';
                if (node?.type === 'alarm') return '#e11d48';
                if (node?.type === 'datapoint') return '#0d9488';
                if (node?.type === 'parameter') return '#7c3aed';
                if (node?.type === 'operating_state') return '#f43f5e';
                if (node?.type === 'activity' || node?.type === 'workflow') return '#f97316';
                if (node?.type === 'datamodel') return '#0284c7';
                if (node?.type === 'operation') return '#d97706';
                return '#475569';
              }}
            />
          </ReactFlow>

          {/* Legend Overlay in Canvas */}
          <div className="absolute bottom-4 left-4 bg-[#111827]/90 backdrop-blur-md border border-gray-800 p-2.5 rounded-xl text-[11px] space-y-1.5 shadow-xl hidden md:block select-none pointer-events-none z-10 max-h-72 overflow-y-auto">
            <div className="flex items-center justify-between gap-3 mb-1 border-b border-gray-800/80 pb-1">
              <span className="font-bold text-gray-300 text-[10px] uppercase tracking-wider">Knowledge Ontology</span>
              <span className="text-[9px] px-1.5 py-0.5 rounded bg-blue-950/80 text-blue-300 font-mono border border-blue-800/40">
                {layoutType === 'dagre-lr' && 'Dagre LR'}
                {layoutType === 'dagre-tb' && 'Dagre TB'}
                {layoutType === 'force' && 'Force Organic'}
                {layoutType === 'radial' && 'Radial Orbits'}
                {layoutType === 'layered' && 'Metamodel Layers'}
              </span>
            </div>
            <div className="flex items-center gap-2 text-purple-300"><span className="w-2.5 h-2.5 rounded-full bg-purple-500"></span> Domain / Project Root</div>
            <div className="flex items-center gap-2 text-cyan-300"><span className="w-2.5 h-2.5 rounded-full bg-cyan-500"></span> Physical Devices</div>
            <div className="flex items-center gap-2 text-indigo-300"><span className="w-2.5 h-2.5 rounded-full bg-indigo-500"></span> Semantic Capabilities (.cap)</div>
            <div className="flex items-center gap-2 text-cyan-400"><span className="w-2.5 h-2.5 rounded-full bg-cyan-600"></span> Commands (Control)</div>
            <div className="flex items-center gap-2 text-amber-300"><span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span> Events / Operations (.op)</div>
            <div className="flex items-center gap-2 text-rose-300"><span className="w-2.5 h-2.5 rounded-full bg-rose-500"></span> Alarms & States (MNC-ML)</div>
            <div className="flex items-center gap-2 text-teal-300"><span className="w-2.5 h-2.5 rounded-full bg-teal-500"></span> DataPoints (Telemetry)</div>
            <div className="flex items-center gap-2 text-sky-300"><span className="w-2.5 h-2.5 rounded-full bg-sky-500"></span> Data Models (.dml)</div>
            <div className="flex items-center gap-2 text-violet-300"><span className="w-2.5 h-2.5 rounded-full bg-violet-500"></span> Parameters & Types</div>
            <div className="flex items-center gap-2 text-orange-300"><span className="w-2.5 h-2.5 rounded-full bg-orange-500"></span> Activities & Workflows (.activity)</div>
          </div>
        </div>

        {/* Entity Inspection & 1-Click Reusability Drawer */}
        {selectedGraphNode && (
          <aside 
            aria-label="Entity Inspection & Reusability Drawer"
            className="w-80 md:w-96 bg-[#0f172a]/95 border-l border-gray-800 shadow-2xl backdrop-blur-md flex flex-col z-20 animate-in slide-in-from-right duration-200"
          >
            {/* Drawer Header */}
            <div className="p-4 bg-[#111827] border-b border-gray-800 flex items-start justify-between">
              <div>
                <div className="flex items-center gap-2 mb-1">
                  <span className="text-[10px] font-bold uppercase tracking-wider px-2 py-0.5 rounded bg-blue-900/50 text-blue-300 border border-blue-700/40">
                    {selectedGraphNode.type}
                  </span>
                  {selectedGraphNode.category && (
                    <span className="text-[10px] text-gray-400 truncate max-w-[180px]">
                      {selectedGraphNode.category}
                    </span>
                  )}
                </div>
                <h3 className="text-sm font-bold text-white leading-snug">
                  {selectedGraphNode.name}
                </h3>
              </div>
              <button
                onClick={() => setSelectedGraphNode(null)}
                className="text-gray-400 hover:text-white p-1 rounded hover:bg-gray-800 transition"
                title="Close Drawer"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            {/* Drawer Content */}
            <div className="flex-1 overflow-y-auto p-4 space-y-4 text-xs">
              
              {/* Success Notification */}
              {importSuccessMsg && (
                <div className="p-3 bg-emerald-950/60 border border-emerald-700/50 rounded-xl text-emerald-300 text-xs flex items-center justify-between animate-in fade-in">
                  <div className="flex items-center gap-2">
                    <CheckCircle2 className="w-4 h-4 shrink-0 text-emerald-400" />
                    <span>{importSuccessMsg}</span>
                  </div>
                  <button
                    onClick={() => setActiveView('editor')}
                    className="ml-2 underline font-semibold hover:text-white shrink-0"
                  >
                    View Code
                  </button>
                </div>
              )}

              {/* Reusability Action Callout */}
              <div className="p-3 bg-gradient-to-r from-blue-950/40 to-indigo-950/40 border border-blue-800/40 rounded-xl space-y-2">
                <div className="flex items-center justify-between">
                  <span className="font-semibold text-gray-200">Reusability Service</span>
                  <span className="text-[10px] text-indigo-400 font-mono">1-Click Integration</span>
                </div>
                <p className="text-[11px] text-gray-400 leading-relaxed">
                  Import this pre-available knowledge artifact directly into your active project workspace.
                </p>

                {selectedGraphNode.type === 'domain' ? (
                  <button
                    onClick={() => handleImportDomain(selectedGraphNode.properties?.raw_id || selectedGraphNode.id.replace('cat_', ''), selectedGraphNode.name)}
                    disabled={importingEntityId === selectedGraphNode.id}
                    className="w-full py-2 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white rounded-lg font-semibold flex items-center justify-center gap-1.5 shadow transition"
                  >
                    {importingEntityId === selectedGraphNode.id ? (
                      <Loader2 className="w-3.5 h-3.5 animate-spin" />
                    ) : (
                      <Plus className="w-3.5 h-3.5" />
                    )}
                    <span>Import Full Domain Bundle</span>
                  </button>
                ) : (
                  <div className="space-y-1.5">
                    <button
                      onClick={() => handleImportEntity(selectedGraphNode)}
                      disabled={importingEntityId === selectedGraphNode.id}
                      className="w-full py-2 bg-blue-600 hover:bg-blue-500 disabled:opacity-50 text-white rounded-lg font-semibold flex items-center justify-center gap-1.5 shadow transition"
                    >
                      {importingEntityId === selectedGraphNode.id ? (
                        <Loader2 className="w-3.5 h-3.5 animate-spin" />
                      ) : (
                        <Plus className="w-3.5 h-3.5" />
                      )}
                      <span>Import Entity into Project</span>
                    </button>

                    {selectedGraphNode.properties?.parent_domain && storeSummary?.domains && (
                      <button
                        onClick={() => {
                          const dom = storeSummary.domains.find(d => d.name === selectedGraphNode.properties?.parent_domain);
                          if (dom) handleImportDomain(dom.id, dom.name);
                        }}
                        className="w-full py-1.5 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded-lg text-[11px] font-medium transition"
                      >
                        Import Parent Domain Bundle
                      </button>
                    )}
                  </div>
                )}
              </div>

              {/* Source & Reference Metadata */}
              <div className="space-y-2">
                <div className="font-semibold text-gray-300 uppercase text-[10px] tracking-wider">
                  Knowledge Provenance
                </div>
                {selectedGraphNode.source_file && (
                  <div className="flex items-center gap-2 text-gray-300 bg-gray-900/60 p-2 rounded-lg border border-gray-800">
                    <FileCode2 className="w-3.5 h-3.5 text-blue-400 shrink-0" />
                    <span className="font-mono text-xs">{selectedGraphNode.source_file}</span>
                  </div>
                )}
                {selectedGraphNode.specification_reference && (
                  <div className="flex items-start gap-2 text-gray-400 bg-gray-900/60 p-2 rounded-lg border border-gray-800">
                    <BookOpen className="w-3.5 h-3.5 text-purple-400 shrink-0 mt-0.5" />
                    <span className="text-[11px]">{selectedGraphNode.specification_reference}</span>
                  </div>
                )}
              </div>

              {/* Entity Specific Properties */}
              {selectedGraphNode.properties && Object.keys(selectedGraphNode.properties).length > 0 && (
                <div className="space-y-2">
                  <div className="font-semibold text-gray-300 uppercase text-[10px] tracking-wider">
                    Specification Properties
                  </div>
                  <div className="bg-gray-900/80 rounded-lg border border-gray-800 p-2.5 space-y-2 font-mono text-[11px]">
                    {selectedGraphNode.properties.parent_domain && (
                      <div className="flex justify-between">
                        <span className="text-gray-500">Domain:</span>
                        <span className="text-gray-300">{selectedGraphNode.properties.parent_domain}</span>
                      </div>
                    )}
                    {selectedGraphNode.properties.package && (
                      <div className="flex justify-between">
                        <span className="text-gray-500">Package:</span>
                        <span className="text-gray-300">{selectedGraphNode.properties.package}</span>
                      </div>
                    )}
                    {selectedGraphNode.properties.commands && (
                      <div>
                        <span className="text-gray-500">Commands:</span>
                        <div className="text-cyan-400 pl-2">
                          {selectedGraphNode.properties.commands.join(', ') || 'None'}
                        </div>
                      </div>
                    )}
                    {selectedGraphNode.properties.events && (
                      <div>
                        <span className="text-gray-500">Events:</span>
                        <div className="text-emerald-400 pl-2">
                          {selectedGraphNode.properties.events.join(', ') || 'None'}
                        </div>
                      </div>
                    )}
                    {selectedGraphNode.properties.alarms && (
                      <div>
                        <span className="text-gray-500">Alarms:</span>
                        <div className="text-rose-400 pl-2">
                          {selectedGraphNode.properties.alarms.join(', ') || 'None'}
                        </div>
                      </div>
                    )}
                    {selectedGraphNode.properties.datapoints && (
                      <div>
                        <span className="text-gray-500">DataPoints:</span>
                        <div className="text-amber-400 pl-2">
                          {selectedGraphNode.properties.datapoints.join(', ') || 'None'}
                        </div>
                      </div>
                    )}
                    {selectedGraphNode.properties.script && (
                      <div>
                        <span className="text-gray-500">Execution Script:</span>
                        <div className="text-indigo-300 bg-gray-950 p-1.5 rounded border border-gray-800 mt-1 overflow-x-auto text-[10px]">
                          {selectedGraphNode.properties.script}
                        </div>
                      </div>
                    )}
                    {selectedGraphNode.properties.required_capability && (
                      <div className="flex justify-between">
                        <span className="text-gray-500">Requires Cap:</span>
                        <span className="text-indigo-400">{selectedGraphNode.properties.required_capability}</span>
                      </div>
                    )}
                    {selectedGraphNode.properties.requires_operation && (
                      <div className="flex justify-between">
                        <span className="text-gray-500">Requires Op:</span>
                        <span className="text-amber-400">{selectedGraphNode.properties.requires_operation.join(', ')}</span>
                      </div>
                    )}
                  </div>
                </div>
              )}

              {/* Connected Relationships in Graph */}
              <div className="space-y-3">
                <div className="font-semibold text-gray-300 uppercase text-[10px] tracking-wider">
                  Knowledge Graph Topology
                </div>

                {/* Outgoing Connections */}
                {selectedNodeRelations.outgoing.length > 0 && (
                  <div className="space-y-1.5">
                    <span className="text-[11px] text-gray-400 flex items-center gap-1 font-medium">
                      <ArrowRight className="w-3 h-3 text-blue-400" />
                      <span>Outgoing Dependencies ({selectedNodeRelations.outgoing.length}):</span>
                    </span>
                    <div className="space-y-1">
                      {selectedNodeRelations.outgoing.map((rel: any, idx: number) => (
                        <button
                          key={idx}
                          onClick={() => setSelectedGraphNode(rel.node)}
                          className="w-full text-left p-2 rounded-lg bg-gray-900/60 hover:bg-gray-800 border border-gray-800 hover:border-blue-500/50 flex items-center justify-between group transition"
                        >
                          <div>
                            <span className="text-[9px] uppercase font-bold text-gray-500 group-hover:text-blue-400 block">
                              {rel.edge.label || rel.edge.type}
                            </span>
                            <span className="font-semibold text-gray-200 group-hover:text-white text-xs">
                              {rel.node.name}
                            </span>
                          </div>
                          <ChevronRight className="w-3.5 h-3.5 text-gray-600 group-hover:text-blue-400" />
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {/* Incoming Connections */}
                {selectedNodeRelations.incoming.length > 0 && (
                  <div className="space-y-1.5">
                    <span className="text-[11px] text-gray-400 flex items-center gap-1 font-medium">
                      <ArrowLeft className="w-3 h-3 text-purple-400" />
                      <span>Incoming Connections ({selectedNodeRelations.incoming.length}):</span>
                    </span>
                    <div className="space-y-1">
                      {selectedNodeRelations.incoming.map((rel: any, idx: number) => (
                        <button
                          key={idx}
                          onClick={() => setSelectedGraphNode(rel.node)}
                          className="w-full text-left p-2 rounded-lg bg-gray-900/60 hover:bg-gray-800 border border-gray-800 hover:border-purple-500/50 flex items-center justify-between group transition"
                        >
                          <div>
                            <span className="text-[9px] uppercase font-bold text-gray-500 group-hover:text-purple-400 block">
                              {rel.edge.label || rel.edge.type}
                            </span>
                            <span className="font-semibold text-gray-200 group-hover:text-white text-xs">
                              {rel.node.name}
                            </span>
                          </div>
                          <ChevronRight className="w-3.5 h-3.5 text-gray-600 group-hover:text-purple-400" />
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {selectedNodeRelations.incoming.length === 0 && selectedNodeRelations.outgoing.length === 0 && (
                  <div className="text-gray-500 text-[11px] italic">
                    No active edges in current filtered scope.
                  </div>
                )}
              </div>
            </div>
          </aside>
        )}
      </div>

      {/* Semantic Capability Matcher Modal */}
      {showMatchModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-[#0f172a] border border-gray-800 rounded-2xl w-full max-w-2xl overflow-hidden shadow-2xl flex flex-col max-h-[85vh]">
            
            {/* Modal Header */}
            <div className="p-4 bg-[#111827] border-b border-gray-800 flex items-center justify-between">
              <div className="flex items-center gap-2.5">
                <div className="p-2 bg-indigo-600/20 text-indigo-400 rounded-lg border border-indigo-500/30">
                  <Sparkles className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-sm font-bold text-white">Semantic Capability Matcher</h3>
                  <p className="text-xs text-gray-400">Discover and bind knowledge base capabilities to your activities</p>
                </div>
              </div>
              <button 
                onClick={() => setShowMatchModal(false)}
                className="text-gray-400 hover:text-white p-1 rounded hover:bg-gray-800"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Modal Body */}
            <div className="p-4 space-y-4 overflow-y-auto flex-1">
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="Enter process requirement (e.g. 'read temperature sensor', 'actuate barrier gate', 'vacuum gripper', 'chemical reactor')"
                  value={matchQuery}
                  onChange={(e) => setMatchQuery(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleRunMatch()}
                  className="flex-1 bg-gray-900 border border-gray-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-indigo-500"
                />
                <button
                  onClick={handleRunMatch}
                  disabled={isMatching || !matchQuery.trim()}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 disabled:opacity-50 text-white rounded-lg text-xs font-semibold flex items-center gap-1.5 transition"
                >
                  {isMatching ? <Loader2 className="w-4 h-4 animate-spin" /> : <Search className="w-4 h-4" />}
                  <span>Find Matches</span>
                </button>
              </div>

              {/* Recommended Matches List */}
              <div className="space-y-2.5 mt-2">
                {matchingResults.map((match, idx) => (
                  <div key={idx} className="p-3 bg-gray-900/70 border border-gray-800 hover:border-indigo-500/40 rounded-xl transition">
                    <div className="flex items-center justify-between mb-1.5">
                      <div className="flex items-center gap-2">
                        <span className="font-semibold text-xs text-white">{match.system_name}</span>
                        <span className="text-[10px] text-gray-400">&bull; {match.category}</span>
                      </div>
                      <span className="text-xs font-bold text-indigo-400 bg-indigo-950/60 px-2 py-0.5 rounded-full border border-indigo-700/40">
                        {match.score}% Match
                      </span>
                    </div>

                    <div className="text-[11px] text-gray-400 mb-2">
                      <span className="text-gray-500 font-medium">Equipped Devices:</span> {match.devices.join(', ')}
                    </div>

                    {match.capabilities.length > 0 && (
                      <div className="space-y-1">
                        <div className="text-[10px] font-bold text-indigo-300 uppercase tracking-wider">Compatible Capabilities:</div>
                        {match.capabilities.map((cap, cIdx) => (
                          <div key={cIdx} className="bg-[#0b0f19] p-2 rounded-lg border border-gray-800/80 flex items-center justify-between text-xs">
                            <div>
                              <div className="font-mono text-indigo-400 font-semibold">{cap.name}</div>
                              <div className="text-[10px] text-gray-400">
                                Interface: <span className="text-gray-300">{cap.interface}</span>
                              </div>
                            </div>
                            <button
                              onClick={() => {
                                if (projectId) {
                                  knowledgeApi.importKnowledge(projectId, match.catalog_id).then(() => {
                                    alert(`Imported ${cap.name} bundle into project!`);
                                    setShowMatchModal(false);
                                    fetchGraph();
                                  });
                                }
                              }}
                              className="px-2.5 py-1 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-medium flex items-center gap-1 transition"
                            >
                              <Plus className="w-3 h-3" />
                              <span>Import</span>
                            </button>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                ))}

                {matchingResults.length === 0 && !isMatching && (
                  <div className="text-center py-8 text-gray-500 text-xs">
                    Type an industrial requirement above to query the Knowledge Graph.
                  </div>
                )}
              </div>
            </div>

            {/* Modal Footer */}
            <div className="p-3 bg-[#111827] border-t border-gray-800 flex justify-end">
              <button
                onClick={() => setShowMatchModal(false)}
                className="px-4 py-1.5 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded-lg text-xs font-semibold transition"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
