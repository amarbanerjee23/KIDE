import React, { useState, useEffect } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import Editor from '@monaco-editor/react';
import { MncFlowViewer } from '../flow/MncFlowViewer';
import { exportJson, exportDsl, exportPython, exportActivity, exportCapability, exportOperation, exportDml } from '../../api/export';
import { ControlNodeTree } from './ControlNodeTree';
import { ValidationPanel } from './ValidationPanel';

export const TransformOutput: React.FC = () => {
  const { transformResult, isTransforming } = useEditorStore();
  const [activeTab, setActiveTab] = useState<string>('mncml');
  const [tabContent, setTabContent] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  const tabs = [
    { id: 'mncml', label: 'MNC-ML DSL' },
    { id: 'json', label: 'MNC JSON' },
    { id: 'activity', label: 'Activity DSL' },
    { id: 'capability', label: 'Capability DSL' },
    { id: 'operation', label: 'Operation DSL' },
    { id: 'dml', label: 'DML' },
    { id: 'python', label: 'Python Stubs' },
    { id: 'statemachine', label: 'State Machine' },
    { id: 'blockdiagram', label: 'Block Diagram' },
    { id: 'tree', label: 'Control Tree' },
    { id: 'validation', label: 'Validation' }
  ];

  useEffect(() => {
    const model = transformResult?.model;
    if (!model) return;

    const loadContent = async () => {
      setIsLoading(true);
      try {
        let content = '';
        switch (activeTab) {
          case 'mncml': content = await exportDsl(model); break;
          case 'json': content = await exportJson(model); break;
          case 'python': content = await exportPython(model); break;
          case 'activity': content = await exportActivity(model); break;
          case 'capability': content = await exportCapability(model); break;
          case 'operation': content = await exportOperation(model); break;
          case 'dml': content = await exportDml(model); break;
        }
        setTabContent(content);
      } catch (e) {
        setTabContent(`Error: ${e}`);
      }
      setIsLoading(false);
    };

    if (['mncml', 'json', 'python', 'activity', 'capability', 'operation', 'dml'].includes(activeTab)) {
      loadContent();
    }
  }, [activeTab, transformResult]);

  if (isTransforming) {
    return <div className="p-4 text-gray-400">Transforming...</div>;
  }

  if (!transformResult) {
    return <div className="p-4 text-gray-500">Run a transform to see output.</div>;
  }

  return (
    <div className="h-full flex flex-col bg-background text-gray-200">
      <div className="flex bg-surface border-b border-accent overflow-x-auto scrollbar-thin">
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-4 py-2 text-sm whitespace-nowrap border-b-2 ${
              activeTab === tab.id 
                ? 'border-blue-500 text-blue-400 bg-background' 
                : 'border-transparent text-gray-400 hover:text-gray-200 hover:bg-gray-800'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>
      
      <div className="flex-1 overflow-hidden">
        {['mncml', 'json', 'python', 'activity', 'capability', 'operation', 'dml'].includes(activeTab) && (
          isLoading ? (
            <div className="p-4">Loading...</div>
          ) : (
            <Editor
              height="100%"
              language={activeTab === 'json' ? 'json' : activeTab === 'python' ? 'python' : 'text'}
              theme="vs-dark"
              value={tabContent}
              options={{ readOnly: true, minimap: { enabled: false } }}
            />
          )
        )}
        
        {(activeTab === 'statemachine' || activeTab === 'blockdiagram') && transformResult.model && (
          <MncFlowViewer model={transformResult.model} />
        )}

        {activeTab === 'tree' && transformResult.model && (
          <div className="p-4 h-full overflow-auto">
            <ControlNodeTree model={transformResult.model} />
          </div>
        )}

        {activeTab === 'validation' && (
          <div className="p-4 h-full overflow-auto">
            <ValidationPanel />
          </div>
        )}
      </div>
    </div>
  );
};
