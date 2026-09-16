import React, { useState, useEffect, useRef } from 'react';
import Editor from '@monaco-editor/react';
import { useEditorStore } from '../../stores/editorStore';
import { 
  exportPython, exportRos2, exportJava, exportPlc, exportCpp, exportDsl, exportJson, downloadZipBundle 
} from '../../api/export';
import { generatorsApi, GeneratorInfo, GeneratorTemplateInfo } from '../../api/generators';
import { 
  Download, Copy, Check, Play, Loader2, Wrench, Eye, 
  BookOpen, ChevronDown, Sparkles, FileCode, Cpu, Layers, 
  Code, FileCheck, ArrowRight
} from 'lucide-react';

interface Props {
  onClose?: () => void;
  isEmbedded?: boolean;
}

type TargetTab = 'python' | 'ros2' | 'java' | 'plc' | 'cpp' | 'custom' | 'mnc' | 'json';
type ViewMode = 'output' | 'template';

export const CodeGenerationStudio: React.FC<Props> = () => {
  const { transformResult, projectName, projectId, setActiveView } = useEditorStore();
  const [activeTab, setActiveTab] = useState<TargetTab>('python');
  const [viewMode, setViewMode] = useState<ViewMode>('output');
  
  const [outputContent, setOutputContent] = useState<string>('');
  const [templateContent, setTemplateContent] = useState<string>('');
  const [templateMeta, setTemplateMeta] = useState<GeneratorTemplateInfo | null>(null);
  
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [copied, setCopied] = useState<boolean>(false);
  const [isDownloading, setIsDownloading] = useState<boolean>(false);
  const [isMoreOpen, setIsMoreOpen] = useState<boolean>(false);

  // Custom generator states
  const [generators, setGenerators] = useState<GeneratorInfo[]>([]);
  const [selectedGeneratorId, setSelectedGeneratorId] = useState<string>('python');
  const [customScriptCode, setCustomScriptCode] = useState<string>('');
  const [isExecutingGen, setIsExecutingGen] = useState<boolean>(false);
  const [genStatus, setGenStatus] = useState<string | null>(null);

  const moreMenuRef = useRef<HTMLDivElement>(null);

  const model = transformResult?.model;
  const modelName = model?.name || projectName || 'Supervisor';

  // Close more menu on outside click
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (moreMenuRef.current && !moreMenuRef.current.contains(e.target as Node)) {
        setIsMoreOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Load custom generators list
  useEffect(() => {
    if (!projectId) return;
    const loadGens = async () => {
      try {
        const list = await generatorsApi.listGenerators(projectId);
        setGenerators(list);
      } catch (err) {
        console.error('Failed to load generators:', err);
      }
    };
    loadGens();
  }, [projectId]);

  // Load code and template source for active tab
  useEffect(() => {
    if (!model && activeTab !== 'custom') return;

    const loadData = async () => {
      setIsLoading(true);
      try {
        // 1. Generate Target Code
        let output = '';
        if (model) {
          if (activeTab === 'python') output = await exportPython(model);
          else if (activeTab === 'ros2') output = await exportRos2(model);
          else if (activeTab === 'java') output = await exportJava(model);
          else if (activeTab === 'plc') output = await exportPlc(model);
          else if (activeTab === 'cpp') output = await exportCpp(model);
          else if (activeTab === 'mnc') output = await exportDsl(model);
          else if (activeTab === 'json') output = await exportJson(model);
          else if (activeTab === 'custom' && projectId) {
            const res = await generatorsApi.generateCode(projectId, { generator_id: selectedGeneratorId });
            const files = Object.entries(res.files);
            output = files.length > 0 ? files[0][1] : '// No custom generator output generated';
          }
        }
        setOutputContent(output);

        // 2. Fetch Generator Template
        if (projectId) {
          const targetGen = activeTab === 'plc' ? 'plc_st' : activeTab;
          try {
            const tpl = await generatorsApi.getTemplateSource(projectId, targetGen);
            setTemplateMeta(tpl);
            setTemplateContent(tpl.template_source || '');
            if (activeTab === 'custom' && !customScriptCode) {
              setCustomScriptCode(tpl.template_source || '');
            }
          } catch (e) {
            setTemplateMeta(null);
            setTemplateContent('// Template source unavailable');
          }
        }
      } catch (err: any) {
        console.error('Error fetching generated code/template:', err);
        setOutputContent(`// Error generating code: ${err.message || err}`);
      } finally {
        setIsLoading(false);
      }
    };

    loadData();
  }, [activeTab, model, projectId, selectedGeneratorId]);

  const handleCopy = () => {
    const textToCopy = viewMode === 'template' ? templateContent : outputContent;
    navigator.clipboard.writeText(textToCopy);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadFile = () => {
    const targetMap: Record<string, { ext: string; mime: string }> = {
      python: { ext: '_controller.py', mime: 'text/x-python' },
      ros2: { ext: '_ros2_node.py', mime: 'text/x-python' },
      java: { ext: 'SupervisorController.java', mime: 'text/x-java-source' },
      plc: { ext: '_plc.st', mime: 'text/plain' },
      cpp: { ext: '_controller.hpp', mime: 'text/x-c++hdr' },
      mnc: { ext: '.mncspec', mime: 'text/plain' },
      json: { ext: '.json', mime: 'application/json' },
      custom: { ext: '_custom.txt', mime: 'text/plain' }
    };
    const target = targetMap[activeTab] || { ext: '.txt', mime: 'text/plain' };
    const filename = `${modelName}${target.ext}`;
    const textToSave = viewMode === 'template' ? templateContent : outputContent;
    
    const blob = new Blob([textToSave], { type: target.mime });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = viewMode === 'template' ? `${activeTab}_generator_template.py` : filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  const handleDownloadZip = async () => {
    if (!model) return;
    setIsDownloading(true);
    try {
      await downloadZipBundle(model);
    } catch (err) {
      console.error('Failed to download ZIP bundle:', err);
    } finally {
      setIsDownloading(false);
    }
  };

  const handleRunCustomGenerator = async () => {
    if (!projectId) return;
    setIsExecutingGen(true);
    setGenStatus(null);
    try {
      const res = await generatorsApi.generateCode(projectId, { 
        generator_id: selectedGeneratorId, 
        save_to_project: true 
      });
      const entries = Object.entries(res.files);
      if (entries.length > 0) {
        setOutputContent(entries[0][1]);
        setGenStatus(`Generated ${entries.length} file(s): ${entries.map(e => e[0]).join(', ')}`);
        setViewMode('output');
      }
    } catch (err: any) {
      setGenStatus(`Execution error: ${err.message || err}`);
    } finally {
      setIsExecutingGen(false);
    }
  };

  const getLanguage = () => {
    if (viewMode === 'template') return templateMeta?.language || 'python';
    if (activeTab === 'python' || activeTab === 'ros2') return 'python';
    if (activeTab === 'java') return 'java';
    if (activeTab === 'plc') return 'text';
    if (activeTab === 'cpp') return 'cpp';
    if (activeTab === 'json') return 'json';
    return 'plaintext';
  };

  const primaryTargets: { id: TargetTab; label: string; tooltip: string; icon: React.FC<{ className?: string }> }[] = [
    { id: 'python', label: 'Python', tooltip: 'Python 3.11+ async supervisory event loop', icon: FileCode },
    { id: 'ros2', label: 'ROS2', tooltip: 'ROS2 (rclpy) lifecycle node & topic bindings', icon: Cpu },
    { id: 'java', label: 'Java', tooltip: 'Java controller & Eclipse Xtext specification', icon: Code },
    { id: 'plc', label: 'PLC', tooltip: 'PLC (IEC 61131-3) structured text FUNCTION_BLOCK', icon: Layers },
    { id: 'cpp', label: 'Embedded C++', tooltip: 'Embedded C++ FreeRTOS / Arduino supervisory class', icon: Cpu },
    { id: 'custom', label: 'Custom', tooltip: 'Custom project generator script (.generator.py)', icon: Wrench },
    { id: 'mnc', label: 'MNC Spec', tooltip: 'Formal MNC-ML Supervisory Specification', icon: FileCheck },
  ];

  if (!model) {
    return (
      <div className="flex-1 flex flex-col items-center justify-center p-8 bg-[#0b0f19] text-center h-full">
        <div className="p-4 rounded-2xl bg-blue-950/40 border border-blue-800/40 text-blue-400 mb-4 shadow-lg">
          <Sparkles className="w-10 h-10" />
        </div>
        <h3 className="text-lg font-bold text-gray-100 mb-2">No Synthesized Controller Yet</h3>
        <p className="text-xs text-gray-400 max-w-md mb-6 leading-relaxed">
          The KIDE synthesis engine requires a synthesized supervisory MNC-ML model to generate executable controllers for Python, ROS2, PLC Structured Text, Java, and Embedded C++.
        </p>
        <button
          onClick={() => setActiveView('statemachine')}
          className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded-lg text-xs font-semibold flex items-center gap-2 shadow-lg shadow-blue-600/30 transition"
        >
          <span>Go to State Machine / Synthesize</span>
          <ArrowRight className="w-3.5 h-3.5" />
        </button>
      </div>
    );
  }

  return (
    <div className="flex-1 flex flex-col min-w-0 min-h-0 h-full bg-[#0b0f19] text-gray-200 overflow-hidden select-none">
      
      {/* LEVEL 1: Focused Workspace Header */}
      <div className="h-11 min-h-[44px] bg-[#0d121d] border-b border-gray-800 px-4 flex items-center justify-between shrink-0 text-xs">
        <div className="flex items-center gap-2.5">
          <span className="font-semibold text-gray-200">Code Generation Studio</span>
          <span className="font-mono text-[11px] bg-blue-950/60 text-blue-300 px-2 py-0.5 rounded border border-blue-800/50">
            Supervisor: {modelName}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => setActiveView('simulator')}
            className="flex items-center gap-1.5 px-2.5 py-1 text-xs text-gray-300 hover:text-white bg-gray-800/80 hover:bg-gray-700 border border-gray-700 rounded-lg transition"
            title="Launch digital twin controller in live simulation runner"
          >
            <Play className="w-3.5 h-3.5 text-emerald-400" />
            <span>Launch Runner</span>
          </button>

          <button
            onClick={handleDownloadZip}
            disabled={isDownloading}
            className="flex items-center gap-1.5 px-2.5 py-1 text-xs text-gray-300 hover:text-white bg-gray-800/80 hover:bg-gray-700 border border-gray-700 rounded-lg transition"
            title="Download multi-target bundle ZIP with all target compilers"
          >
            {isDownloading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Download className="w-3.5 h-3.5 text-blue-400" />}
            <span>Export Suite (.ZIP)</span>
          </button>
        </div>
      </div>

      {/* LEVEL 2: Generator Target Selector (Horizontal pills with zero horizontal scrollbar) */}
      <div className="h-10 min-h-[40px] bg-[#111622] border-b border-gray-800 px-4 flex items-center justify-between shrink-0 text-xs gap-2">
        <div className="flex items-center gap-1 overflow-x-hidden flex-1 py-1">
          {primaryTargets.map((target) => {
            const Icon = target.icon;
            const isSelected = activeTab === target.id;

            return (
              <button
                key={target.id}
                onClick={() => setActiveTab(target.id)}
                title={target.tooltip}
                className={`flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-medium transition shrink-0 border ${
                  isSelected
                    ? 'bg-blue-600/20 text-blue-300 border-blue-500/50 font-semibold shadow-sm'
                    : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50 border-transparent'
                }`}
              >
                <Icon className={`w-3.5 h-3.5 ${isSelected ? 'text-blue-400' : 'text-gray-500'}`} />
                <span>{target.label}</span>
              </button>
            );
          })}

          {/* More ▾ Overflow Menu */}
          <div className="relative" ref={moreMenuRef}>
            <button
              onClick={() => setIsMoreOpen(prev => !prev)}
              aria-expanded={isMoreOpen}
              className={`flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs font-medium transition border ${
                activeTab === 'json' || isMoreOpen
                  ? 'bg-blue-600/20 text-blue-300 border-blue-500/50 font-semibold'
                  : 'text-gray-400 hover:text-gray-200 hover:bg-gray-800/50 border-transparent'
              }`}
              title="Additional targets & custom generator scripts"
            >
              <span>More</span>
              <ChevronDown className="w-3 h-3 text-gray-500" />
            </button>

            {isMoreOpen && (
              <div className="absolute left-0 mt-1 w-52 bg-[#1c2128] border border-gray-700/80 rounded-xl shadow-xl py-1 z-50 animate-in fade-in-50 zoom-in-95">
                <button
                  onClick={() => {
                    setActiveTab('json');
                    setIsMoreOpen(false);
                  }}
                  title="Full JSON Model AST"
                  className={`w-full text-left px-3 py-2 text-xs flex items-center gap-2 transition ${
                    activeTab === 'json' ? 'text-blue-300 bg-blue-600/15 font-semibold' : 'text-gray-200 hover:bg-gray-800'
                  }`}
                >
                  <FileCode className="w-3.5 h-3.5 text-blue-400" />
                  <span>JSON Model AST</span>
                </button>

                {generators.length > 0 && (
                  <>
                    <div className="px-3 py-1 text-[10px] font-semibold text-gray-500 uppercase tracking-wider border-t border-gray-700/60 mt-1 pt-1">
                      Project Custom Generators
                    </div>
                    {generators.map(g => (
                      <button
                        key={g.id}
                        onClick={() => {
                          setSelectedGeneratorId(g.id);
                          setActiveTab('custom');
                          setIsMoreOpen(false);
                        }}
                        className="w-full text-left px-3 py-1.5 text-xs text-gray-300 hover:bg-gray-800 flex items-center gap-2 transition"
                      >
                        <Wrench className="w-3 h-3 text-emerald-400" />
                        <span className="truncate">{g.name}</span>
                      </button>
                    ))}
                  </>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Generation Status Indicator if any */}
        {genStatus && (
          <span className="text-[11px] text-emerald-400 font-mono truncate hidden lg:inline">
            {genStatus}
          </span>
        )}
      </div>

      {/* LEVEL 3: Content Selector Bar (Generated Code vs. Template + Actions) */}
      <div className="h-10 min-h-[40px] bg-[#161b22] border-b border-gray-800 px-4 flex items-center justify-between shrink-0 text-xs">
        {/* Left: View Mode Segmented Pill */}
        <div className="flex items-center gap-2.5">
          <div className="flex bg-[#0d1117] p-0.5 rounded-lg border border-gray-700/60">
            <button
              onClick={() => setViewMode('output')}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-medium transition ${
                viewMode === 'output' 
                  ? 'bg-blue-600 text-white font-semibold shadow-sm' 
                  : 'text-gray-400 hover:text-white'
              }`}
            >
              <Eye className="w-3.5 h-3.5" />
              <span>Generated Code</span>
            </button>
            <button
              onClick={() => setViewMode('template')}
              className={`flex items-center gap-1.5 px-3 py-1 rounded-md text-xs font-medium transition ${
                viewMode === 'template' 
                  ? 'bg-amber-600 text-white font-semibold shadow-sm' 
                  : 'text-gray-400 hover:text-white'
              }`}
              title="Inspect underlying generator template implementation"
            >
              <BookOpen className="w-3.5 h-3.5" />
              <span>Template</span>
            </button>
          </div>

          {viewMode === 'template' && templateMeta?.specification_module && (
            <span className="hidden md:inline-flex text-[11px] text-amber-300 font-mono bg-amber-950/40 px-2.5 py-0.5 rounded-full border border-amber-800/50">
              Spec Ref: {templateMeta.specification_module}
            </span>
          )}
        </div>

        {/* Right: Actions */}
        <div className="flex items-center gap-2">
          {activeTab === 'custom' && (
            <button
              onClick={handleRunCustomGenerator}
              disabled={isExecutingGen}
              className="flex items-center gap-1.5 px-3 py-1 bg-emerald-600 hover:bg-emerald-500 text-white rounded-md text-xs font-semibold transition shadow-sm"
            >
              {isExecutingGen ? <Loader2 className="w-3 h-3 animate-spin" /> : <Play className="w-3 h-3" />}
              <span>Run Generator</span>
            </button>
          )}

          <button
            onClick={handleCopy}
            className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white rounded-md text-xs border border-gray-700 transition"
            title="Copy code to clipboard"
          >
            {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
            <span>{copied ? 'Copied' : 'Copy'}</span>
          </button>

          <button
            onClick={handleDownloadFile}
            className="flex items-center gap-1.5 px-2.5 py-1 bg-gray-800 hover:bg-gray-700 text-gray-300 hover:text-white rounded-md text-xs border border-gray-700 transition"
            title="Download this file"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Save File</span>
          </button>
        </div>
      </div>

      {/* LEVEL 4: Main Code Editor (Fills 100% remaining space) */}
      <div className="flex-1 min-h-0 w-full overflow-hidden relative">
        {isLoading ? (
          <div className="absolute inset-0 flex items-center justify-center bg-[#0b0f19] text-gray-400 gap-2 z-10">
            <Loader2 className="w-5 h-5 animate-spin text-blue-500" />
            <span className="text-xs">Generating {activeTab.toUpperCase()} controller...</span>
          </div>
        ) : null}

        <Editor
          height="100%"
          language={getLanguage()}
          value={viewMode === 'template' ? templateContent : outputContent}
          theme="vs-dark"
          options={{
            readOnly: viewMode !== 'template' || activeTab !== 'custom',
            minimap: { enabled: true },
            fontSize: 12.5,
            lineNumbers: 'on',
            scrollBeyondLastLine: false,
            wordWrap: 'on',
            automaticLayout: true,
            padding: { top: 12, bottom: 12 }
          }}
        />
      </div>
    </div>
  );
};
