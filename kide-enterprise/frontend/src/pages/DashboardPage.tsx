import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuthStore } from '../stores/authStore';
import { projectsApi } from '../api/projects';
import { knowledgeApi } from '../api/knowledge';
import { transformData } from '../api/transform';
import ProjectCard from '../components/common/ProjectCard';
import Button from '../components/common/Button';
import Input from '../components/common/Input';
import Modal from '../components/common/Modal';
import { 
  FolderPlus, Play, LayoutDashboard, Sparkles, CheckCircle2, 
  ArrowRight, Network, Cpu, ChevronRight, ExternalLink 
} from 'lucide-react';
import { TransformResult } from '../types/models';


const REFERENCE_EXAMPLES_INFO = [
  {
    key: 'industrial_cooling',
    title: 'Industrial Cooling System',
    badge: 'Synthesis Reference Architecture',
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
    badge: 'Standard Reference (Demo_ECRE.dml)',
    description: 'Automotive assembly cell integrating Demo_ECRE.dml data model and AssemblyCell.mnc supervisory model.',
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

  const { data: storeSummary } = useQuery({
    queryKey: ['storeSummary'],
    queryFn: knowledgeApi.getStoreSummary,
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

  const selectedExample = REFERENCE_EXAMPLES_INFO[selectedExampleIndex];

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
    <div className="w-full h-full overflow-y-auto bg-[#0b0f19]">
      <div className="p-6 lg:p-8 max-w-[1780px] mx-auto flex flex-col xl:flex-row gap-8 items-start">
        
        {/* ── Left Rail: Industrial Domains & Workspaces Hub ── */}
        <aside className="w-full xl:w-80 shrink-0 flex flex-col gap-6">
          {/* 1. Quick Workspaces Navigator */}
          <div className="bg-[#111622] border border-gray-800/80 rounded-xl p-4 shadow-lg">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <FolderPlus className="w-4 h-4 text-blue-400" />
                <h3 className="text-xs font-bold uppercase tracking-wider text-gray-300">Quick Workspaces</h3>
              </div>
              <button
                onClick={() => setIsNewProjectModalOpen(true)}
                className="text-[11px] text-blue-400 hover:text-blue-300 font-semibold"
              >
                + New
              </button>
            </div>

            <div className="space-y-1.5">
              {recentProjects.map(p => (
                <button
                  key={p.id}
                  onClick={() => navigate(`/projects/${p.id}`)}
                  className="w-full text-left p-2.5 rounded-lg bg-gray-900/60 hover:bg-gray-800/80 border border-gray-800 hover:border-blue-500/40 flex items-center justify-between group transition"
                >
                  <div className="min-w-0 flex-1 pr-2">
                    <div className="font-semibold text-xs text-gray-200 group-hover:text-white truncate">
                      {p.name}
                    </div>
                    <div className="text-[10px] text-gray-500 truncate mt-0.5">
                      {p.file_count || 0} files &bull; {new Date(p.updated_at || p.created_at).toLocaleDateString()}
                    </div>
                  </div>
                  <ChevronRight className="w-3.5 h-3.5 text-gray-600 group-hover:text-blue-400 shrink-0 transition-transform group-hover:translate-x-0.5" />
                </button>
              ))}
              {recentProjects.length === 0 && (
                <div className="p-3 text-center text-xs text-gray-500 bg-gray-900/40 rounded-lg border border-dashed border-gray-800">
                  No projects yet. Click "+ New" to begin.
                </div>
              )}
            </div>
          </div>

          {/* 2. Real-World Industrial Domains Directory */}
          <div className="bg-[#111622] border border-gray-800/80 rounded-xl p-4 shadow-lg flex flex-col">
            <div className="flex items-center justify-between mb-3">
              <div className="flex items-center gap-2">
                <Cpu className="w-4 h-4 text-indigo-400" />
                <h3 className="text-xs font-bold uppercase tracking-wider text-gray-300">Industrial Domains</h3>
              </div>
              <button
                onClick={() => window.open('/knowledge-graph', '_blank')}
                className="text-[10px] text-indigo-400 hover:text-indigo-300 font-semibold flex items-center gap-1"
                title="Open complete Knowledge Graph Studio in new tab"
              >
                <span>Full Graph</span>
                <ExternalLink className="w-3 h-3" />
              </button>
            </div>
            <p className="text-[11px] text-gray-400 mb-3 leading-relaxed">
              19 real-world automation domains with 69 physical devices ready for supervisory synthesis.
            </p>

            <div className="space-y-1.5 max-h-[380px] overflow-y-auto pr-1">
              {(storeSummary?.domains || [
                { id: 'siemens_motion_s120', name: 'Siemens S7-1500 & SINAMICS S120', category: 'Motion Control' },
                { id: 'robotic_pick_and_place', name: 'Robotic Pick-and-Place Cell', category: 'Robotics & Automation' },
                { id: 'boom_barrier_system', name: 'Boom Barrier Vehicle Entry', category: 'Access Control' },
                { id: 'emerson_fisher_valves', name: 'Emerson Fisher DVC6200 Valve', category: 'Flow Control' },
                { id: 'beckhoff_twincat_ethercat', name: 'Beckhoff TwinCAT 3 & EtherCAT', category: 'High-Speed Automation' },
                { id: 'haas_cnc_machining', name: 'Haas VF-2 3-Axis CNC Center', category: 'Subtractive Machining' },
                { id: 'abb_irc5_robotics', name: 'ABB IRB 2600 & IRC5 Cell', category: 'Heavy Robotics' },
                { id: 'industrial_cooling_system', name: 'Industrial Process Cooling Loop', category: 'Process Utilities' },
                { id: 'chemical_batch_reactor', name: 'Continuous Chemical Reactor', category: 'Petrochemical' },
                { id: 'festo_cpx_pneumatics', name: 'Festo CPX Valve Terminal', category: 'Pneumatics & Fluid' },
                { id: 'keyence_vision_inspection', name: 'Keyence CV-X400 Vision System', category: 'Quality Control' },
                { id: 'sartorius_biostat_bioreactor', name: 'Sartorius BIOSTAT Bioreactor', category: 'Biotech & Pharma' },
                { id: 'sma_solar_bess_inverter', name: 'SMA Sunny Tripower & BESS', category: 'Microgrids' },
              ]).map((d) => (
                <button
                  key={d.id}
                  onClick={() => window.open(`/knowledge-graph?domain=${d.id}`, '_blank')}
                  className="w-full text-left p-2 rounded-lg bg-gray-900/60 hover:bg-indigo-950/40 border border-gray-800/80 hover:border-indigo-500/40 flex items-center justify-between group transition"
                  title={`Open ${d.name} in Knowledge Graph Studio`}
                >
                  <div className="min-w-0 flex-1 pr-2">
                    <div className="font-medium text-xs text-gray-200 group-hover:text-indigo-300 truncate">
                      {d.name}
                    </div>
                    <div className="text-[10px] text-gray-500 truncate">
                      {d.category}
                    </div>
                  </div>
                  <ExternalLink className="w-3 h-3 text-gray-600 group-hover:text-indigo-400 shrink-0" />
                </button>
              ))}
            </div>
          </div>

          {/* 3. Synthesis Platform Health Status */}
          <div className="bg-[#111622] border border-gray-800/80 rounded-xl p-4 shadow-lg space-y-2.5">
            <div className="flex items-center justify-between text-xs">
              <span className="font-bold uppercase tracking-wider text-gray-400">Synthesis Engine</span>
              <span className="flex items-center gap-1.5 text-emerald-400 font-mono text-[11px] font-semibold">
                <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
                ONLINE
              </span>
            </div>

            <div className="grid grid-cols-2 gap-2 text-[10px] font-mono text-gray-400 pt-1 border-t border-gray-800">
              <div className="bg-gray-900/80 p-2 rounded border border-gray-800/80">
                <div className="text-gray-500 text-[9px] uppercase">DSLs</div>
                <div className="font-bold text-gray-200 mt-0.5">5 Metamodels</div>
              </div>
              <div className="bg-gray-900/80 p-2 rounded border border-gray-800/80">
                <div className="text-gray-500 text-[9px] uppercase">Catalog</div>
                <div className="font-bold text-gray-200 mt-0.5">69 Devices</div>
              </div>
              <div className="bg-gray-900/80 p-2 rounded border border-gray-800/80">
                <div className="text-gray-500 text-[9px] uppercase">Semantics</div>
                <div className="font-bold text-gray-200 mt-0.5">W3C RDF/OWL</div>
              </div>
              <div className="bg-gray-900/80 p-2 rounded border border-gray-800/80">
                <div className="text-gray-500 text-[9px] uppercase">Synthesis</div>
                <div className="font-bold text-gray-200 mt-0.5">MNC-ML IR</div>
              </div>
            </div>
          </div>
        </aside>

        {/* ── Main Content Area ── */}
        <div className="flex-1 min-w-0 flex flex-col gap-8 w-full">
          {/* Header */}
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div>
              <h1 className="text-3xl font-bold text-white mb-2">Hello, {user?.full_name || user?.name || 'Researcher'}</h1>
              <p className="text-gray-400">Welcome to KIDE Enterprise — Model-Driven Supervisory Control Synthesis.</p>
            </div>
            <div className="flex flex-wrap gap-3">
              <Button 
                variant="secondary" 
                onClick={() => window.open('/knowledge-graph', '_blank')}
                className="border-indigo-500/40 text-indigo-400 hover:bg-indigo-950/40"
                title="Open Knowledge Graph Studio in a new tab"
              >
                <Network className="w-4 h-4 mr-2 text-indigo-400" /> Knowledge Graph Studio ↗
              </Button>
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
          <div className="grid grid-cols-1 sm:grid-cols-2 2xl:grid-cols-4 gap-5">
            <div className="bg-surface border border-accent p-5 rounded-xl">
              <div className="flex items-center justify-between">
                <h3 className="text-gray-400 text-xs font-semibold uppercase tracking-wider">Total Projects</h3>
                <LayoutDashboard className="text-blue-400 w-4 h-4" />
              </div>
              <p className="text-3xl font-bold text-white mt-3">{projects.length}</p>
            </div>
            <div className="bg-surface border border-accent p-5 rounded-xl">
              <div className="flex items-center justify-between">
                <h3 className="text-gray-400 text-xs font-semibold uppercase tracking-wider">Reference Templates</h3>
                <Sparkles className="text-amber-400 w-4 h-4" />
              </div>
              <p className="text-3xl font-bold text-white mt-3">{REFERENCE_EXAMPLES_INFO.length}</p>
            </div>
            <div className="bg-surface border border-accent p-5 rounded-xl">
              <div className="flex items-center justify-between">
                <h3 className="text-gray-400 text-xs font-semibold uppercase tracking-wider">Total Files</h3>
                <FolderPlus className="text-emerald-400 w-4 h-4" />
              </div>
              <p className="text-3xl font-bold text-white mt-3">{projects.reduce((sum, p) => sum + (p.file_count || 0), 0)}</p>
            </div>
            <div className="bg-surface border border-accent p-5 rounded-xl">
              <div className="flex items-center justify-between">
                <h3 className="text-gray-400 text-xs font-semibold uppercase tracking-wider">Catalog Devices</h3>
                <Cpu className="text-purple-400 w-4 h-4" />
              </div>
              <p className="text-3xl font-bold text-white mt-3">69 <span className="text-xs font-normal text-gray-400 font-mono">in 19 Domains</span></p>
            </div>
          </div>

          {/* Knowledge Graph Studio Showcase Banner */}
          <div className="bg-gradient-to-r from-[#111625] via-[#141b2f] to-[#101524] border border-indigo-500/30 rounded-xl p-6 relative overflow-hidden shadow-xl">
            <div className="absolute -right-10 -bottom-10 w-64 h-64 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none" />
            <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-6 relative z-10">
              <div className="space-y-2.5 max-w-2xl">
                <div className="flex flex-wrap items-center gap-2">
                  <span className="px-2.5 py-0.5 rounded-full bg-indigo-950/80 border border-indigo-600/40 text-indigo-300 text-[11px] font-bold uppercase tracking-wider flex items-center gap-1.5">
                    <Network className="w-3.5 h-3.5 text-indigo-400" />
                    KIDE Metamodel Ontology
                  </span>
                  <span className="text-gray-400 text-xs font-mono">&bull; 19 Domains &bull; 69+ Physical Devices</span>
                </div>
                <h2 className="text-xl font-bold text-white tracking-tight">
                  Pre-Available Industrial Knowledge Graph Studio
                </h2>
                <p className="text-gray-300 text-xs sm:text-sm leading-relaxed">
                  Explore the complete property knowledge graph across 19 real-world industrial automation domains. Inspect fine-grained metamodel ontology entities—Commands, Events, Alarms, DataPoints, Parameters, Interfaces, Capabilities, DataModels, and Supervisory Workflows—and 1-click import reusable building blocks into your projects.
                </p>
                <div className="flex flex-wrap items-center gap-2 pt-1 text-[11px] text-gray-400">
                  <span className="px-2 py-0.5 rounded bg-gray-800/80 border border-gray-700/60 text-gray-300">
                    ⚡ Fireable Commands
                  </span>
                  <span className="px-2 py-0.5 rounded bg-gray-800/80 border border-gray-700/60 text-gray-300">
                    📡 Receivable Events
                  </span>
                  <span className="px-2 py-0.5 rounded bg-gray-800/80 border border-gray-700/60 text-gray-300">
                    🚨 Raised Alarms
                  </span>
                  <span className="px-2 py-0.5 rounded bg-gray-800/80 border border-gray-700/60 text-gray-300">
                    📊 Telemetry DataPoints
                  </span>
                  <span className="px-2 py-0.5 rounded bg-gray-800/80 border border-gray-700/60 text-gray-300">
                    🐢 W3C OWL/RDF Turtle
                  </span>
                </div>
              </div>

              <div className="flex flex-col sm:flex-row lg:flex-col gap-2.5 shrink-0">
                <Button
                  onClick={() => window.open('/knowledge-graph', '_blank')}
                  className="bg-indigo-600 hover:bg-indigo-500 text-white font-semibold shadow-lg shadow-indigo-600/20 text-xs px-4 py-2.5 flex items-center justify-center gap-2"
                >
                  <Network className="w-4 h-4" />
                  <span>Open Knowledge Graph in New Tab ↗</span>
                </Button>
                <Button
                  variant="secondary"
                  onClick={() => navigate('/knowledge-graph')}
                  className="border-gray-700 hover:border-gray-600 text-gray-300 hover:text-white text-xs px-4 py-2.5 flex items-center justify-center gap-2"
                >
                  <span>Explore Studio View &rarr;</span>
                </Button>
              </div>
            </div>
          </div>

          {/* Recent Projects Section */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold text-white">Recent Projects</h2>
            </div>

            {recentProjects.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 2xl:grid-cols-3 gap-6">
                {recentProjects.map(project => (
                  <ProjectCard key={project.id} project={project} />
                ))}
              </div>
            ) : (
              <div className="bg-surface border border-dashed border-accent rounded-lg p-12 text-center">
                <p className="text-gray-400 mb-4">No projects yet. Get started by creating one or running a reference template.</p>
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
                    <Sparkles className="w-4 h-4 mr-2 text-emerald-400" /> Explore Reference Templates
                  </Button>
                </div>
              </div>
            )}
          </div>
        </div>

      </div>

      {/* Try Example Transform Modal */}
      <Modal
        isOpen={isExampleModalOpen}
        onClose={() => setIsExampleModalOpen(false)}
        title="Try Model Synthesis & Transform"
      >
        <div className="space-y-4 max-h-[80vh] overflow-y-auto pr-1">
          <p className="text-xs text-gray-400">
            Select one of the verified reference templates below to inspect the DSL, run live model transformation into supervisory MNC-ML, or load it into an editable workspace.
          </p>

          {/* Example Selector Tabs */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            {REFERENCE_EXAMPLES_INFO.map((ex, idx) => (
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
                      {(() => {
                        const m = transformResult.model;
                        const ifaceRaw = (m.interface_description as any)?.operating_states || (m.interface_description as any)?.operatingStatesUtility?.operatingStates || [];
                        const ctrlRaw = (m.control_node as any)?.operating_states || [];
                        const set = new Set<string>();
                        if (Array.isArray(ifaceRaw)) {
                          ifaceRaw.forEach((s: any) => { const n = typeof s === 'string' ? s : s?.name; if (n) set.add(n); });
                        }
                        if (Array.isArray(ctrlRaw)) {
                          ctrlRaw.forEach((s: any) => { const n = typeof s === 'string' ? s : s?.name; if (n) set.add(n); });
                        }
                        return Array.from(set).join(', ');
                      })()}
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
              <option value="industrial_cooling">Industrial Cooling System (Synthesis Reference Architecture)</option>
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
