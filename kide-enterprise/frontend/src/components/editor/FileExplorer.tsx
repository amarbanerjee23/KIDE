import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { projectsApi } from '../../api/projects';
import { 
  File, FileJson, Plus, Trash2, Activity, Settings, Cpu, Database, 
  Sparkles, AlertCircle, FilePlus
} from 'lucide-react';
import Modal from '../common/Modal';
import Button from '../common/Button';
import Input from '../common/Input';

const uuidv4 = () => Math.random().toString(36).substring(2, 9) + Date.now().toString(36);

export const FileExplorer: React.FC = () => {
  const { 
    files, activeFileId, dirtyFileIds, projectId,
    setActiveFileId, addFile, removeFile, setFiles 
  } = useEditorStore();

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSeedModalOpen, setIsSeedModalOpen] = useState(false);
  const [fileNameInput, setFileNameInput] = useState('');
  const [selectedExt, setSelectedExt] = useState('.activity');
  const [selectedTemplate, setSelectedTemplate] = useState('standard');
  const [isCreating, setIsCreating] = useState(false);
  const [isSeeding, setIsSeeding] = useState(false);
  const [deleteConfirmId, setDeleteConfirmId] = useState<string | null>(null);

  const getLanguage = (filename: string) => {
    if (filename.endsWith('.json')) return 'json';
    if (filename.endsWith('.mnc')) return 'mncml';
    if (filename.endsWith('.activity')) return 'activitydsl';
    if (filename.endsWith('.cap') || filename.endsWith('.capability')) return 'capabilitydsl';
    if (filename.endsWith('.op') || filename.endsWith('.operation')) return 'operationdsl';
    if (filename.endsWith('.dml')) return 'dmldsl';
    return 'text';
  };

  const getFileType = (ext: string) => {
    switch (ext) {
      case '.activity': return 'activity';
      case '.mnc': return 'mnc';
      case '.cap': return 'capability';
      case '.op': return 'operation';
      case '.dml': return 'dml';
      case '.json': return 'json';
      default: return 'text';
    }
  };

  const getTemplateContent = (ext: string, templateKey: string, baseName: string) => {
    const cleanName = baseName.replace(/[^a-zA-Z0-9_]/g, '') || 'MyComponent';

    if (templateKey === 'cooling') {
      if (ext === '.activity') {
        return `ActivityDiagram CoolingSystem\nuses Objects [ string status, float temperature ]\nproduces results ( string finalStatus )\nhas activities {\n    Activity Initialize {\n        description : "Initialize pumps and cooling valves"\n        requireOperation ( StartPumpOperation )\n        nextActivity : Monitor\n        time : 2.0 secs\n    }\n    Activity Monitor {\n        description : "Monitor industrial temperature"\n        requireCapability : "SensorCap" { ReadTemperature }\n        conditions {\n            if outcome temperature is ( > 100.0 ) => nextActivity : CoolDown,\n            if outcome temperature is ( < 80.0 ) => nextActivity : NormalOperation\n        }\n        time : 5.0 secs\n    }\n    Activity CoolDown {\n        description : "Engage max cooling fan"\n        requireOperation ( MaxFanOperation )\n        nextActivity : Monitor\n        time : 10.0 secs\n    }\n    Activity NormalOperation {\n        description : "Maintain nominal cooling rate"\n        nextActivity : Monitor\n    }\n}`;
      }
      if (ext === '.dml') {
        return `Package CoolingSystem\nDataModel TemperatureData {\n    primitives {\n        float currentTemp = 25.0,\n        float maxThreshold = 100.0,\n        boolean isAlert = false,\n        string unit = "Celsius"\n    }\n}`;
      }
      if (ext === '.op') {
        return `Operation StartPumpOperation(int pumpId) {\n    execute "scripts/start_pump.py"\n    return boolean success\n}\n\nOperation MaxFanOperation(float fanSpeed) {\n    execute "scripts/max_fan.py"\n    return boolean success\n}`;
      }
      if (ext === '.cap') {
        return `Capability SensorCap compatible component interface CoolingSystem_Interface {\n    Init {\n        subscribe alarms [ OVERHEATING() ]\n        fire Commands [ START_PUMP() ]\n    }\n    providesControlCapabilities {\n        fireable commands : START_PUMP, OPEN_VALVE, MAX_FAN\n        receivable events : PUMP_STARTED, TEMP_HIGH\n        raised alarms : OVERHEATING\n        subscribable DataPoints : temperature\n    }\n    providesOutcomes {\n        receivable responses : PUMP_ACK\n    }\n}`;
      }
    }

    if (templateKey === 'assembly') {
      if (ext === '.dml') {
        return `DataModel ECRE {\n\tprimitives {\n\t\tboolean isValidTruck = false,\n\t\tint RFID_TAG_VALUE,\n\t\tdate ecre_date    \n\t} \n}`;
      }
      if (ext === '.mnc') {
        return `import com.kide.common.*\nModel AssemblyCell\nInterfaceDescription AssemblyIF {\n    IPaddress : 192.168.1.100\n    port mainPort = 8080\n    commands {\n        async StartAssembly [int partId]\n        StopAssembly []\n    }\n    events {\n        Publish AssemblyCompleted [string partId]\n    }\n    alarms {\n        Publish EmergencyStop [] level = 1\n    }\n    dataPoints {\n        Publish int PartCount = 0 []\n    }\n    operatingStates {\n        IDLE []\n        RUNNING []\n        FAULT []\n        startStates : IDLE\n        endStates : FAULT\n    }\n}\nControlNode AssemblyCN implements interface AssemblyIF {\n    CommandResponseBlock {\n        Command StartAssembly {\n            Action {\n                fire commands [ StartAssembly() ]\n                transition states [ currentState IDLE => nextState RUNNING ]\n            }\n        }\n    }\n}`;
      }
    }

    // Default valid grammar templates
    switch (ext) {
      case '.activity':
        return `ActivityDiagram ${cleanName}\nuses Objects [ string status ]\nproduces results ( string finalResult )\nhas activities {\n    Activity Step1 {\n        description : "Initial workflow step"\n        nextActivity : Step2\n        time : 1.0 secs\n    }\n    Activity Step2 {\n        description : "Final processing step"\n    }\n}`;
      case '.mnc':
        return `Model ${cleanName}\nInterfaceDescription ${cleanName}_Interface {\n    IPaddress : 192.168.1.10\n    port mainPort = 8080\n    commands {\n        async StartCommand [int id]\n        StopCommand []\n    }\n    events {\n        Publish ProcessFinished [string status]\n    }\n    operatingStates {\n        IDLE []\n        ACTIVE []\n        startStates : IDLE\n        endStates : ACTIVE\n    }\n}\nControlNode ${cleanName}_Control implements interface ${cleanName}_Interface {\n    CommandResponseBlock {\n        Command StartCommand {\n            Action {\n                transition states [ currentState IDLE => nextState ACTIVE ]\n            }\n        }\n    }\n}`;
      case '.cap':
        return `Capability ${cleanName}_Cap compatible component interface ${cleanName}_Interface {\n    Init {\n        fire Commands [ StartCommand() ]\n    }\n    providesControlCapabilities {\n        fireable commands : StartCommand, StopCommand\n        receivable events : ProcessFinished\n    }\n    providesOutcomes {\n        receivable responses : StatusACK\n    }\n}`;
      case '.op':
        return `Operation ${cleanName}_Operation(int inputId, string config) {\n    execute "scripts/${cleanName.toLowerCase()}.py"\n    return boolean status\n}`;
      case '.dml':
        return `Package ${cleanName}_Package\nDataModel ${cleanName}_Data {\n    primitives {\n        int itemId = 1,\n        string status = "READY",\n        boolean isActive = true\n    }\n}`;
      case '.json':
        return JSON.stringify({
          name: cleanName,
          default_operating_states: ["IDLE", "RUNNING", "FAULT"],
          activities: [
            {
              name: "Initialize",
              requires_operation: true,
              commands: [{ name: "START" }],
              events: [{ name: "STARTED" }],
              transitions: [{ from: "Initialize", to: "Process" }]
            },
            {
              name: "Process",
              requires_operation: false,
              commands: [{ name: "EXECUTE" }],
              transitions: [{ from: "Process", to: "Initialize" }]
            }
          ]
        }, null, 2);
      default:
        return '';
    }
  };

  const getIcon = (filename: string) => {
    if (filename.endsWith('.json')) return <FileJson size={16} className="text-yellow-400 shrink-0" />;
    if (filename.endsWith('.mnc')) return <Settings size={16} className="text-blue-400 shrink-0" />;
    if (filename.endsWith('.activity')) return <Activity size={16} className="text-emerald-400 shrink-0" />;
    if (filename.endsWith('.cap') || filename.endsWith('.capability')) return <Cpu size={16} className="text-purple-400 shrink-0" />;
    if (filename.endsWith('.op') || filename.endsWith('.operation')) return <Settings size={16} className="text-amber-400 shrink-0" />;
    if (filename.endsWith('.dml')) return <Database size={16} className="text-rose-400 shrink-0" />;
    return <File size={16} className="text-gray-400 shrink-0" />;
  };

  const handleCreateFile = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!fileNameInput.trim()) return;

    let baseName = fileNameInput.trim();
    if (baseName.includes('.')) {
      baseName = baseName.substring(0, baseName.lastIndexOf('.'));
    }
    const finalName = `${baseName}${selectedExt}`;
    const initialContent = getTemplateContent(selectedExt, selectedTemplate, baseName);
    const fileType = getFileType(selectedExt);

    setIsCreating(true);
    try {
      let createdId = uuidv4();
      if (projectId) {
        const saved = await projectsApi.addFile(projectId, {
          filename: finalName,
          file_type: fileType,
          content: initialContent
        });
        createdId = String(saved.id);
      }

      addFile({
        id: createdId,
        name: finalName,
        content: initialContent,
        language: getLanguage(finalName)
      });

      setIsModalOpen(false);
      setFileNameInput('');
    } catch (err) {
      console.error('Failed to create file:', err);
    } finally {
      setIsCreating(false);
    }
  };

  const handleDeleteFile = async (fileId: string) => {
    try {
      if (projectId) {
        await projectsApi.deleteFile(projectId, fileId);
      }
      removeFile(fileId);
      setDeleteConfirmId(null);
    } catch (err) {
      console.error('Failed to delete file:', err);
    }
  };

  const handleSeedExamples = async (templateKey: string) => {
    if (!projectId) return;
    setIsSeeding(true);
    try {
      const seeded = await projectsApi.seedExamples(projectId, templateKey);
      const mappedFiles = seeded.map(f => ({
        id: String(f.id),
        name: f.filename,
        content: f.content,
        language: getLanguage(f.filename)
      }));
      setFiles(mappedFiles);
      setIsSeedModalOpen(false);
    } catch (err) {
      console.error('Failed to seed examples:', err);
    } finally {
      setIsSeeding(false);
    }
  };

  return (
    <div className="w-full h-full bg-[#0d1117] border-r border-gray-800 flex flex-col select-none">
      {/* Explorer Top Toolbar */}
      <div className="p-3 border-b border-gray-800 flex justify-between items-center bg-[#161b22]">
        <div className="flex items-center gap-2">
          <span className="text-xs font-bold text-gray-300 uppercase tracking-wider">Project Files</span>
          <span className="text-xs bg-gray-800 text-gray-400 px-1.5 py-0.5 rounded-full font-mono">
            {files.length}
          </span>
        </div>
        <div className="flex items-center gap-1">
          <button
            onClick={() => setIsSeedModalOpen(true)}
            className="p-1 text-gray-400 hover:text-amber-400 hover:bg-gray-800 rounded transition-colors"
            title="Load Reference Templates"
          >
            <Sparkles size={15} />
          </button>
          <button
            onClick={() => {
              setFileNameInput('');
              setIsModalOpen(true);
            }}
            className="flex items-center gap-1 px-2 py-1 bg-blue-600 hover:bg-blue-500 text-white rounded text-xs font-semibold shadow transition-all"
            title="Create New DSL File"
          >
            <Plus size={14} />
            <span>New</span>
          </button>
        </div>
      </div>

      {/* File List */}
      <div className="flex-1 overflow-y-auto py-2">
        {files.length === 0 ? (
          <div className="p-4 text-center">
            <p className="text-xs text-gray-400 mb-3">No files in project yet.</p>
            <div className="flex flex-col gap-2">
              <Button 
                variant="secondary"
                className="w-full text-xs py-1.5 justify-center"
                onClick={() => setIsModalOpen(true)}
              >
                <FilePlus size={14} className="mr-1.5" /> + Create File
              </Button>
              <Button
                variant="ghost"
                className="w-full text-xs py-1.5 justify-center text-amber-400 hover:text-amber-300 border border-amber-500/30"
                onClick={() => setIsSeedModalOpen(true)}
              >
                <Sparkles size={14} className="mr-1.5" /> Load Reference Architecture
              </Button>
            </div>
          </div>
        ) : (
          files.map(file => {
            const isDirty = dirtyFileIds.includes(file.id);
            const isActive = activeFileId === file.id;

            return (
              <div
                key={file.id}
                onClick={() => setActiveFileId(file.id)}
                className={`flex items-center justify-between px-3 py-1.5 cursor-pointer text-xs group transition-colors ${
                  isActive
                    ? 'bg-blue-600/20 text-blue-200 border-l-2 border-blue-500 font-medium'
                    : 'text-gray-300 hover:bg-gray-800/60 border-l-2 border-transparent'
                }`}
              >
                <div className="flex items-center gap-2 overflow-hidden flex-1 min-w-0">
                  {getIcon(file.name)}
                  <span className="truncate">{file.name}</span>
                  {isDirty && (
                    <span 
                      className="w-2 h-2 rounded-full bg-amber-400 shrink-0" 
                      title="Unsaved changes"
                    />
                  )}
                </div>

                <div className="flex items-center opacity-0 group-hover:opacity-100 transition-opacity">
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      setDeleteConfirmId(file.id);
                    }}
                    className="text-gray-500 hover:text-red-400 p-0.5 rounded"
                    title="Delete File"
                  >
                    <Trash2 size={13} />
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* New File Modal */}
      <Modal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        title="Create New Project File"
      >
        <form onSubmit={handleCreateFile} className="space-y-4">
          <Input
            label="File Base Name"
            placeholder="e.g. MyProcess, CoolingControl, AssemblyCell"
            value={fileNameInput}
            onChange={(e) => setFileNameInput(e.target.value)}
            required
            autoFocus
          />

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              DSL File Type / Extension
            </label>
            <div className="grid grid-cols-2 gap-2">
              {[
                { ext: '.activity', name: 'Activity Diagram', icon: <Activity size={14} className="text-emerald-400" />, desc: 'Synthesis Workflow' },
                { ext: '.mnc', name: 'MNC-ML Model', icon: <Settings size={14} className="text-blue-400" />, desc: 'Component Controller' },
                { ext: '.cap', name: 'Capability Spec', icon: <Cpu size={14} className="text-purple-400" />, desc: 'Init & Control Caps' },
                { ext: '.op', name: 'Operation Desc', icon: <Settings size={14} className="text-amber-400" />, desc: 'Executable Scripts' },
                { ext: '.dml', name: 'Data Model (DML)', icon: <Database size={14} className="text-rose-400" />, desc: 'Primitives & Structs' },
                { ext: '.json', name: 'Activity JSON', icon: <FileJson size={14} className="text-yellow-400" />, desc: 'JSON Activity Spec' },
              ].map(item => (
                <button
                  key={item.ext}
                  type="button"
                  onClick={() => setSelectedExt(item.ext)}
                  className={`flex items-start gap-2.5 p-2.5 rounded-md border text-left transition-all ${
                    selectedExt === item.ext
                      ? 'bg-blue-600/20 border-blue-500 text-white shadow-sm'
                      : 'bg-[#161b22] border-gray-700 text-gray-400 hover:border-gray-500 hover:text-gray-200'
                  }`}
                >
                  <div className="mt-0.5">{item.icon}</div>
                  <div>
                    <div className="text-xs font-medium text-white">{item.name}</div>
                    <div className="text-[10px] text-gray-400">{item.desc} ({item.ext})</div>
                  </div>
                </button>
              ))}
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Starting Template
            </label>
            <select
              value={selectedTemplate}
              onChange={(e) => setSelectedTemplate(e.target.value)}
              className="w-full bg-[#161b22] border border-gray-700 text-gray-200 text-xs rounded px-3 py-2 outline-none focus:border-blue-500"
            >
              <option value="standard">Standard Metamodel Grammar Starter</option>
              <option value="cooling">Industrial Cooling System Template</option>
              <option value="assembly">Assembly Cell Supervisor Template</option>
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-2 border-t border-gray-800">
            <Button 
              type="button" 
              variant="ghost" 
              onClick={() => setIsModalOpen(false)}
            >
              Cancel
            </Button>
            <Button 
              type="submit" 
              isLoading={isCreating}
            >
              Create File
            </Button>
          </div>
        </form>
      </Modal>

      {/* Load Reference Examples Modal */}
      <Modal
        isOpen={isSeedModalOpen}
        onClose={() => setIsSeedModalOpen(false)}
        title="Load Reference Examples into Project"
      >
        <div className="space-y-3">
          <p className="text-xs text-gray-400">
            Select a verified industrial reference architecture. This will load all associated DSL files (.activity, .dml, .op, .cap, .mnc) directly into this project workspace.
          </p>

          <div className="space-y-2">
            {[
              {
                key: 'industrial_cooling',
                title: 'Industrial Cooling System',
                desc: 'Case study with multiple activities, state machines, temperature monitoring, and alarms.',
                files: 'CoolingSystem.activity, CoolingSystem.json, CoolingData.dml, CoolingOps.op, CoolingCap.cap'
              },
              {
                key: 'pick_and_place',
                title: 'Pick & Place Robotic Cell',
                desc: 'Multi-DSL robotic cell integrating capability bindings, robotic operations, and part validation.',
                files: 'PickAndPlace.activity, PickAndPlace.json, RoboticCellData.dml, RoboticCellOps.op, ScannerCap.cap'
              },
              {
                key: 'chemical_reactor',
                title: 'Chemical Reactor Plant',
                desc: 'Multi-state chemical reaction process with safety alarms and emergency quench.',
                files: 'ChemicalReactor.activity, ChemicalReactor.json, ReactorData.dml, ReactorOps.op'
              },
              {
                key: 'assembly_supervisor',
                title: 'Assembly Cell Supervisor (Demo_ECRE.dml)',
                desc: 'Automotive cell supervisor integrating repository Demo_ECRE.dml and AssemblyCell.mnc.',
                files: 'AssemblySupervisor.activity, AssemblySupervisor.json, Demo_ECRE.dml, AssemblyCell.mnc'
              }
            ].map(ex => (
              <div 
                key={ex.key}
                className="p-3 bg-[#161b22] border border-gray-700 hover:border-blue-500 rounded-md transition-all flex justify-between items-center"
              >
                <div className="pr-3">
                  <h4 className="text-xs font-semibold text-white">{ex.title}</h4>
                  <p className="text-[11px] text-gray-400 mt-0.5">{ex.desc}</p>
                  <p className="text-[10px] text-gray-500 font-mono mt-1">{ex.files}</p>
                </div>
                <Button
                  variant="secondary"
                  size="sm"
                  className="text-xs shrink-0"
                  isLoading={isSeeding}
                  onClick={() => handleSeedExamples(ex.key)}
                >
                  Load
                </Button>
              </div>
            ))}
          </div>

          <div className="flex justify-end pt-2 border-t border-gray-800">
            <Button variant="ghost" onClick={() => setIsSeedModalOpen(false)}>
              Close
            </Button>
          </div>
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={deleteConfirmId !== null}
        onClose={() => setDeleteConfirmId(null)}
        title="Confirm File Deletion"
      >
        <div className="space-y-4">
          <div className="flex items-center gap-3 text-amber-400">
            <AlertCircle size={24} />
            <p className="text-sm text-gray-300">
              Are you sure you want to delete this file? This action cannot be undone.
            </p>
          </div>
          <div className="flex justify-end gap-2">
            <Button variant="ghost" onClick={() => setDeleteConfirmId(null)}>
              Cancel
            </Button>
            <Button
              className="bg-red-600 hover:bg-red-700 text-white"
              onClick={() => deleteConfirmId && handleDeleteFile(deleteConfirmId)}
            >
              Delete
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};
