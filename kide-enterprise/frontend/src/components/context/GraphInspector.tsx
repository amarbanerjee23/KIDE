import React, { useMemo } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { CollapsibleSection } from './CollapsibleSection';
import { 
  Network, Cpu, Zap, Wrench, Database, 
  Activity, GitMerge, FileCode2, ExternalLink, X, 
  ArrowRight, ArrowLeft, Layers, Sparkles
} from 'lucide-react';

export const GraphInspector: React.FC = () => {
  const { 
    graphScope, 
    graphData, 
    selectedGraphNode, 
    setSelectedGraphNode,
    files, 
    setActiveFileId, 
    setActiveView 
  } = useEditorStore();

  // Categorize entity counts in the graph
  const typeCounts = useMemo(() => {
    if (!graphData || !graphData.nodes) return {};
    const counts: Record<string, number> = {};
    for (const node of graphData.nodes) {
      counts[node.type] = (counts[node.type] || 0) + 1;
    }
    return counts;
  }, [graphData]);

  // Compute incoming and outgoing connections for the selected node
  const connections = useMemo(() => {
    if (!selectedGraphNode || !graphData || !graphData.edges) {
      return { incoming: [], outgoing: [] };
    }
    const nodeId = selectedGraphNode.id;
    const nodeName = selectedGraphNode.name;

    const incoming = (graphData.edges as any[])
      .filter((e: any) => e.target === nodeId || e.target === nodeName)
      .map((e: any) => {
        const sourceNode = (graphData.nodes as any[]).find((n: any) => n.id === e.source || n.name === e.source);
        return {
          name: sourceNode?.name || e.source,
          type: sourceNode?.type || 'entity',
          label: formatEdgeLabel(e.label || 'connects to'),
          node: sourceNode
        };
      });

    const outgoing = (graphData.edges as any[])
      .filter((e: any) => e.source === nodeId || e.source === nodeName)
      .map((e: any) => {
        const targetNode = (graphData.nodes as any[]).find((n: any) => n.id === e.target || n.name === e.target);
        return {
          name: targetNode?.name || e.target,
          type: targetNode?.type || 'entity',
          label: formatEdgeLabel(e.label || 'connects to'),
          node: targetNode
        };
      });

    return { incoming, outgoing };
  }, [selectedGraphNode, graphData]);

  // Format raw ontology edges to human-readable text
  function formatEdgeLabel(raw: string): string {
    const clean = raw.toLowerCase().replace(/_/g, ' ');
    if (clean.includes('requires')) return 'Requires';
    if (clean.includes('provides')) return 'Provides';
    if (clean.includes('uses')) return 'Uses';
    if (clean.includes('implements')) return 'Implements';
    if (clean.includes('depends')) return 'Depends on';
    if (clean.includes('transition')) return 'Transitions to';
    if (clean.includes('child')) return 'Child component';
    return clean.charAt(0).toUpperCase() + clean.slice(1);
  }

  // Handle clicking an entity connection to inspect it
  const handleSelectConnectedNode = (node: any) => {
    if (node) {
      setSelectedGraphNode(node);
    }
  };

  // Open definition file in editor
  const handleOpenDefinition = (sourceFile: string) => {
    const match = files.find(f => f.name.toLowerCase() === sourceFile.toLowerCase() || f.name.toLowerCase().includes(sourceFile.toLowerCase()));
    if (match) {
      setActiveFileId(match.id);
      setActiveView('editor');
    }
  };

  // -------------------------------------------------------------
  // STATE A: NOTHING SPECIFICALLY SELECTED (Graph Overview)
  // -------------------------------------------------------------
  if (!selectedGraphNode) {
    const totalNodes = graphData?.stats?.total_nodes || graphData?.nodes?.length || 0;
    const totalEdges = graphData?.stats?.total_edges || graphData?.edges?.length || 0;
    const isGlobal = graphScope === 'global';

    return (
      <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
        {/* Graph Overview Summary Banner */}
        <div className="p-3.5 border-b border-gray-800 bg-[#111622]/40">
          <div className="flex items-center gap-1.5 text-blue-400 mb-1">
            <Network size={14} />
            <span className="text-xs font-bold uppercase tracking-wider text-gray-200">
              {isGlobal ? 'Global Thesis Repository' : 'Project Knowledge Graph'}
            </span>
          </div>
          <div className="text-[11px] text-gray-400">
            {isGlobal ? 'Cross-Domain Equipment & Ontology Catalog' : 'Unified Semantic System Architecture'}
          </div>
          <div className="mt-2 text-xs font-mono text-gray-300 font-semibold flex items-center gap-2">
            <span>{totalNodes} Nodes</span>
            <span className="text-gray-600">&bull;</span>
            <span>{totalEdges} Relationships</span>
          </div>
        </div>

        {/* Entity Types Breakdown */}
        <CollapsibleSection 
          title="Entity Types" 
          count={Object.keys(typeCounts).length}
          icon={<Layers size={13} className="text-indigo-400" />}
          hideIfZero={false}
        >
          <div className="space-y-1 mt-1">
            {[
              { id: 'device', label: 'Physical Devices', icon: Cpu, color: 'text-cyan-400', border: 'border-cyan-800/40 bg-cyan-950/20' },
              { id: 'capability', label: 'Semantic Capabilities', icon: Zap, color: 'text-indigo-400', border: 'border-indigo-800/40 bg-indigo-950/20' },
              { id: 'operation', label: 'Device Operations', icon: Wrench, color: 'text-amber-400', border: 'border-amber-800/40 bg-amber-950/20' },
              { id: 'datamodel', label: 'Data Models', icon: Database, color: 'text-sky-400', border: 'border-sky-800/40 bg-sky-950/20' },
              { id: 'activity', label: 'Process Activities', icon: Activity, color: 'text-orange-400', border: 'border-orange-800/40 bg-orange-950/20' },
              { id: 'operating_state', label: 'Supervisory States', icon: GitMerge, color: 'text-rose-400', border: 'border-rose-800/40 bg-rose-950/20' },
            ].map(item => {
              const count = typeCounts[item.id] || 0;
              if (count === 0) return null;
              const Icon = item.icon;
              return (
                <div 
                  key={item.id}
                  className={`flex items-center justify-between p-1.5 rounded border ${item.border} text-xs`}
                >
                  <div className="flex items-center gap-2">
                    <Icon size={13} className={item.color} />
                    <span className="text-gray-300 font-medium">{item.label}</span>
                  </div>
                  <span className="font-mono text-gray-400 font-bold">{count}</span>
                </div>
              );
            })}
          </div>
        </CollapsibleSection>

        {/* Instructional Empty State */}
        <div className="p-4 mx-3 my-4 rounded-xl border border-dashed border-gray-800 bg-gray-900/30 text-center">
          <Sparkles className="w-5 h-5 text-blue-400/80 mx-auto mb-2" />
          <div className="text-xs font-semibold text-gray-300">Select a Graph Node</div>
          <p className="text-[11px] text-gray-500 mt-1 leading-relaxed">
            Click any node or relationship on the canvas to inspect its semantic contracts, dependencies, and connections.
          </p>
        </div>
      </div>
    );
  }

  // -------------------------------------------------------------
  // STATE C: GRAPH NODE SELECTED
  // -------------------------------------------------------------
  const props = selectedGraphNode.properties || {};
  const propEntries = Object.entries(props).filter(([k]) => k !== 'parent_domain');

  return (
    <div className="flex-1 flex flex-col min-h-0 overflow-y-auto select-none">
      {/* Selected Node Quick Controls */}
      <div className="p-3 border-b border-gray-800 bg-[#111622]/60 flex items-center justify-between">
        <div className="text-[11px] text-gray-400 font-medium">
          {connections.incoming.length} incoming &bull; {connections.outgoing.length} outgoing
        </div>
        <button
          onClick={() => setSelectedGraphNode(null)}
          className="text-gray-500 hover:text-gray-300 text-[11px] flex items-center gap-1 hover:underline"
        >
          <X size={12} />
          <span>Clear</span>
        </button>
      </div>

      {/* Outgoing Dependencies (DEPENDS ON / PROVIDES) */}
      <CollapsibleSection
        title={selectedGraphNode.type === 'capability' ? 'Provides & Dispatches' : 'Depends On / Calls'}
        count={connections.outgoing.length}
        icon={<ArrowRight size={13} className="text-emerald-400" />}
        hideIfZero={true}
      >
        <div className="space-y-1.5 mt-1">
          {connections.outgoing.map((conn: any, idx: number) => (
            <div
              key={idx}
              onClick={() => handleSelectConnectedNode(conn.node)}
              className="p-1.5 rounded bg-gray-900/70 border border-gray-800 hover:border-blue-700/60 flex items-center justify-between cursor-pointer group transition"
            >
              <div className="min-w-0">
                <div className="text-[10px] text-gray-500 uppercase tracking-wider">{conn.label}</div>
                <div className="font-semibold text-gray-200 group-hover:text-blue-300 truncate">
                  {conn.name}
                </div>
              </div>
              <span className="text-[10px] font-mono text-gray-500 group-hover:text-gray-400 uppercase">
                {conn.type}
              </span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Incoming Dependencies (USED BY) */}
      <CollapsibleSection
        title="Used By / Consumers"
        count={connections.incoming.length}
        icon={<ArrowLeft size={13} className="text-cyan-400" />}
        hideIfZero={true}
      >
        <div className="space-y-1.5 mt-1">
          {connections.incoming.map((conn: any, idx: number) => (
            <div
              key={idx}
              onClick={() => handleSelectConnectedNode(conn.node)}
              className="p-1.5 rounded bg-gray-900/70 border border-gray-800 hover:border-blue-700/60 flex items-center justify-between cursor-pointer group transition"
            >
              <div className="min-w-0">
                <div className="text-[10px] text-gray-500 uppercase tracking-wider">{conn.label}</div>
                <div className="font-semibold text-gray-200 group-hover:text-blue-300 truncate">
                  {conn.name}
                </div>
              </div>
              <span className="text-[10px] font-mono text-gray-500 group-hover:text-gray-400 uppercase">
                {conn.type}
              </span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Attributes & Properties */}
      <CollapsibleSection
        title="Attributes & Contracts"
        count={propEntries.length}
        icon={<Wrench size={13} className="text-amber-400" />}
        hideIfZero={true}
      >
        <div className="bg-gray-900/80 p-2 rounded-lg border border-gray-800 text-[11px] font-mono space-y-1 text-gray-300 mt-1">
          {propEntries.map(([k, v]) => (
            <div key={k} className="border-b border-gray-800/60 pb-1 last:border-none flex items-baseline justify-between gap-2">
              <span className="text-gray-500 shrink-0">{k}:</span>
              <span className="text-gray-200 truncate text-right font-medium">
                {Array.isArray(v) 
                  ? v.map((item: any) => typeof item === 'object' ? item.name || JSON.stringify(item) : String(item)).join(', ') || '[]'
                  : typeof v === 'object' ? JSON.stringify(v) : String(v)}
              </span>
            </div>
          ))}
        </div>
      </CollapsibleSection>

      {/* Source Definition File */}
      {selectedGraphNode.source_file && (
        <CollapsibleSection
          title="Defined In"
          icon={<FileCode2 size={13} className="text-blue-400" />}
          hideIfZero={false}
        >
          <button
            onClick={() => handleOpenDefinition(selectedGraphNode.source_file!)}
            className="w-full mt-1 p-2 rounded bg-blue-950/20 border border-blue-800/40 hover:border-blue-600 flex items-center justify-between text-left transition group"
          >
            <div className="flex items-center gap-2 min-w-0">
              <FileCode2 size={14} className="text-blue-400 shrink-0" />
              <span className="font-mono text-xs text-blue-300 group-hover:underline truncate">
                {selectedGraphNode.source_file}
              </span>
            </div>
            <ExternalLink size={12} className="text-blue-400 shrink-0" />
          </button>
        </CollapsibleSection>
      )}

      {/* Relevant Actions */}
      <div className="p-3 mt-auto border-t border-gray-800 space-y-1.5">
        <div className="text-[10px] uppercase font-bold text-gray-500 tracking-wider mb-1">
          Actions
        </div>
        {selectedGraphNode.source_file && (
          <button
            onClick={() => handleOpenDefinition(selectedGraphNode.source_file!)}
            className="w-full py-1.5 px-2.5 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-semibold flex items-center justify-center gap-1.5 transition shadow"
          >
            <FileCode2 size={13} />
            <span>Open Source Definition</span>
          </button>
        )}
        <button
          onClick={() => setSelectedGraphNode(null)}
          className="w-full py-1 px-2.5 bg-gray-800 hover:bg-gray-700 text-gray-300 rounded text-xs transition"
        >
          Return to Graph Overview
        </button>
      </div>
    </div>
  );
};
