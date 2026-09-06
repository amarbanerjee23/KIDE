import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';
import ValidationPanel from './ValidationPanel';
import ControlNodeTree from './ControlNodeTree';
import { exportApi } from '../../api/export';

const DslEditor = ({ model }: { model: any }) => {
  const [dsl, setDsl] = useState<string>('Loading...');
  
  useEffect(() => {
    exportApi.exportDsl(model)
      .then(setDsl)
      .catch(err => setDsl('Error loading DSL: ' + err.message));
  }, [model]);

  return (
    <Editor
      height="100%"
      defaultLanguage="plaintext"
      theme="vs-dark"
      value={dsl}
      options={{ readOnly: true, minimap: { enabled: false }, padding: { top: 16 } }}
    />
  );
};

const TransformOutput = () => {
  const [activeTab, setActiveTab] = useState<'json' | 'dsl' | 'validation' | 'tree'>('json');
  const transformResult = useEditorStore(state => state.transformResult);

  const tabs = [
    { id: 'json', label: 'MNC JSON' },
    { id: 'dsl', label: 'MNC-ML DSL' },
    { id: 'validation', label: 'Validation' },
    { id: 'tree', label: 'Control Tree' }
  ];

  return (
    <div className="h-full flex flex-col bg-background">
      <div className="flex border-b border-accent bg-surface px-2">
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id as any)}
            className={`px-4 py-3 text-sm font-medium border-b-2 transition-colors ${
              activeTab === tab.id 
                ? 'border-highlight text-highlight' 
                : 'border-transparent text-gray-400 hover:text-white'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>
      
      <div className="flex-1 overflow-hidden relative">
        {activeTab === 'json' && (
          transformResult ? (
            <Editor
              height="100%"
              defaultLanguage="json"
              theme="vs-dark"
              value={JSON.stringify(transformResult.model, null, 2)}
              options={{ readOnly: true, minimap: { enabled: false }, padding: { top: 16 } }}
            />
          ) : <Placeholder />
        )}

        {activeTab === 'dsl' && (
          transformResult ? (
            <DslEditor model={transformResult.model} />
          ) : <Placeholder />
        )}

        {activeTab === 'validation' && <ValidationPanel />}
        {activeTab === 'tree' && <ControlNodeTree />}
      </div>
    </div>
  );
};

const Placeholder = () => (
  <div className="flex h-full items-center justify-center text-gray-500">
    Run Transform to see output
  </div>
);

export default TransformOutput;

