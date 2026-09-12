import React, { useState } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { transformWorkspace } from '../../api/transform';
import { 
  Database, Cpu, Activity, Layers, FileCode, CheckCircle2, 
  ChevronRight, Sparkles, HelpCircle, Loader2, BookOpen
} from 'lucide-react';
import { CodeGenerationModal } from './CodeGenerationModal';
import { KnowledgeCatalogModal } from '../knowledge/KnowledgeCatalogModal';

export const SynthesisWorkflowStepper: React.FC = () => {
  const { 
    files, activeFileId, transformResult, isTransforming,
    setActiveFileId, setTransformResult, setIsTransforming, setActiveView
  } = useEditorStore();

  const [currentStep, setCurrentStep] = useState<number>(1);
  const [isCodeModalOpen, setIsCodeModalOpen] = useState<boolean>(false);
  const [isKnowledgeModalOpen, setIsKnowledgeModalOpen] = useState<boolean>(false);
  const [showGuidance, setShowGuidance] = useState<boolean>(false);

  // File metrics (safely handle both name and filename attributes)
  const getFname = (f: any) => f.filename || f.name || '';
  const dmlFiles = files.filter(f => getFname(f).endsWith('.dml'));
  const capFiles = files.filter(f => getFname(f).endsWith('.cap') || getFname(f).endsWith('.capability'));
  const opFiles = files.filter(f => getFname(f).endsWith('.op') || getFname(f).endsWith('.operation'));
  const actFiles = files.filter(f => getFname(f).endsWith('.activity'));
  const mncFiles = files.filter(f => getFname(f).endsWith('.mnc') || getFname(f).endsWith('.mncspec'));

  // Step completion checks
  const step1Complete = dmlFiles.length > 0;
  const step2Complete = capFiles.length > 0 || opFiles.length > 0;
  const step3Complete = actFiles.length > 0;
  const step4Complete = transformResult !== null || mncFiles.length > 0;
  const step5Complete = transformResult !== null;

  // Step details
  const steps = [
    {
      step: 1,
      title: 'Data Modeling',
      ext: '.dml',
      icon: Database,
      completed: step1Complete,
      badge: `${dmlFiles.length} Schema${dmlFiles.length !== 1 ? 's' : ''}`,
      desc: 'Define domain data structures, primitive parameters, and composite entities using DML (Thesis Section 3.3).'
    },
    {
      step: 2,
      title: 'Capabilities & Ops',
      ext: '.cap, .op',
      icon: Cpu,
      completed: step2Complete,
      badge: `${capFiles.length} Cap, ${opFiles.length} Op`,
      desc: 'Declare device contracts, provided control capabilities, fireable commands, and low-level executable scripts (Thesis Section 4.1).'
    },
    {
      step: 3,
      title: 'Supervisory Workflow',
      ext: '.activity',
      icon: Activity,
      completed: step3Complete,
      badge: `${actFiles.length} Diagram${actFiles.length !== 1 ? 's' : ''}`,
      desc: 'Orchestrate multi-step supervisory process logic, event conditions, and capability bindings (Thesis Section 4.2).'
    },
    {
      step: 4,
      title: 'Automated Synthesis',
      ext: '.mnc',
      icon: Layers,
      completed: step4Complete,
      badge: step4Complete ? 'Synthesized' : 'Ready',
      desc: 'Automatically transform the activity workflow and device capabilities into a formal MNC-ML supervisory model (Thesis Chapter 5).'
    },
    {
      step: 5,
      title: 'Code Studio & Simulator',
      ext: 'Python / ROS2 / Java / PLC',
      icon: FileCode,
      completed: step5Complete,
      badge: step5Complete ? 'Ready' : 'Pending',
      desc: 'Inspect code generation templates and synthesize executable Python, ROS2, Java, and PLC controllers with live simulation.'
    }
  ];

  // Navigate to corresponding view and file on step click
  const handleStepClick = (stepNum: number) => {
    setCurrentStep(stepNum);

    if (stepNum === 1) {
      if (dmlFiles.length > 0) setActiveFileId(dmlFiles[0].id);
      setActiveView('editor');
    } else if (stepNum === 2) {
      if (capFiles.length > 0) setActiveFileId(capFiles[0].id);
      else if (opFiles.length > 0) setActiveFileId(opFiles[0].id);
      setActiveView('editor');
    } else if (stepNum === 3) {
      if (actFiles.length > 0) setActiveFileId(actFiles[0].id);
      setActiveView('workflow');
    } else if (stepNum === 4) {
      if (mncFiles.length > 0) setActiveFileId(mncFiles[0].id);
      setActiveView('statemachine');
    } else if (stepNum === 5) {
      setActiveView('codegen');
    }
  };

  // Run Synthesis Engine
  const handleRunSynthesis = async () => {
    setIsTransforming(true);
    try {
      const activeFile = files.find(f => f.id === activeFileId);
      const res = await transformWorkspace(files, activeFile?.id);
      setTransformResult(res);
      setCurrentStep(4);
      setActiveView('statemachine');
    } catch (err) {
      alert(`Synthesis failed: ${err}`);
    } finally {
      setIsTransforming(false);
    }
  };

  return (
    <div className="bg-[#161b22] border-b border-gray-800 text-gray-200">
      
      {/* Stepper Header Bar */}
      <div className="flex items-center justify-between px-4 py-2">
        <div className="flex items-center gap-1.5 overflow-x-auto scrollbar-none">
          {steps.map((s, idx) => {
            const Icon = s.icon;
            const isActive = currentStep === s.step;
            return (
              <React.Fragment key={s.step}>
                <button
                  onClick={() => handleStepClick(s.step)}
                  className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-xs font-semibold transition-all group relative ${
                    isActive
                      ? 'bg-blue-600/20 text-blue-400 border border-blue-500/40 shadow-sm'
                      : s.completed
                      ? 'text-gray-300 hover:text-white hover:bg-gray-800'
                      : 'text-gray-500 hover:text-gray-400 hover:bg-gray-800/50'
                  }`}
                >
                  <div className={`p-1 rounded-md ${
                    isActive 
                      ? 'bg-blue-500 text-white' 
                      : s.completed 
                      ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30' 
                      : 'bg-gray-800 text-gray-400'
                  }`}>
                    {s.completed ? (
                      <CheckCircle2 className="w-3.5 h-3.5" />
                    ) : (
                      <Icon className="w-3.5 h-3.5" />
                    )}
                  </div>

                  <div className="text-left">
                    <div className="flex items-center gap-1.5">
                      <span className="leading-tight">Step {s.step}: {s.title}</span>
                      <span className={`text-[10px] px-1.5 py-0.2 rounded font-mono ${
                        s.completed ? 'bg-emerald-950 text-emerald-300 border border-emerald-800/50' : 'bg-gray-800 text-gray-400'
                      }`}>
                        {s.badge}
                      </span>
                    </div>
                  </div>
                </button>

                {idx < steps.length - 1 && (
                  <ChevronRight className="w-3.5 h-3.5 text-gray-600 flex-shrink-0" />
                )}
              </React.Fragment>
            );
          })}
        </div>

        {/* Step Action Buttons */}
        <div className="flex items-center gap-2 ml-4">
          <button
            onClick={() => setIsKnowledgeModalOpen(true)}
            className="flex items-center gap-1.5 px-2.5 py-1.5 bg-indigo-950/60 hover:bg-indigo-900/80 border border-indigo-700/50 text-indigo-300 text-xs font-semibold rounded-lg shadow transition-all"
            title="Browse Real-World Equipment & Ontologies"
          >
            <BookOpen className="w-3.5 h-3.5 text-indigo-400" />
            <span>Knowledge Hub</span>
          </button>

          <button
            onClick={() => setShowGuidance(!showGuidance)}
            className="p-1.5 text-gray-400 hover:text-blue-400 hover:bg-gray-800 rounded-lg transition-colors"
            title="Toggle Thesis Step Guidance"
          >
            <HelpCircle className="w-4 h-4" />
          </button>

          {currentStep === 4 || !transformResult ? (
            <button
              onClick={handleRunSynthesis}
              disabled={isTransforming || actFiles.length === 0}
              className="flex items-center gap-1.5 px-3 py-1.5 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-500 hover:to-indigo-500 disabled:opacity-50 text-white text-xs font-semibold rounded-lg shadow transition-all"
            >
              {isTransforming ? (
                <Loader2 className="w-3.5 h-3.5 animate-spin" />
              ) : (
                <Sparkles className="w-3.5 h-3.5" />
              )}
              <span>Synthesize MNC Model</span>
            </button>
          ) : (
            <button
              onClick={() => setIsCodeModalOpen(true)}
              className="flex items-center gap-1.5 px-3 py-1.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white text-xs font-semibold rounded-lg shadow transition-all"
            >
              <Sparkles className="w-3.5 h-3.5" />
              <span>Code Studio & Live Runner</span>
            </button>
          )}
        </div>
      </div>

      {/* Expandable Guidance Tray */}
      {showGuidance && (
        <div className="px-6 py-3 bg-[#0d1117] border-t border-gray-800 text-xs animate-in slide-in-from-top-2 duration-200">
          <div className="flex items-start justify-between">
            <div className="max-w-3xl">
              <span className="font-semibold text-blue-400 uppercase tracking-wide text-[10px] block mb-1">
                Step {currentStep} Guidance &bull; {steps[currentStep - 1].title}
              </span>
              <p className="text-gray-300 leading-relaxed">
                {steps[currentStep - 1].desc}
              </p>
            </div>
            <button
              onClick={() => setShowGuidance(false)}
              className="text-gray-500 hover:text-gray-300 text-xs ml-4"
            >
              Dismiss
            </button>
          </div>
        </div>
      )}

      {/* Code Generation Studio Modal */}
      <CodeGenerationModal 
        isOpen={isCodeModalOpen}
        onClose={() => setIsCodeModalOpen(false)}
      />

      {/* External Knowledge Catalog Modal */}
      <KnowledgeCatalogModal
        isOpen={isKnowledgeModalOpen}
        onClose={() => setIsKnowledgeModalOpen(false)}
      />
    </div>
  );
};
