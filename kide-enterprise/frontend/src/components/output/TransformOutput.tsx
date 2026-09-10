import React, { useState, useEffect } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import Editor from '@monaco-editor/react';
import { MncFlowViewer } from '../flow/MncFlowViewer';
import { 
  exportJson, exportDsl, exportPython, exportRos2, exportJava, exportPlc, exportCpp,
  exportActivity, exportCapability, exportOperation, exportDml, downloadZipBundle 
} from '../../api/export';
import { ControlNodeTree } from './ControlNodeTree';
import { ValidationPanel } from './ValidationPanel';
import { LiveRunnerConsole } from '../simulation/LiveRunnerConsole';
import { Download, Copy, Check, Loader2 } from 'lucide-react';

export const TransformOutput: React.FC = () => {
  const { transformResult, isTransforming, projectId } = useEditorStore();
  const [activeTab, setActiveTab] = useState<string>('mncml');
  const [tabContent, setTabContent] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [copied, setCopied] = useState(false);

  const tabs = [
    { id: 'mncml', label: 'MNC-ML DSL' },
    { id: 'python', label: 'Python Controller' },
    { id: 'ros2', label: 'ROS2 Node' },
    { id: 'java', label: 'Java Controller' },
    { id: 'plc', label: 'PLC (IEC 61131-3)' },
    { id: 'cpp', label: 'Embedded C++' },
    { id: 'simulation', label: '▶ Live Simulator' },
    { id: 'json', label: 'MNC JSON' },
    { id: 'statemachine', label: 'State Machine' },
    { id: 'blockdiagram', label: 'Block Diagram' },
    { id: 'tree', label: 'Control Tree' },
    { id: 'activity', label: 'Activity DSL' },
    { id: 'capability', label: 'Capability DSL' },
    { id: 'operation', label: 'Operation DSL' },
    { id: 'dml', label: 'DML' },
    { id: 'validation', label: 'Validation' }
  ];

  useEffect(() => {
    const model = transformResult?.model;
    if (!model) return;
    if (activeTab === 'statemachine' || activeTab === 'blockdiagram' || activeTab === 'tree' || activeTab === 'validation' || activeTab === 'simulation') {
      return;
    }

    const fetchContent = async () => {
      setIsLoading(true);
      try {
        let content = '';
        switch (activeTab) {
          case 'mncml':
            content = await exportDsl(model);
            break;
          case 'python':
            content = await exportPython(model);
            break;
          case 'ros2':
            content = await exportRos2(model);
            break;
          case 'java':
            content = await exportJava(model);
            break;
          case 'plc':
            content = await exportPlc(model);
            break;
          case 'cpp':
            content = await exportCpp(model);
            break;
          case 'json':
            content = await exportJson(model);
            break;
          case 'activity':
            content = await exportActivity(model);
            break;
          case 'capability':
            content = await exportCapability(model);
            break;
          case 'operation':
            content = await exportOperation(model);
            break;
          case 'dml':
            content = await exportDml(model);
            break;
          default:
            content = '';
        }
        setTabContent(content);
      } catch (err) {
        console.error('Failed to export:', err);
        setTabContent(`// Error exporting: ${err}`);
      } finally {
        setIsLoading(false);
      }
    };

    fetchContent();
  }, [activeTab, transformResult]);

  const handleCopy = () => {
    navigator.clipboard.writeText(tabContent);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownload = () => {
    const ext = activeTab === 'json' ? 'json' : (activeTab === 'python' ? 'py' : (activeTab === 'java' ? 'java' : (activeTab === 'plc' ? 'st' : (activeTab === 'cpp' ? 'hpp' : 'txt'))));
    const blob = new Blob([tabContent], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${transformResult?.model?.name || 'transformed'}.${ext}`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const handleDownloadZip = async () => {
    if (!transformResult?.model) return;
    try {
      await downloadZipBundle(transformResult.model, `${transformResult.model.name || 'controller'}_package.zip`);
    } catch (err) {
      console.error('Download bundle error:', err);
    }
  };

  if (isTransforming) {
    return (
      <div className="h-full flex items-center justify-center bg-[#1e1e1e] text-gray-400">
        <div className="flex flex-col items-center gap-2">
          <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          <span>Synthesizing Controller & Code Models...</span>
        </div>
      </div>
    );
  }

  if (!transformResult || !transformResult.model) {
    return (
      <div className="h-full flex items-center justify-center bg-[#1e1e1e] text-gray-500">
        Run synthesis or click "Try Transform Example" to view generated models & executable code.
      </div>
    );
  }

  const model = transformResult.model;

  const getLanguage = () => {
    switch (activeTab) {
      case 'json': return 'json';
      case 'python': return 'python';
      case 'ros2': return 'python';
      case 'java': return 'java';
      case 'cpp': return 'cpp';
      default: return 'plaintext';
    }
  };

  return (
    <div className="h-full flex flex-col bg-[#1e1e1e] border-t border-[#333]">
      {/* Top Tab Bar */}
      <div className="flex items-center justify-between bg-[#252526] border-b border-[#333] px-2 overflow-x-auto">
        <div className="flex space-x-1">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`px-3 py-1.5 text-xs whitespace-nowrap transition-colors rounded-t ${
                activeTab === tab.id
                  ? 'bg-[#1e1e1e] text-white font-medium border-t-2 border-blue-500'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-[#2a2a2b]'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Actions */}
        <div className="flex items-center space-x-2 py-1">
          <button
            onClick={handleDownloadZip}
            className="flex items-center space-x-1 px-2 py-1 bg-emerald-700/80 hover:bg-emerald-600 text-white rounded text-xs transition"
            title="Download full controller package with all targets"
          >
            <Download className="w-3.5 h-3.5" />
            <span>ZIP Bundle</span>
          </button>
          {activeTab !== 'statemachine' && activeTab !== 'blockdiagram' && activeTab !== 'tree' && activeTab !== 'validation' && activeTab !== 'simulation' && (
            <>
              <button
                onClick={handleCopy}
                className="flex items-center space-x-1 px-2 py-1 bg-[#333] hover:bg-[#444] text-gray-300 rounded text-xs transition"
              >
                {copied ? <Check className="w-3.5 h-3.5 text-green-400" /> : <Copy className="w-3.5 h-3.5" />}
                <span>{copied ? 'Copied' : 'Copy'}</span>
              </button>
              <button
                onClick={handleDownload}
                className="flex items-center space-x-1 px-2 py-1 bg-[#333] hover:bg-[#444] text-gray-300 rounded text-xs transition"
              >
                <Download className="w-3.5 h-3.5" />
                <span>Save File</span>
              </button>
            </>
          )}
        </div>
      </div>

      {/* Content Body */}
      <div className="flex-1 overflow-hidden relative">
        {isLoading && (
          <div className="absolute inset-0 z-20 flex items-center justify-center bg-[#1e1e1e]/70 backdrop-blur-xs">
            <Loader2 className="w-6 h-6 animate-spin text-blue-400" />
            <span className="ml-2 text-xs text-gray-300">Generating code...</span>
          </div>
        )}
        {activeTab === 'statemachine' ? (
          <MncFlowViewer model={model} mode="statemachine" />
        ) : activeTab === 'blockdiagram' ? (
          <MncFlowViewer model={model} mode="blockdiagram" />
        ) : activeTab === 'tree' ? (
          <ControlNodeTree model={model} />
        ) : activeTab === 'validation' ? (
          <ValidationPanel />
        ) : activeTab === 'simulation' ? (
          <div className="h-full p-2">
            {projectId ? (
              <LiveRunnerConsole projectId={projectId} />
            ) : (
              <div className="flex items-center justify-center h-full text-slate-500 text-xs">
                Open a project to launch live controller runner.
              </div>
            )}
          </div>
        ) : (
          <Editor
            height="100%"
            language={getLanguage()}
            value={tabContent}
            theme="vs-dark"
            options={{
              readOnly: true,
              minimap: { enabled: false },
              fontSize: 12,
              scrollBeyondLastLine: false,
              wordWrap: 'on'
            }}
          />
        )}
      </div>
    </div>
  );
};
