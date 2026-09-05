import React, { useEffect, useState, useRef } from 'react';
import { useParams } from 'react-router-dom';
import MonacoDslEditor from '../components/editor/MonacoDslEditor';
import EditorToolbar from '../components/editor/EditorToolbar';
import ActivityFlowEditor from '../components/flow/ActivityFlowEditor';
import TransformOutput from '../components/output/TransformOutput';
import { useEditorStore } from '../stores/editorStore';
import { projectsApi } from '../api/projects';

const ProjectWorkspacePage = () => {
  const { id } = useParams();
  const { activityJson, setActivityJson } = useEditorStore();
  const [leftWidth, setLeftWidth] = useState(40); // percentage
  const [topHeight, setTopHeight] = useState(60); // percentage
  const containerRef = useRef<HTMLDivElement>(null);
  const rightContainerRef = useRef<HTMLDivElement>(null);

  // Initialize
  useEffect(() => {
    // In real app, fetch project and files.
    // We'll just load a default example if empty.
    if (activityJson === '{\n  "name": "New Activity",\n  "defaultOperatingStates": [],\n  "activities": []\n}') {
      const example = {
        name: "CoolingSystem",
        defaultOperatingStates: ["NORMAL"],
        activities: [
          {
            name: "Initialize",
            requiresOperation: true,
            commands: ["START_PUMP"],
            events: [],
            transitions: [{ from: "Initialize", to: "Monitor" }]
          },
          {
            name: "Monitor",
            requiresOperation: false,
            events: ["TEMP_HIGH"],
            transitions: [{ from: "Monitor", to: "CoolDown", condition: "temp > 100" }]
          },
          {
            name: "CoolDown",
            requiresOperation: true,
            commands: ["MAX_FAN"],
            transitions: [{ from: "CoolDown", to: "Monitor" }]
          }
        ]
      };
      setActivityJson(JSON.stringify(example, null, 2));
    }
  }, [id, setActivityJson, activityJson]);

  // Handle Dragging Horizontal Split
  const handleHDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    document.addEventListener('mousemove', handleHDrag);
    document.addEventListener('mouseup', handleHDragEnd);
  };
  const handleHDrag = (e: MouseEvent) => {
    if (containerRef.current) {
      const rect = containerRef.current.getBoundingClientRect();
      const newWidth = ((e.clientX - rect.left) / rect.width) * 100;
      if (newWidth > 20 && newWidth < 80) setLeftWidth(newWidth);
    }
  };
  const handleHDragEnd = () => {
    document.removeEventListener('mousemove', handleHDrag);
    document.removeEventListener('mouseup', handleHDragEnd);
  };

  // Handle Dragging Vertical Split
  const handleVDragStart = (e: React.MouseEvent) => {
    e.preventDefault();
    document.addEventListener('mousemove', handleVDrag);
    document.addEventListener('mouseup', handleVDragEnd);
  };
  const handleVDrag = (e: MouseEvent) => {
    if (rightContainerRef.current) {
      const rect = rightContainerRef.current.getBoundingClientRect();
      const newHeight = ((e.clientY - rect.top) / rect.height) * 100;
      if (newHeight > 20 && newHeight < 80) setTopHeight(newHeight);
    }
  };
  const handleVDragEnd = () => {
    document.removeEventListener('mousemove', handleVDrag);
    document.removeEventListener('mouseup', handleVDragEnd);
  };

  return (
    <div className="flex flex-col h-full bg-background overflow-hidden">
      <EditorToolbar />
      
      <div ref={containerRef} className="flex flex-1 overflow-hidden relative">
        {/* Left Panel - JSON Editor */}
        <div style={{ width: `${leftWidth}%` }} className="h-full flex flex-col min-w-[200px]">
          <MonacoDslEditor value={activityJson} onChange={setActivityJson} />
        </div>

        {/* Resizer H */}
        <div 
          className="w-1 bg-accent hover:bg-highlight cursor-col-resize z-10 transition-colors"
          onMouseDown={handleHDragStart}
        />

        {/* Right Panel - Flow + Output */}
        <div ref={rightContainerRef} style={{ width: `${100 - leftWidth}%` }} className="h-full flex flex-col min-w-[200px]">
          
          {/* Top Right - Flow Editor */}
          <div style={{ height: `${topHeight}%` }} className="w-full relative min-h-[100px]">
            <ActivityFlowEditor />
          </div>

          {/* Resizer V */}
          <div 
            className="h-1 bg-accent hover:bg-highlight cursor-row-resize z-10 transition-colors"
            onMouseDown={handleVDragStart}
          />

          {/* Bottom Right - Output */}
          <div style={{ height: `${100 - topHeight}%` }} className="w-full bg-surface min-h-[100px]">
            <TransformOutput />
          </div>

        </div>
      </div>
    </div>
  );
};

export default ProjectWorkspacePage;

