import React from 'react';
import { useNavigate } from 'react-router-dom';
import { KnowledgeGraphViewer } from '../components/knowledge/KnowledgeGraphViewer';
import { Network, ArrowLeft, ExternalLink } from 'lucide-react';
import Button from '../components/common/Button';

export const KnowledgeGraphPage: React.FC = () => {
  const navigate = useNavigate();

  return (
    <div className="w-full h-screen flex flex-col bg-[#0b0f19] text-gray-100 overflow-hidden">
      {/* Studio Header Bar */}
      <header className="h-14 bg-[#0d1117] border-b border-gray-800 px-4 flex items-center justify-between z-20 shrink-0">
        <div className="flex items-center gap-3">
          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/dashboard')}
            className="text-gray-400 hover:text-white hover:bg-gray-800/60 p-2"
            title="Back to Dashboard"
          >
            <ArrowLeft className="w-4 h-4 mr-1.5" />
            <span className="hidden sm:inline">Dashboard</span>
          </Button>

          <div className="h-4 w-px bg-gray-800" />

          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-indigo-600 to-purple-600 flex items-center justify-center shadow-sm">
              <Network className="w-4.5 h-4.5 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h1 className="text-sm font-bold text-gray-100 tracking-wide">
                  Knowledge Graph Studio
                </h1>
                <span className="text-[10px] uppercase font-bold tracking-wider px-2 py-0.5 rounded bg-indigo-950/80 border border-indigo-700/60 text-indigo-300">
                  Thesis Ontology Metamodel
                </span>
              </div>
              <p className="text-[11px] text-gray-400 hidden md:block">
                Property Graph &amp; W3C OWL/RDF Semantic Repository (19 Domains &bull; 69+ Physical Devices)
              </p>
            </div>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            onClick={() => window.open('/knowledge-graph', '_blank')}
            className="border-indigo-500/40 text-indigo-300 hover:bg-indigo-950/50 text-xs"
            title="Pop out Knowledge Graph into a standalone browser window"
          >
            <ExternalLink className="w-3.5 h-3.5 mr-1.5 text-indigo-400" />
            <span>Open in New Tab ↗</span>
          </Button>
        </div>
      </header>

      {/* Main Graph Viewer Canvas */}
      <main className="flex-1 w-full relative overflow-hidden">
        <KnowledgeGraphViewer />
      </main>
    </div>
  );
};

export default KnowledgeGraphPage;

