import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';
import Modal from '../common/Modal';
import Input from '../common/Input';
import Button from '../common/Button';
import { Database, Cpu, Settings, Activity } from 'lucide-react';

interface NewArtifactModalProps {
  isOpen: boolean;
  onClose: () => void;
  defaultExt?: string;
}

export const NewArtifactModal: React.FC<NewArtifactModalProps> = ({
  isOpen,
  onClose,
  defaultExt = '.dml'
}) => {
  const { projectId, addFile, setActiveFileId, setActiveView } = useEditorStore();
  const [baseName, setBaseName] = useState('');
  const [selectedExt, setSelectedExt] = useState(defaultExt);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const artifactTypes = [
    { ext: '.dml', name: 'Data Model (DML)', icon: <Database size={15} className="text-rose-400" />, desc: 'Data structs & primitives' },
    { ext: '.cap', name: 'Capability Spec', icon: <Cpu size={15} className="text-purple-400" />, desc: 'Init & control capabilities' },
    { ext: '.op', name: 'Operation', icon: <Settings size={15} className="text-amber-400" />, desc: 'Executable driver script' },
    { ext: '.activity', name: 'Supervisory Workflow', icon: <Activity size={15} className="text-emerald-400" />, desc: 'Activity process diagram' },
    { ext: '.mnc', name: 'State Machine (MNC)', icon: <Settings size={15} className="text-blue-400" />, desc: 'Formal control node & states' },
  ];

  const getTemplateContent = (ext: string, name: string) => {
    const clean = name.replace(/[^a-zA-Z0-9_]/g, '') || 'MyArtifact';
    switch (ext) {
      case '.dml':
        return `Package ${clean}Package\nDataModel ${clean}Data {\n    primitives {\n        int itemId = 1,\n        float value = 25.0,\n        boolean isActive = true,\n        string status = "READY"\n    }\n}`;
      case '.cap':
        return `Capability ${clean}Cap compatible component interface ${clean}Interface {\n    Init {\n        fire Commands [ START() ]\n    }\n    providesControlCapabilities {\n        fireable commands : START, STOP\n        receivable events : STARTED, STOPPED\n        raised alarms : FAULT\n        subscribable DataPoints : status\n    }\n    providesOutcomes {\n        receivable responses : ACK\n    }\n}`;
      case '.op':
        return `Operation ${clean}Op(int id, float speed) {\n    execute "scripts/${clean.toLowerCase()}.py"\n    return boolean success\n}`;
      case '.activity':
        return `ActivityDiagram ${clean}Diagram\nuses Objects [ string status ]\nproduces results ( string finalResult )\nhas activities {\n    Activity InitStep {\n        description : "Initialize process"\n        nextActivity : RunStep\n        time : 2.0 secs\n    }\n    Activity RunStep {\n        description : "Execute active step"\n    }\n}`;
      case '.mnc':
        return `Model ${clean}Model\nInterfaceDescription ${clean}IF {\n    IPaddress : 192.168.1.10\n    port mainPort = 8080\n    commands {\n        async START []\n        STOP []\n    }\n    events {\n        Publish STARTED []\n    }\n    alarms {\n        Publish FAULT [] level = 1\n    }\n    dataPoints {\n        Publish int status = 0 []\n    }\n    operatingStates {\n        IDLE []\n        RUNNING []\n        FAULT []\n        startStates : IDLE\n        endStates : FAULT\n    }\n}\nControlNode ${clean}CN implements interface ${clean}IF {\n    CommandResponseBlock {\n        Command START {\n            Action {\n                transition states [ currentState IDLE => nextState RUNNING ]\n            }\n        }\n    }\n}`;
      default:
        return '';
    }
  };

  const getLanguage = (filename: string) => {
    if (filename.endsWith('.json')) return 'json';
    if (filename.endsWith('.mnc')) return 'mncml';
    if (filename.endsWith('.activity')) return 'activitydsl';
    if (filename.endsWith('.cap') || filename.endsWith('.capability')) return 'capabilitydsl';
    if (filename.endsWith('.op') || filename.endsWith('.operation')) return 'operationdsl';
    if (filename.endsWith('.dml')) return 'dmldsl';
    return 'text';
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!baseName.trim() || !projectId) return;

    let clean = baseName.trim();
    if (clean.includes('.')) {
      clean = clean.substring(0, clean.lastIndexOf('.'));
    }
    const finalFilename = `${clean}${selectedExt}`;
    const initialContent = getTemplateContent(selectedExt, clean);
    const fileType = selectedExt.replace('.', '');

    setIsSubmitting(true);
    try {
      const saved = await projectsApi.addFile(projectId, {
        filename: finalFilename,
        file_type: fileType,
        content: initialContent
      });

      const newId = String(saved.id);
      addFile({
        id: newId,
        name: finalFilename,
        content: initialContent,
        language: getLanguage(finalFilename)
      });

      setActiveFileId(newId);
      if (selectedExt === '.activity') {
        setActiveView('workflow');
      } else if (selectedExt === '.mnc') {
        setActiveView('statemachine');
      } else {
        setActiveView('editor');
      }

      onClose();
      setBaseName('');
    } catch (err) {
      console.error('Failed to create artifact:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Create Project Artifact">
      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Artifact Base Name"
          placeholder="e.g. BarrierControl, CoolingSensors, AssemblyLine"
          value={baseName}
          onChange={(e) => setBaseName(e.target.value)}
          required
          autoFocus
        />

        <div>
          <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-2">
            Engineering Artifact Type
          </label>
          <div className="grid grid-cols-1 gap-1.5">
            {artifactTypes.map((item) => (
              <button
                key={item.ext}
                type="button"
                onClick={() => setSelectedExt(item.ext)}
                className={`flex items-center justify-between p-2.5 rounded-lg border text-left transition-all ${
                  selectedExt === item.ext
                    ? 'bg-blue-600/20 border-blue-500/80 text-blue-200 shadow-sm'
                    : 'bg-[#161b22] border-gray-800 text-gray-400 hover:border-gray-700 hover:text-gray-200'
                }`}
              >
                <div className="flex items-center gap-2.5">
                  <div className="p-1 rounded bg-gray-900 border border-gray-800">{item.icon}</div>
                  <div>
                    <div className="text-xs font-semibold text-gray-200">{item.name}</div>
                    <div className="text-[11px] text-gray-400">{item.desc}</div>
                  </div>
                </div>
                <span className="font-mono text-xs text-gray-400 bg-gray-900/80 px-2 py-0.5 rounded border border-gray-800">
                  {item.ext}
                </span>
              </button>
            ))}
          </div>
        </div>

        <div className="flex justify-end gap-2 pt-2 border-t border-gray-800">
          <Button variant="ghost" type="button" onClick={onClose} disabled={isSubmitting}>
            Cancel
          </Button>
          <Button variant="primary" type="submit" disabled={isSubmitting || !baseName.trim()}>
            {isSubmitting ? 'Creating...' : 'Create Artifact'}
          </Button>
        </div>
      </form>
    </Modal>
  );
};
