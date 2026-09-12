import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { 
  ReactFlow, Background, Controls, MiniMap, 
  Node, Edge, MarkerType, Handle, Position, NodeProps 
} from '@xyflow/react';
import '@xyflow/react/dist/style.css';
import { useEditorStore } from '../../stores/editorStore';
import { knowledgeApi, KnowledgeGraphData, KnowledgeGraphNode, CapabilityMatch } from '../../api/knowledge';
import { 
  Network, Cpu, Shield, Zap, Wrench, Database, 
  Activity, GitMerge, Search, Download, Sparkles, 
  Layers, X, Loader2, RefreshCw
} from 'lucide-react';

// Custom Knowledge Node Component
const KnowledgeNodeComponent: React.FC<NodeProps> = ({ data, selected }) => {
  const node = data.node as KnowledgeGraphNode;
  const type = node.type;

  const getTypeStyle = () => {
    switch (type) {
      case 'domain':
      case 'repository':
      case 'project':
        return {
          border: 'border-purple-500/70',
          bg: 'bg-purple-950/40',
          text: 'text-purple-300',
          badgeBg: 'bg-purple-900/60',
          icon: Network
        };
      case 'device':
        return {
          border: 'border-cyan-500/70',
          bg: 'bg-cyan-950/40',
          text: 'text-cyan-300',
          badgeBg: 'bg-cyan-900/60',
          icon: Cpu
        };
      case 'interface':
        return {
          border: 'border-emerald-500/70',
          bg: 'bg-emerald-950/40',
          text: 'text-emerald-300',
          badgeBg: 'bg-emerald-900/60',
          icon: Shield
        };
      case 'capability':
        return {
          border: 'border-indigo-500/70',
          bg: 'bg-indigo-950/40',
          text: 'text-indigo-300',
          badgeBg: 'bg-indigo-900/60',
          icon: Zap
        };
      case 'operation':
        return {
          border: 'border-amber-500/70',
          bg: 'bg-amber-950/40',
          text: 'text-amber-300',
          badgeBg: 'bg-amber-900/60',
          icon: Wrench
        };
      case 'datamodel':
        return {
          border: 'border-sky-500/70',
          bg: 'bg-sky-950/40',
          text: 'text-sky-300',
          badgeBg: 'bg-sky-900/60',
          icon: Database
        };
      case 'activity':
      case 'workflow':
        return {
          border: 'border-orange-500/70',
          bg: 'bg-orange-950/40',
          text: 'text-orange-300',
          badgeBg: 'bg-orange-900/60',
          icon: Activity
        };
      case 'operating_state':
        return {
          border: 'border-rose-500/70',
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

  return (
    <div className={`px-3 py-2.5 rounded-xl border-2 shadow-lg backdrop-blur-md transition-all min-w-[190px] max-w-[240px] select-none ${
      style.bg
    } ${style.border} ${
      selected ? 'ring-2 ring-blue-400 ring-offset-2 ring-offset-[#0b0f19] scale-[1.02]' : 'hover:scale-[1.01]'
    }`}>
      {/* 4-Way Handles for clean Bezier routing */}
      <Handle type="target" position={Position.Left} id="left-in" className="w-2.5 h-2.5 !bg-gray-400 !border-2 !border-gray-900" />
      <Handle type="source" position={Position.Right} id="right-out" className="w-2.5 h-2.5 !bg-gray-400 !border-2 !border-gray-900" />
      <Handle type="target" position={Position.Top} id="top-in" className="w-2.5 h-2.5 !bg-gray-400 !border-2 !border-gray-900" />
      <Handle type="source" position={Position.Bottom} id="bottom-out" className="w-2.5 h-2.5 !bg-gray-400 !border-2 !border-gray-900" />

      {/* Header with Type Badge */}
      <div className="flex items-center justify-between gap-1 mb-1">
        <span className={`text-[9px] font-bold uppercase tracking-wider px-2 py-0.5 rounded-full ${style.badgeBg} ${style.text}`}>
          {node.type.replace('_', ' ')}
        </span>
        <Icon className={`w-3.5 h-3.5 ${style.text}`} />
      </div>

      {/* Node Name */}
      <div className="font-semibold text-xs text-gray-100 truncate" title={node.name}>
        {node.name}
      </div>

      {/* Category / Context subtitle */}
      <div className="text-[10px] text-gray-400 truncate mt-0.5">
        {node.category || node.properties?.parent_domain || 'Ontology Entity'}
      </div>

      {/* Thesis Reference or Source File */}
      {node.source_file && (
        <div className="mt-1 text-[9px] font-mono text-blue-400/80 truncate">
          📄 {node.source_file}
        </div>
      )}
    </div>
  );
};

const nodeTypes = {
  knowledgeNode: KnowledgeNodeComponent
};

export const KnowledgeGraphViewer: React.FC = () => {
  const { projectId, projectName } = useEditorStore();
  const [scope, setScope] = useState<'project' | 'global'>('project');
  const [graphData, setGraphData] = useState<KnowledgeGraphData | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [selectedNode, setSelectedNode] = useState<KnowledgeGraphNode | null>(null);
  const [filterType, setFilterType] = useState<string>('all');
  const [searchQuery, setSearchQuery] = useState<string>('');

  // Semantic capability matching modal state
  const [showMatchModal, setShowMatchModal] = useState<boolean>(false);
  const [matchQuery, setMatchQuery] = useState<string>('');
  const [matchingResults, setMatchingResults] = useState<CapabilityMatch[]>([]);
  const [isMatching, setIsMatching] = useState<boolean>(false);

  // Fetch Graph Data based on Scope
  const fetchGraph = useCallback(async () => {
    setIsLoading(true);
    try {
      if (scope === 'project' && projectId) {
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
  }, [scope, projectId]);

  useEffect(() => {
    fetchGraph();
  }, [fetchGraph]);

  // Generate topological layered layout
  const { nodes, edges } = useMemo(() => {
    if (!graphData) return { nodes: [], edges: [] };

    const q = searchQuery.toLowerCase().trim();
    const filteredNodesList = graphData.nodes.filter(n => {
      const matchSearch = !q || n.name.toLowerCase().includes(q) || (n.category && n.category.toLowerCase().includes(q));
      const matchType = filterType === 'all' || n.type === filterType;
      return matchSearch && matchType;
    });

    const filteredIds = new Set(filteredNodesList.map(n => n.id));

    // Group nodes by Layer / Category for clean layout
    const layer0 = filteredNodesList.filter(n => ['domain', 'repository', 'project'].includes(n.type));
    const layer1 = filteredNodesList.filter(n => ['device', 'interface'].includes(n.type));
    const layer2 = filteredNodesList.filter(n => ['capability', 'datamodel'].includes(n.type));
    const layer3 = filteredNodesList.filter(n => ['operation', 'activity', 'workflow'].includes(n.type));
    const layer4 = filteredNodesList.filter(n => ['operating_state'].includes(n.type));

    const layers = [layer0, layer1, layer2, layer3, layer4];
    const positionedNodes: Node[] = [];

    layers.forEach((layerNodes, colIdx) => {
      const x = 80 + colIdx * 300;
      layerNodes.forEach((n, rowIdx) => {
        const y = 80 + rowIdx * 110;
        positionedNodes.push({
          id: n.id,
          type: 'knowledgeNode',
          position: { x, y },
          data: { node: n }
        });
      });
    });

    // Edges with smooth curves & labels
    const positionedEdges: Edge[] = graphData.edges
      .filter(e => filteredIds.has(e.source) && filteredIds.has(e.target))
      .map(e => ({
        id: e.id,
        source: e.source,
        target: e.target,
        sourceHandle: 'right-out',
        targetHandle: 'left-in',
        type: 'smoothstep',
        label: e.label,
        style: { stroke: '#475569', strokeWidth: 1.5 },
        labelStyle: { fill: '#94a3b8', fontSize: 10, fontFamily: 'monospace' },
        labelBgStyle: { fill: '#0f172a', fillOpacity: 0.8 },
        markerEnd: {
          type: MarkerType.ArrowClosed,
          color: '#64748b',
          width: 14,
          height: 14
        }
      }));

    return { nodes: positionedNodes, edges: positionedEdges };
  }, [graphData, filterType, searchQuery]);

  const handleNodeClick = (_: any, n: Node) => {
    const raw = graphData?.nodes.find(item => item.id === n.id);
    if (raw) setSelectedNode(raw);
  };

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
        
        {/* Left: Scope Switcher & Search */}
        <div className="flex items-center gap-3">
          <div className="flex bg-[#1a2333] p-0.5 rounded-lg border border-gray-700/60">
            <button
              onClick={() => setScope('project')}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition ${
                scope === 'project' ? 'bg-blue-600 text-white shadow-sm' : 'text-gray-400 hover:text-white'
              }`}
            >
              <span>📁 Project Graph</span>
            </button>
            <button
              onClick={() => setScope('global')}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-semibold transition ${
                scope === 'global' ? 'bg-purple-600 text-white shadow-sm' : 'text-gray-400 hover:text-white'
              }`}
              title="Explore the global thesis equipment & ontology repository"
            >
              <span>🌐 Global Repository</span>
            </button>
          </div>

          {/* Search Bar */}
          <div className="relative">
            <Search className="w-3.5 h-3.5 absolute left-2.5 top-2 text-gray-500" />
            <input
              type="text"
              placeholder="Search entities..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="bg-gray-900 border border-gray-700 rounded-md pl-8 pr-3 py-1 text-xs text-gray-200 placeholder-gray-500 focus:outline-none focus:border-blue-500 w-40 md:w-56"
            />
          </div>
        </div>

        {/* Center: Filter Type Pills */}
        <div className="hidden xl:flex items-center gap-1">
          {[
            { id: 'all', label: 'All' },
            { id: 'device', label: 'Devices' },
            { id: 'capability', label: 'Capabilities' },
            { id: 'operation', label: 'Operations' },
            { id: 'datamodel', label: 'Data Models' },
            { id: 'activity', label: 'Activities' },
            { id: 'operating_state', label: 'States' },
          ].map(tab => (
            <button
              key={tab.id}
              onClick={() => setFilterType(tab.id)}
              className={`px-2.5 py-1 rounded-md text-[11px] font-medium transition ${
                filterType === tab.id
                  ? 'bg-gray-800 text-blue-400 border border-blue-500/30'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/40'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Right: Actions */}
        <div className="flex items-center gap-2">
          {graphData && (
            <span className="hidden md:inline-flex text-[11px] text-gray-400 font-mono bg-gray-900 px-2.5 py-1 rounded-md border border-gray-800">
              {graphData.stats.total_nodes} Nodes &bull; {graphData.stats.total_edges} Edges
            </span>
          )}

          <button
            onClick={() => setShowMatchModal(true)}
            className="flex items-center gap-1.5 px-3 py-1 bg-indigo-600 hover:bg-indigo-500 text-white rounded-md text-xs font-semibold shadow-sm transition"
            title="Search for device capabilities to match your activity diagrams"
          >
            <Sparkles className="w-3.5 h-3.5" />
            <span>Match Capabilities</span>
          </button>

          {scope === 'project' && (
            <button
              onClick={handleExportRdf}
              className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded-md text-xs border border-gray-700 transition"
              title="Export as W3C RDF Turtle ontology"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Export RDF (.ttl)</span>
            </button>
          )}

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
      <div className="flex-1 relative">
        {isLoading ? (
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-[#0b0f19] text-gray-400 gap-3 z-10">
            <Loader2 className="w-8 h-8 animate-spin text-blue-500" />
            <span className="text-xs">Building Knowledge Graph from models...</span>
          </div>
        ) : null}

        <ReactFlow
          nodes={nodes}
          edges={edges}
          nodeTypes={nodeTypes}
          onNodeClick={handleNodeClick}
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
              if (node?.type === 'operating_state') return '#f43f5e';
              if (node?.type === 'activity') return '#f97316';
              return '#475569';
            }}
          />
        </ReactFlow>

        {/* Legend Overlay in Canvas */}
        <div className="absolute bottom-4 left-4 bg-[#111827]/90 backdrop-blur-md border border-gray-800 p-2.5 rounded-xl text-[11px] space-y-1.5 shadow-xl hidden md:block select-none pointer-events-none">
          <div className="font-bold text-gray-300 text-[10px] uppercase tracking-wider mb-1">Knowledge Ontology Layers</div>
          <div className="flex items-center gap-2 text-purple-300"><span className="w-2.5 h-2.5 rounded-full bg-purple-500"></span> Domain / Project Root</div>
          <div className="flex items-center gap-2 text-cyan-300"><span className="w-2.5 h-2.5 rounded-full bg-cyan-500"></span> Physical Devices (Sensors/Actuators)</div>
          <div className="flex items-center gap-2 text-indigo-300"><span className="w-2.5 h-2.5 rounded-full bg-indigo-500"></span> Semantic Capabilities (.cap)</div>
          <div className="flex items-center gap-2 text-amber-300"><span className="w-2.5 h-2.5 rounded-full bg-amber-500"></span> Device Operations (.op)</div>
          <div className="flex items-center gap-2 text-orange-300"><span className="w-2.5 h-2.5 rounded-full bg-orange-500"></span> Process Activities (.activity)</div>
          <div className="flex items-center gap-2 text-rose-300"><span className="w-2.5 h-2.5 rounded-full bg-rose-500"></span> Supervisory States (MNC-ML)</div>
        </div>
      </div>

      {/* Right Slide-over Inspector Drawer */}
      {selectedNode && (
        <div className="absolute right-0 top-12 bottom-0 w-80 bg-[#0d121d] border-l border-gray-800 shadow-2xl p-4 flex flex-col justify-between z-20 overflow-y-auto">
          <div>
            <div className="flex items-center justify-between pb-3 border-b border-gray-800">
              <div className="flex items-center gap-2">
                <span className="text-xs font-bold text-gray-200">Entity Details</span>
                <span className="text-[10px] font-mono bg-blue-900/40 text-blue-300 px-2 py-0.5 rounded border border-blue-700/40">
                  {selectedNode.type}
                </span>
              </div>
              <button 
                onClick={() => setSelectedNode(null)}
                className="text-gray-400 hover:text-white p-1 rounded hover:bg-gray-800"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <div className="mt-4 space-y-4">
              <div>
                <label className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Name</label>
                <div className="text-sm font-semibold text-white mt-0.5">{selectedNode.name}</div>
              </div>

              <div>
                <label className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Category</label>
                <div className="text-xs text-gray-300 mt-0.5">{selectedNode.category}</div>
              </div>

              {selectedNode.thesis_reference && (
                <div>
                  <label className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Thesis Reference</label>
                  <div className="text-xs text-amber-300 font-mono mt-0.5 bg-amber-950/30 p-1.5 rounded border border-amber-800/40">
                    📖 {selectedNode.thesis_reference}
                  </div>
                </div>
              )}

              {selectedNode.source_file && (
                <div>
                  <label className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Source Model File</label>
                  <div className="text-xs text-blue-400 font-mono mt-0.5">
                    📄 {selectedNode.source_file}
                  </div>
                </div>
              )}

              {/* Node Properties */}
              {selectedNode.properties && Object.keys(selectedNode.properties).length > 0 && (
                <div>
                  <label className="text-[10px] font-bold uppercase tracking-wider text-gray-500">Attributes & Contracts</label>
                  <div className="mt-1 bg-gray-900/80 p-2 rounded-lg border border-gray-800 text-[11px] font-mono space-y-1.5 text-gray-300 max-h-48 overflow-y-auto">
                    {Object.entries(selectedNode.properties).map(([k, v]) => (
                      <div key={k} className="border-b border-gray-800/60 pb-1 last:border-none">
                        <span className="text-gray-500">{k}: </span>
                        <span className="text-gray-200">
                          {Array.isArray(v) 
                            ? v.map((item: any) => typeof item === 'object' ? item.name || JSON.stringify(item) : String(item)).join(', ') || '[]'
                            : typeof v === 'object' ? JSON.stringify(v) : String(v)}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className="pt-4 border-t border-gray-800">
            <button
              onClick={() => setSelectedNode(null)}
              className="w-full py-2 bg-gray-800 hover:bg-gray-700 text-gray-200 rounded-lg text-xs font-semibold transition"
            >
              Close Inspector
            </button>
          </div>
        </div>
      )}

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
                  placeholder="Enter process requirement (e.g. 'read temperature sensor', 'actuate barrier gate', 'vacuum gripper')"
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
                                    alert(`Imported ${cap.name} and related ontology into project!`);
                                    setShowMatchModal(false);
                                    fetchGraph();
                                  });
                                }
                              }}
                              className="px-2.5 py-1 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-medium flex items-center gap-1 transition"
                            >
                              <PlusIcon className="w-3 h-3" />
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
                    Type an industrial requirement above to query the thesis Knowledge Graph.
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

// Helper icon
const PlusIcon = ({ className }: { className?: string }) => (
  <svg className={className} fill="none" viewBox="0 0 24 24" stroke="currentColor">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
  </svg>
);
