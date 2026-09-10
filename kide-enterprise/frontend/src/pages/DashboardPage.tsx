import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../stores/authStore';
import { projectsApi } from '../api/projects';
import { transformData } from '../api/transform';
import ProjectCard from '../components/common/ProjectCard';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import { 
  FolderPlus, Play, LayoutDashboard, Sparkles, CheckCircle2, 
  ArrowRight
} from 'lucide-react';
import { TransformResult } from '../types/models';


const THESIS_EXAMPLES_INFO = [
  {
    key: 'industrial_cooling',
    title: 'Industrial Cooling System',
    badge: 'Thesis Synthesis Case Study',
    description: 'Multi-activity cooling system with temperature thresholds, state transitions (STANDBY, NORMAL, EMERGENCY), commands, events, and alarms.',
    files: ['CoolingSystem.activity', 'CoolingSystem.json', 'CoolingData.dml', 'CoolingOps.op', 'CoolingCap.cap'],
    activitySample: `ActivityDiagram CoolingSystem
uses Objects [ string status, float temperature ]
produces results ( string finalStatus )
has activities {
    Activity Initialize {
        description : "Initialize pumps and cooling valves"
        requireOperation ( StartPumpOperation )
        nextActivity : Monitor
        time : 2.0 secs
    }
    Activity Monitor {
        description : "Monitor industrial temperature"
        requireCapability : "SensorCap" { ReadTemperature }
        conditions {
            if outcome temperature is ( > 100.0 ) => nextActivity : CoolDown,
            if outcome temperature is ( < 80.0 ) => nextActivity : NormalOperation
        }
        time : 5.0 secs
    }
    Activity CoolDown {
        description : "Engage max cooling fan"
        requireOperation ( MaxFanOperation )
        nextActivity : Monitor
        time : 10.0 secs
    }
    Activity NormalOperation {
        description : "Maintain nominal cooling rate"
        nextActivity : Monitor
    }
}`,
    jsonPayload: {
      name: "CoolingSystem",
      default_operating_states: ["STANDBY", "NORMAL", "EMERGENCY"],
      activities: [
        {
          name: "Initialize",
          requires_operation: true,
          commands: [{ name: "START_PUMP" }, { name: "OPEN_VALVE" }],
          events: [{ name: "PUMP_STARTED" }],
          transitions: [{ from: "Initialize", to: "Monitor" }]
        },
        {
          name: "Monitor",
          requires_operation: false,
          events: [{ name: "TEMP_HIGH" }],
          data_points: [{ name: "temperature", type: "float" }],
          transitions: [{ from: "Monitor", to: "CoolDown", condition: "temp > 100" }]
        },
        {
          name: "CoolDown",
          requires_operation: true,
          commands: [{ name: "MAX_FAN" }],
          alarms: [{ name: "OVERHEATING", severity: "CRITICAL" }],
          transitions: [{ from: "CoolDown", to: "Monitor", condition: "temp < 80" }]
        }
      ]
    }
  },
  {
    key: 'pick_and_place',
    title: 'Pick & Place Robotic Cell',
    badge: 'Multi-DSL Synthesis',
    description: 'Robotic pick and place workflow integrating Activity, Capability (ScannerCap), Operation descriptions, and DML data models.',
    files: ['PickAndPlace.activity', 'PickAndPlace.json', 'RoboticCellData.dml', 'RoboticCellOps.op', 'ScannerCap.cap'],
    activitySample: `ActivityDiagram PickAndPlace
uses Objects [ string partId, int targetBin ]
produces results ( string status )
has activities {
    Activity ScanPart {
        description : "Scan barcode on incoming part"
        requireCapability : "ScannerCap" { ScanCommand, BarcodeScanned }
        conditions {
            if outcome barCode is ( = ("VALID") ) => nextActivity : PickPart
        }
        time : 1.5 secs
    }
    Activity PickPart {
        description : "Pick scanned part with robotic gripper"
        requireOperation ( PickOperation )
        nextActivity : PlacePart
    }
    Activity PlacePart {
        description : "Place part into target sorting bin"
        requireOperation ( PlaceOperation )
    }
}`,
    jsonPayload: {
      name: "PickAndPlace",
      default_operating_states: ["IDLE", "SCANNING", "PICKING", "PLACING", "COMPLETED"],
      activities: [
        {
          name: "ScanPart",
          requires_operation: false,
          commands: [{ name: "TRIGGER_SCAN" }],
          events: [{ name: "BARCODE_READ" }],
          data_points: [{ name: "barcode_id", type: "string" }],
          transitions: [{ from: "ScanPart", to: "PickPart" }]
        },
        {
          name: "PickPart",
          requires_operation: true,
          commands: [{ name: "GRIPPER_CLOSE" }, { name: "MOVE_Z_UP" }],
          events: [{ name: "PART_GRIPPED" }],
          alarms: [{ name: "GRIP_FAILED", level: 2 }],
          transitions: [{ from: "PickPart", to: "PlacePart" }]
        },
        {
          name: "PlacePart",
          requires_operation: true,
          commands: [{ name: "MOVE_BIN" }, { name: "GRIPPER_OPEN" }],
          events: [{ name: "PART_RELEASED" }]
        }
      ]
    }
  },
  {
    key: 'chemical_reactor',
    title: 'Chemical Reactor Plant',
    badge: 'State Machine & Safety',
    description: 'Extreme multi-state process testing (OFF, HEATING, REACTION, COOLING, ERROR), temperature transitions, and emergency quench alarms.',
    files: ['ChemicalReactor.activity', 'ChemicalReactor.json', 'ReactorData.dml', 'ReactorOps.op'],
    activitySample: `ActivityDiagram ChemicalReactor
uses Objects [ float reactorTemp, float pressure ]
produces results ( string batchStatus )
has activities {
    Activity HeatUp {
        description : "Heat reactor to activation temperature"
        requireOperation ( StartHeaterOperation )
        nextActivity : React
        time : 120.0 secs
    }
    Activity React {
        description : "Controlled chemical reaction phase"
        requireCapability : "ReactionCap" { MonitorReaction }
        nextActivity : Quench
        time : 300.0 secs
    }
    Activity Quench {
        description : "Emergency coolant injection"
        requireOperation ( CoolantValveOperation )
    }
}`,
    jsonPayload: {
      name: "ChemicalReactor",
      default_operating_states: ["OFF", "HEATING", "REACTION", "COOLING", "ERROR"],
      activities: [
        {
          name: "HeatUp",
          requires_operation: true,
          commands: [{ name: "START_HEATER" }],
          data_points: [{ name: "reactor_temp", type: "float" }],
          transitions: [{ from: "HeatUp", to: "React", condition: "reactor_temp > 200" }]
        },
        {
          name: "React",
          requires_operation: false,
          commands: [{ name: "START_STIRRER" }],
          events: [{ name: "REACTION_COMPLETE" }],
          alarms: [{ name: "OVERPRESSURE", severity: "CRITICAL" }],
          transitions: [{ from: "React", to: "Quench" }]
        },
        {
          name: "Quench",
          requires_operation: true,
          commands: [{ name: "INJECT_COOLANT" }],
          events: [{ name: "COOLED_DOWN" }]
        }
      ]
    }
  },
  {
    key: 'assembly_supervisor',
    title: 'Assembly Cell Supervisor',
    badge: 'Thesis Repo (Demo_ECRE.dml)',
    description: 'Automotive assembly cell from thesis Section 5 integrating the repository Demo_ECRE.dml data model and AssemblyCell.mnc.',
    files: ['AssemblySupervisor.activity', 'AssemblySupervisor.json', 'Demo_ECRE.dml', 'AssemblyCell.mnc'],
    activitySample: `ActivityDiagram AssemblySupervisor
uses Objects [ boolean isValidTruck, int RFID_TAG_VALUE ]
produces results ( string status )
has activities {
    Activity ReadRFID {
        description : "Read truck RFID tag at entrance bay"
        requireCapability : "RFIDCap" { ReadTagCommand, TagReadEvent }
        nextActivity : ValidateTruck
    }
    Activity ValidateTruck {
        description : "Validate RFID credentials against ECRE manifest"
        requireOperation ( ValidateECREOperation )
        nextActivity : DispatchAssembly
    }
    Activity DispatchAssembly {
        description : "Dispatch guided assembly instructions to station"
        requireOperation ( DispatchOperation )
    }
}`,
    jsonPayload: {
      name: "AssemblySupervisor",
      default_operating_states: ["IDLE", "RUNNING", "STOPPED", "FAULT"],
      activities: [
        {
          name: "PickPart",
          requires_operation: true,
          parameters: ["partType"],
          commands: [{ name: "pick" }],
          events: [{ name: "part_detected" }],
          alarms: [{ name: "gripper_fault" }],
          dataPoints: [{ name: "part_id" }],
          transitions: [{ from: "PickPart", to: "PlacePart" }]
        },
        {
          name: "PlacePart",
          requires_operation: true,
          commands: [{ name: "place" }],
          events: [{ name: "part_placed" }],
          transitions: [{ from: "PlacePart", to: "PickPart" }]
        }
      ]
    }
  }
];

const DashboardPage = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = useAuthStore(state => state.user);

  const [isNewProjectModalOpen, setIsNewProjectModalOpen] = useState(false);
  const [isExampleModalOpen, setIsExampleModalOpen] = useState(false);
  const [selectedExampleIndex, setSelectedExampleIndex] = useState(0);
  
  // Transform State in Modal
  const [transformRunning, setTransformRunning] = useState(false);
  const [transformResult, setTransformResult] = useState<TransformResult | null>(null);
  const [openingWorkspace, setOpeningWorkspace] = useState(false);

  // New Project Form State
  const [projectName, setProjectName] = useState('');
  const [projectDesc, setProjectDesc] = useState('');
  const [projectTemplate, setProjectTemplate] = useState('industrial_cooling');

  const { data: projects = [] } = useQuery({
    queryKey: ['projects'],
    queryFn: projectsApi.listProjects,
  });

  const recentProjects = [...projects].sort((a, b) => 
    new Date(b.updated_at || b.created_at).getTime() - new Date(a.updated_at || a.created_at).getTime()
  ).slice(0, 6);

  const createMutation = useMutation({
    mutationFn: async (data: { name: string; description: string; template: string }) => {
      if (data.template === 'blank') {
        return projectsApi.createProject({ name: data.name, description: data.description });
      } else {
        return projectsApi.createFromTemplate({ 
          name: data.name, 
          template: data.template, 
          description: data.description 
        });
      }
    },
    onSuccess: (newProj) => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      setIsNewProjectModalOpen(false);
      setProjectName('');
      setProjectDesc('');
      navigate(`/projects/${newProj.id}`);
    }
  });

  const handleCreateProject = (e: React.FormEvent) => {
    e.preventDefault();
    if (!projectName.trim()) return;
    createMutation.mutate({
      name: projectName.trim(),
      description: projectDesc.trim(),
      template: projectTemplate
    });
  };

  const selectedExample = THESIS_EXAMPLES_INFO[selectedExampleIndex];

  const handleQuickTransform = async () => {
    setTransformRunning(true);
    setTransformResult(null);
    try {
      const res = await transformData({
        activity_diagram: selectedExample.jsonPayload
      });
      setTransformResult(res);
    } catch (err) {
      console.error('Transform error:', err);
    } finally {
      setTransformRunning(false);
    }
  };

  const handleOpenExampleInWorkspace = async () => {
    setOpeningWorkspace(true);
    try {
      const newProj = await projectsApi.createFromTemplate({
        name: `${selectedExample.title}`,
        template: selectedExample.key,
        description: selectedExample.description
      });
      queryClient.invalidateQueries({ queryKey: ['projects'] });
      setIsExampleModalOpen(false);
      navigate(`/projects/${newProj.id}`);
    } catch (err) {
      console.error('Failed to open example in workspace:', err);
    } finally {
      setOpeningWorkspace(false);
    }
  };

  return (
    <div className="p-8 max-w-7xl mx-auto">
      {/* Header */}
      <div className="mb-8 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold text-white mb-2">Hello, {user?.full_name || user?.name || 'Researcher'}</h1>
          <p className="text-gray-400">Welcome to KIDE Enterprise — Model-Driven Supervisory Control Synthesis.</p>
        </div>
        <div className="flex gap-3">
          <Button 
            variant="secondary" 
            onClick={() => {
              setTransformResult(null);
              setIsExampleModalOpen(true);
            }}
            className="border-emerald-500/40 text-emerald-400 hover:bg-emerald-950/40"
          >
            <Sparkles className="w-4 h-4 mr-2 text-emerald-400" /> Try Example Transform
          </Button>
          <Button onClick={() => setIsNewProjectModalOpen(true)}>
            <FolderPlus className="w-4 h-4 mr-2" /> New Project
          </Button>
        </div>
      </div>

      {/* Metrics Banner */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
        <div className="bg-surface border border-accent p-6 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-gray-400 font-medium">Total Projects</h3>
            <LayoutDashboard className="text-highlight w-5 h-5" />
          </div>
          <p className="text-3xl font-bold text-white mt-4">{projects.length}</p>
        </div>
        <div className="bg-surface border border-accent p-6 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-gray-400 font-medium">Thesis Templates</h3>
            <Sparkles className="text-amber-400 w-5 h-5" />
          </div>
          <p className="text-3xl font-bold text-white mt-4">{THESIS_EXAMPLES_INFO.length}</p>
        </div>
        <div className="bg-surface border border-accent p-6 rounded-lg">
          <div className="flex items-center justify-between">
            <h3 className="text-gray-400 font-medium">Total Project Files</h3>
            <FolderPlus className="text-highlight w-5 h-5" />
          </div>
          <p className="text-3xl font-bold text-white mt-4">{projects.reduce((sum, p) => sum + (p.file_count || 0), 0)}</p>
        </div>
      </div>

      {/* Recent Projects Section */}
      <div className="mb-6 flex items-center justify-between">
        <h2 className="text-xl font-semibold text-white">Recent Projects</h2>
      </div>

      {recentProjects.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {recentProjects.map(project => (
            <ProjectCard key={project.id} project={project} />
          ))}
        </div>
      ) : (
        <div className="bg-surface border border-dashed border-accent rounded-lg p-12 text-center">
          <p className="text-gray-400 mb-4">No projects yet. Get started by creating one or running a thesis example.</p>
          <div className="flex justify-center gap-3">
            <Button onClick={() => setIsNewProjectModalOpen(true)}>
              <FolderPlus className="w-4 h-4 mr-2" /> Create First Project
            </Button>
            <Button 
              variant="secondary"
              onClick={() => {
                setTransformResult(null);
                setIsExampleModalOpen(true);
              }}
            >
              <Sparkles className="w-4 h-4 mr-2 text-emerald-400" /> Explore Thesis Examples
            </Button>
          </div>
        </div>
      )}

      {/* Try Example Transform Modal */}
      <Modal
        isOpen={isExampleModalOpen}
        onClose={() => setIsExampleModalOpen(false)}
        title="Try Thesis Synthesis & Transform"
      >
        <div className="space-y-4 max-h-[80vh] overflow-y-auto pr-1">
          <p className="text-xs text-gray-400">
            Select one of the verified PhD thesis examples below to inspect the DSL, run live model transformation into supervisory MNC-ML, or load it into an editable workspace.
          </p>

          {/* Example Selector Tabs */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            {THESIS_EXAMPLES_INFO.map((ex, idx) => (
              <button
                key={ex.key}
                type="button"
                onClick={() => {
                  setSelectedExampleIndex(idx);
                  setTransformResult(null);
                }}
                className={`p-2 rounded border text-left transition-all ${
                  selectedExampleIndex === idx
                    ? 'bg-blue-600/20 border-blue-500 text-white shadow'
                    : 'bg-[#161b22] border-gray-700 text-gray-400 hover:border-gray-500 hover:text-gray-200'
                }`}
              >
                <div className="text-[11px] font-semibold text-white truncate">{ex.title}</div>
                <span className="text-[9px] text-emerald-400 block truncate mt-0.5">{ex.badge}</span>
              </button>
            ))}
          </div>

          {/* Selected Example Details */}
          <div className="p-3 bg-[#161b22] border border-gray-800 rounded-lg">
            <div className="flex justify-between items-center mb-1">
              <h3 className="text-sm font-bold text-white">{selectedExample.title}</h3>
              <span className="text-[10px] bg-emerald-950 text-emerald-400 border border-emerald-800 px-2 py-0.5 rounded font-mono">
                {selectedExample.badge}
              </span>
            </div>
            <p className="text-xs text-gray-400 mb-2">{selectedExample.description}</p>
            <div className="flex flex-wrap gap-1 mb-3">
              {selectedExample.files.map(f => (
                <span key={f} className="text-[10px] bg-gray-800 text-gray-300 font-mono px-1.5 py-0.5 rounded">
                  {f}
                </span>
              ))}
            </div>

            {/* Code Preview */}
            <div className="relative">
              <div className="text-[10px] uppercase font-bold text-gray-400 mb-1">Activity Diagram DSL:</div>
              <pre className="p-3 bg-[#0d1117] border border-gray-800 rounded text-xs text-gray-300 font-mono overflow-x-auto max-h-48">
                {selectedExample.activitySample}
              </pre>
            </div>
          </div>

          {/* Transform Result Display */}
          {transformResult && (
            <div className="p-3 bg-[#0f2027] border border-emerald-800/80 rounded-lg space-y-2">
              <div className="flex items-center gap-2 text-emerald-400 text-xs font-semibold">
                <CheckCircle2 size={16} />
                <span>MNC-ML Synthesis Succeeded! Model: {transformResult.model?.name}</span>
              </div>
              
              {transformResult.model?.interface_description && (
                <div className="text-xs text-gray-300 space-y-1 bg-[#161b22] p-2.5 rounded border border-gray-800">
                  <div className="font-semibold text-gray-200">
                    Interface: {transformResult.model.interface_description.name}
                  </div>
                  <div className="grid grid-cols-2 gap-2 text-[11px] text-gray-400 mt-1">
                    <div>
                      <span className="font-bold text-blue-400">Commands ({transformResult.model.interface_description.commands?.length || 0}): </span>
                      {transformResult.model.interface_description.commands?.map(c => c.name).join(', ')}
                    </div>
                    <div>
                      <span className="font-bold text-emerald-400">Events ({transformResult.model.interface_description.events?.length || 0}): </span>
                      {transformResult.model.interface_description.events?.map(e => e.name).join(', ')}
                    </div>
                    <div>
                      <span className="font-bold text-red-400">Alarms ({transformResult.model.interface_description.alarms?.length || 0}): </span>
                      {transformResult.model.interface_description.alarms?.map(a => a.name).join(', ')}
                    </div>
                    <div>
                      <span className="font-bold text-amber-400">Operating States: </span>
                      {(((transformResult.model.control_node as any)?.operating_states || (transformResult.model.interface_description as any)?.operating_states?.states || []) as any[])
                        .map((s: any) => typeof s === 'string' ? s : s?.name)
                        .filter(Boolean)
                        .join(', ')}
                    </div>

                  </div>
                </div>
              )}
            </div>
          )}

          {/* Action Buttons */}
          <div className="flex justify-between items-center pt-2 border-t border-gray-800">
            <Button
              variant="ghost"
              onClick={() => setIsExampleModalOpen(false)}
            >
              Close
            </Button>
            <div className="flex gap-2">
              <Button
                variant="secondary"
                onClick={handleQuickTransform}
                isLoading={transformRunning}
                className="text-xs"
              >
                <Play className="w-3.5 h-3.5 mr-1.5" />
                Run Quick Transform
              </Button>
              <Button
                onClick={handleOpenExampleInWorkspace}
                isLoading={openingWorkspace}
                className="text-xs bg-blue-600 hover:bg-blue-500"
              >
                Open in Project Workspace
                <ArrowRight className="w-3.5 h-3.5 ml-1.5" />
              </Button>
            </div>
          </div>
        </div>
      </Modal>

      {/* New Project Modal */}
      <Modal
        isOpen={isNewProjectModalOpen}
        onClose={() => setIsNewProjectModalOpen(false)}
        title="Create New Project"
      >
        <form onSubmit={handleCreateProject} className="space-y-4">
          <Input
            label="Project Name"
            placeholder="e.g. Factory_Line_1, Cooling_Module"
            required
            value={projectName}
            onChange={e => setProjectName(e.target.value)}
            autoFocus
          />

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Project Starter Template
            </label>
            <select
              value={projectTemplate}
              onChange={e => setProjectTemplate(e.target.value)}
              className="w-full bg-[#161b22] border border-gray-700 text-gray-200 text-xs rounded px-3 py-2 outline-none focus:border-blue-500"
            >
              <option value="industrial_cooling">Industrial Cooling System (Thesis Synthesis Case Study)</option>
              <option value="pick_and_place">Pick & Place Robotic Cell (Multi-DSL)</option>
              <option value="chemical_reactor">Chemical Reactor Plant (State Machine & Alarms)</option>
              <option value="assembly_supervisor">Assembly Cell Supervisor (Demo_ECRE.dml)</option>
              <option value="blank">Blank Project (Empty Workspace)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-gray-300 uppercase tracking-wider mb-1.5">
              Description (Optional)
            </label>
            <textarea
              className="w-full rounded-md border border-gray-700 bg-[#161b22] px-3 py-2 text-xs text-white focus:ring-2 focus:ring-blue-500 outline-none"
              rows={3}
              placeholder="Provide context or notes about this synthesis project..."
              value={projectDesc}
              onChange={e => setProjectDesc(e.target.value)}
            />
          </div>

          <div className="flex justify-end space-x-2 pt-2 border-t border-gray-800">
            <Button variant="ghost" type="button" onClick={() => setIsNewProjectModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit" isLoading={createMutation.isPending}>
              Create Project
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default DashboardPage;
