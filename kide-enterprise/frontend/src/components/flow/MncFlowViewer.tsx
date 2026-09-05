import React from 'react';
import { ReactFlow, Controls, Background } from '@xyflow/react';

// Simplified viewer for output model
const MncFlowViewer = () => {
  return (
    <div className="w-full h-full bg-black">
      <ReactFlow
        nodes={[]}
        edges={[]}
        fitView
      >
        <Background color="#555" gap={16} />
        <Controls />
      </ReactFlow>
    </div>
  );
};

export default MncFlowViewer;

