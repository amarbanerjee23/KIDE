import React from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { 
  getWorkflowStagesStatus, StageStatusInfo, LifecycleStatus 
} from '../../utils/workflowState';
import { 
  Check, AlertTriangle, AlertCircle, RefreshCw, 
  Database, Cpu, Activity, GitMerge 
} from 'lucide-react';

export const WorkflowStepper: React.FC = () => {
  const { 
    files, transformResult, validationErrors, dirtyFileIds, 
    isSynthesisStale, activeStage, setActiveStage,
    stageLastFileId, setStageLastFileId, setActiveFileId,
    activeView, setActiveView
  } = useEditorStore();

  const stages = getWorkflowStagesStatus(
    files,
    transformResult,
    validationErrors,
    dirtyFileIds,
    isSynthesisStale
  );

  const getFname = (f: any) => f.filename || f.name || '';

  const handleStageClick = (stage: StageStatusInfo) => {
    setActiveStage(stage.number);

    // Context preservation: do NOT blindly destroy the user's workspace tool.
    // Only associate relevant file if currently in Code editor view.
    if (activeView === 'editor') {
      const lastFileId = stageLastFileId[stage.number];
      if (lastFileId && files.some(f => f.id === lastFileId)) {
        setActiveFileId(lastFileId);
        return;
      }

      if (stage.number === 1) {
        const dml = files.find(f => getFname(f).endsWith('.dml'));
        if (dml) {
          setActiveFileId(dml.id);
          setStageLastFileId(1, dml.id);
        }
      } else if (stage.number === 2) {
        const capOp = files.find(f => 
          getFname(f).endsWith('.cap') || 
          getFname(f).endsWith('.capability') || 
          getFname(f).endsWith('.op') || 
          getFname(f).endsWith('.operation')
        );
        if (capOp) {
          setActiveFileId(capOp.id);
          setStageLastFileId(2, capOp.id);
        }
      } else if (stage.number === 3) {
        const act = files.find(f => getFname(f).endsWith('.activity'));
        if (act) {
          setActiveFileId(act.id);
          setStageLastFileId(3, act.id);
        }
      }
    } else if (stage.number === 3 && activeView !== 'workflow') {
      // Suggesting workflow tool if user explicitly clicks Stage 3 from an unrelated view
      setActiveView('workflow');
    } else if (stage.number === 4 && activeView !== 'statemachine' && activeView !== 'codegen') {
      // Suggesting state machine if user explicitly clicks Stage 4 from an unrelated view
      setActiveView('statemachine');
    }
  };

  const getStageIcon = (number: number) => {
    switch (number) {
      case 1: return Database;
      case 2: return Cpu;
      case 3: return Activity;
      case 4: return GitMerge;
      default: return Database;
    }
  };

  const renderStatusBadge = (status: LifecycleStatus, text: string) => {
    switch (status) {
      case 'VALID':
      case 'COMPLETE':
        return (
          <span className="flex items-center gap-1 text-[11px] font-medium text-emerald-400">
            <Check className="w-3 h-3 text-emerald-400" />
            <span className="truncate">{text}</span>
          </span>
        );
      case 'IN_PROGRESS':
        return (
          <span className="flex items-center gap-1 text-[11px] font-medium text-blue-400">
            <span className="w-1.5 h-1.5 rounded-full bg-blue-400 animate-pulse"></span>
            <span className="truncate">{text}</span>
          </span>
        );
      case 'WARNING':
        return (
          <span className="flex items-center gap-1 text-[11px] font-medium text-amber-400">
            <AlertTriangle className="w-3 h-3 text-amber-400" />
            <span className="truncate">{text}</span>
          </span>
        );
      case 'ERROR':
        return (
          <span className="flex items-center gap-1 text-[11px] font-medium text-rose-400">
            <AlertCircle className="w-3 h-3 text-rose-400" />
            <span className="truncate">{text}</span>
          </span>
        );
      case 'STALE':
        return (
          <span className="flex items-center gap-1 text-[11px] font-medium text-amber-300">
            <RefreshCw className="w-3 h-3 text-amber-300 animate-spin-reverse" />
            <span className="truncate">{text}</span>
          </span>
        );
      default:
        return (
          <span className="text-[11px] text-gray-500 truncate">
            {text}
          </span>
        );
    }
  };

  return (
    <nav 
      aria-label="Engineering Workflow Stepper"
      className="bg-[#0d1117] border-b border-gray-800 px-4 py-2 shrink-0 select-none"
    >
      <div className="grid grid-cols-2 md:grid-cols-4 gap-2 w-full max-w-full">
        {stages.map((stage) => {
          const isSelected = activeStage === stage.number;
          const StageIcon = getStageIcon(stage.number);

          return (
            <button
              key={stage.id}
              onClick={() => handleStageClick(stage)}
              aria-current={isSelected ? 'step' : undefined}
              className={`flex items-start gap-2.5 p-2 rounded-xl text-left transition-all border group relative min-w-0 ${
                isSelected
                  ? 'bg-blue-600/10 border-blue-500/50 shadow-sm'
                  : 'bg-[#161b22]/70 hover:bg-[#1c2128] border-gray-800 hover:border-gray-700'
              }`}
            >
              {/* Step Number Circle */}
              <div 
                className={`w-6 h-6 rounded-lg flex items-center justify-center font-bold text-xs shrink-0 transition-colors ${
                  isSelected
                    ? 'bg-blue-600 text-white'
                    : stage.status === 'VALID' || stage.status === 'COMPLETE'
                    ? 'bg-emerald-950/80 text-emerald-300 border border-emerald-800/60'
                    : stage.status === 'ERROR'
                    ? 'bg-rose-950/80 text-rose-300 border border-rose-800/60'
                    : 'bg-gray-800 text-gray-400 group-hover:text-gray-200'
                }`}
              >
                {stage.number}
              </div>

              {/* Step Info */}
              <div className="flex-1 min-w-0">
                <div className="flex items-center justify-between gap-1">
                  <span className={`text-xs font-semibold truncate ${
                    isSelected ? 'text-gray-100' : 'text-gray-300 group-hover:text-white'
                  }`}>
                    {stage.title}
                  </span>
                  <StageIcon className={`w-3.5 h-3.5 shrink-0 hidden sm:block ${
                    isSelected ? 'text-blue-400' : 'text-gray-600'
                  }`} />
                </div>

                <div className="text-[11px] text-gray-400 truncate mt-0.5">
                  {stage.summary}
                </div>

                <div className="mt-1">
                  {renderStatusBadge(stage.status, stage.statusText)}
                </div>
              </div>
            </button>
          );
        })}
      </div>
    </nav>
  );
};
