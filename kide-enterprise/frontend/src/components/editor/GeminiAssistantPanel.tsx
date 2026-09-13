import React, { useState, useEffect, useRef } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { 
  Sparkles, Send, X, Check, FileCode, CheckCircle2, 
  ChevronDown, ChevronRight, Loader2 
} from 'lucide-react';
import { 
  sendAiMessage, 
  applyAiPatch, 
  ChatMessage, 
  ToolCallRecord, 
  ProposedPatch, 
  getAiProviders 
} from '../../api/ai';

interface ExtendedMessage extends ChatMessage {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  tool_calls?: ToolCallRecord[];
  proposed_patches?: ProposedPatch[];
  provenance_id?: number;
  provider?: string;
  model?: string;
}

interface Props {
  onClose: () => void;
  triggerRef?: React.RefObject<HTMLButtonElement>;
}

const stageNames: Record<number, string> = {
  1: 'Data Modeling',
  2: 'Capabilities & Ops',
  3: 'Supervisory Workflow',
  4: 'Automated Synthesis'
};

export const GeminiAssistantPanel: React.FC<Props> = ({ onClose, triggerRef }) => {
  const activeFile = useActiveFile();
  const { 
    projectId, projectName, activeStage, 
    files, addFile, updateFileContent, setActiveFileId 
  } = useEditorStore();

  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [providerInfo, setProviderInfo] = useState<{ provider: string; model: string }>({
    provider: 'deterministic',
    model: 'kide-deterministic-copilot'
  });

  const [appliedPatches, setAppliedPatches] = useState<Record<string, boolean>>({});
  const [expandedDiffs, setExpandedDiffs] = useState<Record<string, boolean>>({});
  const [expandedTools, setExpandedTools] = useState<Record<string, boolean>>({});

  const [messages, setMessages] = useState<ExtendedMessage[]>([
    {
      id: 'welcome',
      role: 'assistant',
      content: `Hello! I am your **KIDE AI Engineering Copilot**.\n\nI am connected to project **"${projectName || 'Workspace'}"**${activeFile ? ` with active file \`${activeFile.name}\`` : ''}.\n\nI can validate your DSL models, synthesize supervisory automata, search the thesis Knowledge Hub, and propose deterministic patches with full provenance tracking.`
    }
  ]);

  const [isWide, setIsWide] = useState(() => 
    typeof window !== 'undefined' ? window.innerWidth >= 1440 : true
  );

  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Responsive width detection
  useEffect(() => {
    const handleResize = () => {
      setIsWide(window.innerWidth >= 1440);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Fetch configured providers on mount
  useEffect(() => {
    getAiProviders()
      .then(res => {
        setProviderInfo({
          provider: res.active_provider,
          model: res.active_model
        });
      })
      .catch(() => {
        // Default to deterministic copilot
      });
  }, []);

  // Keyboard accessibility
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        e.preventDefault();
        onClose();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    
    // Focus input on open
    setTimeout(() => inputRef.current?.focus(), 50);

    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      triggerRef?.current?.focus();
    };
  }, [onClose, triggerRef]);

  // Scroll to bottom on new message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages, isLoading]);

  const handleSend = async (textToSend?: string) => {
    const text = (textToSend || input).trim();
    if (!text || isLoading || !projectId) return;

    const userMsg: ExtendedMessage = {
      id: Date.now().toString(),
      role: 'user',
      content: text
    };

    const newMessages = [...messages, userMsg];
    setMessages(newMessages);
    if (!textToSend) setInput('');
    setIsLoading(true);

    try {
      const chatPayload: ChatMessage[] = newMessages.map(m => ({
        role: m.role,
        content: m.content
      }));

      const res = await sendAiMessage(
        projectId,
        chatPayload,
        activeFile?.name
      );

      const assistantMsg: ExtendedMessage = {
        id: res.session_id || (Date.now() + 1).toString(),
        role: 'assistant',
        content: res.message,
        tool_calls: res.tool_calls,
        proposed_patches: res.proposed_patches,
        provenance_id: res.provenance_id,
        provider: res.provider,
        model: res.model
      };

      setMessages(prev => [...prev, assistantMsg]);
    } catch (err: any) {
      setMessages(prev => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          role: 'assistant',
          content: `⚠️ **AI Engineering Gateway Error**: ${err.message || 'Failed to communicate with AI server. Ensure backend is running.'}`
        }
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleApplyPatch = async (patch: ProposedPatch, provenanceId?: number) => {
    if (!projectId) return;
    const patchKey = `${patch.filename}_${provenanceId || 0}`;

    try {
      await applyAiPatch(projectId, provenanceId, [patch]);

      // Apply locally to editor store
      const existingFile = files.find(f => f.name.toLowerCase() === patch.filename.toLowerCase());
      if (existingFile) {
        updateFileContent(existingFile.id, patch.new_content);
        setActiveFileId(existingFile.id);
      } else {
        const ext = patch.filename.split('.').pop()?.toLowerCase() || '';
        const langMap: Record<string, string> = {
          dml: 'dml',
          cap: 'capability',
          op: 'operation',
          activity: 'activity',
          mnc: 'mnc'
        };
        const newId = `file_${Date.now()}`;
        addFile({
          id: newId,
          name: patch.filename,
          content: patch.new_content,
          language: langMap[ext] || 'text'
        });
        setActiveFileId(newId);
      }

      setAppliedPatches(prev => ({ ...prev, [patchKey]: true }));
    } catch (err: any) {
      alert(`Failed to apply patch: ${err.message}`);
    }
  };

  const toggleDiff = (key: string) => {
    setExpandedDiffs(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const toggleTool = (key: string) => {
    setExpandedTools(prev => ({ ...prev, [key]: !prev[key] }));
  };

  const promptChips = [
    "Validate all models",
    "Synthesize state machine",
    "Analyze impact of removing ChillerCooling",
    "Reconfigure workflow to use SmartChillerV2",
    "Verify state transitions",
    "Explain active DSL",
    "Suggest activity steps",
    "Search knowledge for barrier RFID",
    "Create a safety gate barrier activity patch"
  ];

  return (
    <>
      {/* Overlay Backdrop for small screens */}
      {!isWide && (
        <div 
          className="fixed inset-0 bg-black/40 backdrop-blur-[1px] z-40 animate-in fade-in-50"
          onClick={onClose}
        />
      )}

      {/* Assistant Drawer Panel */}
      <aside
        ref={containerRef}
        role="dialog"
        aria-label="KIDE AI Engineering Copilot"
        aria-modal={!isWide}
        className={`bg-[#121722] border-l border-gray-800 flex flex-col justify-between shrink-0 select-none z-50 transition-all duration-200 shadow-2xl ${
          isWide 
            ? 'relative w-[420px] h-full' 
            : 'fixed top-0 right-0 bottom-0 w-[420px] max-w-[92vw] h-screen animate-in slide-in-from-right'
        }`}
      >
        {/* Top Header */}
        <div className="p-3 border-b border-gray-800/80 bg-[#161b26] flex flex-col gap-1.5 shrink-0">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2 text-xs font-semibold text-gray-100">
              <div className="p-1 rounded bg-blue-950/60 border border-blue-800/40 text-blue-400">
                <Sparkles className="w-3.5 h-3.5" />
              </div>
              <span>AI Engineering Copilot</span>
              <span className="text-[10px] font-mono text-blue-300 bg-blue-900/30 px-1.5 py-0.5 rounded border border-blue-700/40 uppercase">
                {providerInfo.provider}
              </span>
            </div>

            <button 
              onClick={onClose} 
              aria-label="Close AI Assistant"
              title="Close Assistant (Escape)"
              className="p-1 text-gray-400 hover:text-white hover:bg-gray-800 rounded transition"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Context Metadata Pill */}
          <div className="flex items-center gap-1.5 text-[10px] text-gray-400 truncate">
            <span className="truncate text-gray-300 font-medium" title={projectName || 'Project'}>
              {projectName || 'Project'}
            </span>
            <span>•</span>
            <span className="text-blue-300">
              {stageNames[activeStage] || 'Stage 1'}
            </span>
            {activeFile && (
              <>
                <span>•</span>
                <span className="font-mono text-gray-300 truncate" title={activeFile.name}>
                  {activeFile.name}
                </span>
              </>
            )}
          </div>
        </div>

        {/* Messages Body */}
        <div className="flex-1 overflow-y-auto p-3 space-y-3.5 scrollbar-thin scrollbar-thumb-gray-800">
          {messages.map(msg => (
            <div 
              key={msg.id} 
              className={`flex flex-col ${msg.role === 'user' ? 'items-end' : 'items-start'} space-y-2`}
            >
              {/* Message Bubble */}
              <div className={`max-w-[94%] rounded-xl p-3 text-xs leading-relaxed ${
                msg.role === 'user' 
                  ? 'bg-blue-600 text-white rounded-br-none shadow-sm' 
                  : 'bg-[#1a2130] text-gray-200 border border-gray-700/60 rounded-bl-none shadow-sm space-y-2'
              }`}>
                {/* Text Content */}
                <div className="whitespace-pre-wrap font-sans text-xs">
                  {msg.content}
                </div>

                {/* Executed Engineering Tools */}
                {msg.tool_calls && msg.tool_calls.length > 0 && (
                  <div className="pt-2 border-t border-gray-700/50 space-y-1.5">
                    <div className="text-[10px] font-semibold text-gray-400 uppercase tracking-wider flex items-center gap-1">
                      <span>Engineering Tools Executed ({msg.tool_calls.length})</span>
                    </div>
                    <div className="space-y-1">
                      {msg.tool_calls.map((t, idx) => {
                        const toolKey = `${msg.id}_tool_${idx}`;
                        const isExp = expandedTools[toolKey];
                        return (
                          <div key={idx} className="rounded bg-[#111622] border border-gray-800 text-[11px] overflow-hidden">
                            <button
                              onClick={() => toggleTool(toolKey)}
                              className="w-full flex items-center justify-between px-2 py-1 hover:bg-gray-800/40 text-left transition"
                            >
                              <div className="flex items-center gap-1.5 font-mono text-blue-300 truncate">
                                <span className="text-[10px]">⚙</span>
                                <span>{t.tool}</span>
                              </div>
                              <div className="flex items-center gap-1 text-[10px] text-gray-500">
                                <span>{isExp ? <ChevronDown className="w-3 h-3" /> : <ChevronRight className="w-3 h-3" />}</span>
                              </div>
                            </button>
                            {isExp && (
                              <div className="p-2 border-t border-gray-800 bg-[#0d1017] font-mono text-[10px] text-gray-400 max-h-32 overflow-y-auto">
                                <pre className="whitespace-pre-wrap">{JSON.stringify(t.output, null, 2)}</pre>
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  </div>
                )}

                {/* Proposed Patches Card */}
                {msg.proposed_patches && msg.proposed_patches.length > 0 && (
                  <div className="pt-2 border-t border-gray-700/50 space-y-2">
                    <div className="text-[10px] font-semibold text-amber-400 uppercase tracking-wider flex items-center gap-1">
                      <span>Proposed Engineering Patches ({msg.proposed_patches.length})</span>
                    </div>

                    {msg.proposed_patches.map((patch, idx) => {
                      const patchKey = `${patch.filename}_${msg.provenance_id || idx}`;
                      const isApplied = appliedPatches[patchKey];
                      const isDiffOpen = expandedDiffs[patchKey] ?? true;

                      return (
                        <div 
                          key={idx} 
                          className="rounded-lg bg-[#141a26] border border-amber-900/40 overflow-hidden shadow-sm"
                        >
                          {/* Patch Header */}
                          <div className="p-2.5 bg-[#171f2e] border-b border-gray-800 flex items-center justify-between">
                            <div className="flex items-center gap-1.5 truncate">
                              <FileCode className="w-3.5 h-3.5 text-amber-400 shrink-0" />
                              <span className="font-mono font-medium text-gray-200 text-xs truncate">
                                {patch.filename}
                              </span>
                              <span className="text-[9px] px-1 py-0.2 rounded bg-amber-950/60 border border-amber-800/40 text-amber-300 uppercase">
                                {patch.action}
                              </span>
                            </div>

                            <button
                              onClick={() => toggleDiff(patchKey)}
                              className="text-[10px] text-gray-400 hover:text-gray-200 flex items-center gap-0.5"
                            >
                              <span>{isDiffOpen ? 'Hide Diff' : 'View Diff'}</span>
                              {isDiffOpen ? <ChevronDown className="w-3 h-3" /> : <ChevronRight className="w-3 h-3" />}
                            </button>
                          </div>

                          {/* Rationale */}
                          {patch.rationale && (
                            <div className="px-2.5 py-1.5 text-[11px] text-gray-300 bg-[#121620]/60 border-b border-gray-800/60">
                              {patch.rationale}
                            </div>
                          )}

                          {/* Collapsible Diff View */}
                          {isDiffOpen && (
                            <div className="p-2 bg-[#0a0d14] font-mono text-[10px] leading-tight max-h-48 overflow-y-auto border-b border-gray-800">
                              {patch.diff.split('\n').map((line, lIdx) => {
                                let lineClass = 'text-gray-400';
                                if (line.startsWith('+') && !line.startsWith('+++')) lineClass = 'text-emerald-400 bg-emerald-950/30';
                                else if (line.startsWith('-') && !line.startsWith('---')) lineClass = 'text-rose-400 bg-rose-950/30';
                                else if (line.startsWith('@')) lineClass = 'text-blue-400';

                                return (
                                  <div key={lIdx} className={`px-1 py-0.5 ${lineClass} whitespace-pre-wrap break-all`}>
                                    {line || ' '}
                                  </div>
                                );
                              })}
                            </div>
                          )}

                          {/* Review & Apply Action */}
                          <div className="p-2 bg-[#171f2e] flex items-center justify-between">
                            <span className="text-[10px] text-gray-400">
                              {isApplied ? 'Changes committed to project' : 'Deterministic patch verified'}
                            </span>

                            {isApplied ? (
                              <div className="flex items-center gap-1 text-[11px] font-medium text-emerald-400 bg-emerald-950/40 border border-emerald-800/50 px-2.5 py-1 rounded">
                                <CheckCircle2 className="w-3 h-3" />
                                <span>Applied to Project</span>
                              </div>
                            ) : (
                              <button
                                onClick={() => handleApplyPatch(patch, msg.provenance_id)}
                                className="flex items-center gap-1.5 text-[11px] font-medium text-white bg-emerald-600 hover:bg-emerald-500 active:bg-emerald-700 px-3 py-1 rounded shadow transition"
                              >
                                <Check className="w-3 h-3" />
                                <span>Review & Apply Patch</span>
                              </button>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                )}

                {/* Provenance Badge */}
                {msg.provenance_id && (
                  <div className="pt-1 text-[9px] font-mono text-gray-500 flex items-center justify-between">
                    <span>Recorded in Provenance Log #{msg.provenance_id}</span>
                    <span>{msg.provider} • {msg.model}</span>
                  </div>
                )}
              </div>
            </div>
          ))}

          {/* Loading Indicator */}
          {isLoading && (
            <div className="flex justify-start">
              <div className="bg-[#1a2130] text-gray-300 border border-gray-700/60 rounded-xl rounded-bl-none p-3 text-xs flex items-center gap-2">
                <Loader2 className="w-3.5 h-3.5 text-blue-400 animate-spin" />
                <span>Executing engineering tools & analyzing models...</span>
              </div>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        {/* Quick Suggestion Chips & Input */}
        <div className="p-3 border-t border-gray-800/80 bg-[#161b26] shrink-0 space-y-2">
          {/* Quick Prompt Chips */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
            {promptChips.map((chip, idx) => (
              <button
                key={idx}
                disabled={isLoading}
                onClick={() => handleSend(chip)}
                className="text-[10px] px-2.5 py-1 rounded-full bg-[#1e2638] hover:bg-[#253046] active:bg-[#2e3b54] text-gray-300 hover:text-white border border-gray-700/60 whitespace-nowrap transition disabled:opacity-40"
              >
                {chip}
              </button>
            ))}
          </div>

          {/* Input Box */}
          <div className="flex items-center bg-[#0d1117] border border-gray-700/80 rounded-lg overflow-hidden focus-within:border-blue-500 transition">
            <input 
              ref={inputRef}
              type="text" 
              value={input}
              disabled={isLoading}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSend()}
              placeholder="Ask Copilot to validate, synthesize, or patch..."
              aria-label="Message input for AI Engineering Copilot"
              className="flex-1 bg-transparent px-3 py-2 text-xs text-white placeholder-gray-500 focus:outline-none disabled:opacity-40"
            />
            <button 
              onClick={() => handleSend()}
              aria-label="Send message"
              disabled={!input.trim() || isLoading}
              className="p-2 text-blue-400 hover:text-white hover:bg-blue-600 disabled:opacity-30 transition-colors shrink-0"
            >
              {isLoading ? <Loader2 className="w-3.5 h-3.5 animate-spin" /> : <Send className="w-3.5 h-3.5" />}
            </button>
          </div>
        </div>
      </aside>
    </>
  );
};

export default GeminiAssistantPanel;
