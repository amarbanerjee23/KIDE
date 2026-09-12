import React, { useState, useEffect, useRef } from 'react';
import { useEditorStore, useActiveFile } from '../../stores/editorStore';
import { Sparkles, Send, X } from 'lucide-react';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
}

interface Props {
  onClose: () => void;
  triggerRef?: React.RefObject<HTMLButtonElement>;
}

export const GeminiAssistantPanel: React.FC<Props> = ({ onClose, triggerRef }) => {
  const activeFile = useActiveFile();
  const { projectName, activeStage } = useEditorStore();

  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: `Hello! I am your KIDE AI engineering assistant. I am analyzing ${
        projectName ? `project "${projectName}"` : 'your workspace'
      }${activeFile ? ` and active file "${activeFile.name}"` : ''}. How can I assist with your DSL schemas, capability matching, or supervisory synthesis?`
    }
  ]);

  const [isWide, setIsWide] = useState(() => 
    typeof window !== 'undefined' ? window.innerWidth >= 1440 : true
  );

  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // Responsive width detection
  useEffect(() => {
    const handleResize = () => {
      setIsWide(window.innerWidth >= 1440);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Keyboard accessibility: Escape closes drawer, focus enters input on mount, returns to trigger on unmount
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
      // Return focus to trigger button if available
      triggerRef?.current?.focus();
    };
  }, [onClose, triggerRef]);

  const handleSend = (textToSend?: string) => {
    const msg = textToSend || input;
    if (!msg.trim()) return;
    
    const userMsg: Message = { id: Date.now().toString(), role: 'user', content: msg };
    setMessages(prev => [...prev, userMsg]);
    if (!textToSend) setInput('');
    
    // Simulated intelligent response with DSL domain knowledge
    setTimeout(() => {
      let reply = "I've analyzed your project.";
      if (msg.toLowerCase().includes('dml') || msg.toLowerCase().includes('data')) {
        reply = "In DML, primitive types (`int`, `float`, `boolean`, `string`, `date`) and composite structs (`composites { ... }`) define your device data models. Ensure composite types reference valid defined models.";
      } else if (msg.toLowerCase().includes('capability') || msg.toLowerCase().includes('cap')) {
        reply = "Capabilities bind component interfaces to fireable commands and receivable events. Check that your action parameter signatures match the interface definition.";
      } else if (msg.toLowerCase().includes('activity') || msg.toLowerCase().includes('workflow')) {
        reply = "Activity diagrams orchestrate supervisory process logic. Each activity requires capabilities or operations and routes outcomes via conditional branch expressions.";
      } else if (msg.toLowerCase().includes('synthesis') || msg.toLowerCase().includes('state')) {
        reply = "The thesis automated synthesis engine scans activity diagrams and required capabilities to derive full supervisory automata (operating states, transitions, command/event blocks).";
      } else {
        reply = `I have inspected "${activeFile?.name || 'the workspace'}". Everything looks consistent. Would you like me to suggest code generation targets or verify state machine transitions?`;
      }

      setMessages(prev => [...prev, {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: reply
      }]);
    }, 700);
  };

  const stageNames: Record<number, string> = {
    1: '1: Data Modeling',
    2: '2: Capabilities & Ops',
    3: '3: Supervisory Workflow',
    4: '4: Automated Synthesis'
  };

  const promptChips = [
    "Explain active DSL",
    "Verify state transitions",
    "Suggest activity steps"
  ];

  return (
    <>
      {/* Overlay Backdrop for constrained screens (< 1440px) */}
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
        aria-label="Gemini AI Engineering Assistant"
        aria-modal={!isWide}
        className={`bg-[#121722] border-l border-gray-800 flex flex-col justify-between shrink-0 select-none z-50 transition-all duration-200 shadow-2xl ${
          isWide 
            ? 'relative w-[380px] h-full' 
            : 'fixed top-0 right-0 bottom-0 w-[380px] max-w-[90vw] h-screen animate-in slide-in-from-right'
        }`}
      >
        {/* Top Header */}
        <div className="p-3 border-b border-gray-800/80 bg-[#161b26] flex flex-col gap-1.5 shrink-0">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2 text-xs font-semibold text-gray-100">
              <div className="p-1 rounded bg-blue-950/60 border border-blue-800/40 text-blue-400">
                <Sparkles className="w-3.5 h-3.5" />
              </div>
              <span>AI Assistant</span>
              <span className="text-[10px] font-mono text-blue-300 bg-blue-900/30 px-1.5 py-0.2 rounded border border-blue-700/40">
                Gemini
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
            <span className="truncate" title={projectName || 'Project'}>
              {projectName || 'Project'}
            </span>
            <span>•</span>
            <span className="text-blue-300 font-medium">
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
        <div className="flex-1 overflow-y-auto p-3.5 space-y-3 scrollbar-thin scrollbar-thumb-gray-800">
          {messages.map(msg => (
            <div 
              key={msg.id} 
              className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
            >
              <div className={`max-w-[88%] rounded-xl p-2.5 text-xs leading-relaxed ${
                msg.role === 'user' 
                  ? 'bg-blue-600 text-white rounded-br-none shadow-sm' 
                  : 'bg-[#1a2130] text-gray-200 border border-gray-700/60 rounded-bl-none shadow-sm'
              }`}>
                {msg.content}
              </div>
            </div>
          ))}
        </div>

        {/* Quick Suggestion Chips & Input */}
        <div className="p-3 border-t border-gray-800/80 bg-[#161b26] shrink-0 space-y-2">
          {/* Quick Prompt Chips */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 scrollbar-none">
            {promptChips.map((chip, idx) => (
              <button
                key={idx}
                onClick={() => handleSend(chip)}
                className="text-[10px] px-2 py-1 rounded-full bg-[#1e2638] hover:bg-[#253046] text-gray-300 hover:text-white border border-gray-700/60 whitespace-nowrap transition"
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
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && handleSend()}
              placeholder="Ask about DSLs, synthesis, transitions..."
              aria-label="Message input for AI Assistant"
              className="flex-1 bg-transparent px-3 py-2 text-xs text-white placeholder-gray-500 focus:outline-none"
            />
            <button 
              onClick={() => handleSend()}
              aria-label="Send message"
              disabled={!input.trim()}
              className="p-2 text-blue-400 hover:text-white hover:bg-blue-600 disabled:opacity-30 transition-colors shrink-0"
            >
              <Send className="w-3.5 h-3.5" />
            </button>
          </div>
        </div>
      </aside>
    </>
  );
};

export default GeminiAssistantPanel;
