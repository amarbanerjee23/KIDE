import { useState } from 'react';
import { Sparkles, Send, X } from 'lucide-react';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
}

const GeminiAssistantPanel = ({ onClose }: { onClose: () => void }) => {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      role: 'assistant',
      content: "Hello! I am your Gemini AI assistant. I can help you write Knowledge DSLs, validate your capabilities, or generate Activity diagrams. What would you like to build?"
    }
  ]);

  const handleSend = () => {
    if (!input.trim()) return;
    
    setMessages([...messages, { id: Date.now().toString(), role: 'user', content: input }]);
    setInput('');
    
    // Mock response
    setTimeout(() => {
      setMessages(prev => [...prev, {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: "I've analyzed your project. To implement this, I suggest creating a new `.operation` file. Shall I generate the DSL for you?"
      }]);
    }, 1000);
  };

  return (
    <div className="h-full flex flex-col bg-surface border-l border-accent w-[300px] flex-shrink-0">
      <div className="flex items-center justify-between p-3 border-b border-accent bg-[#161b22]">
        <div className="flex items-center text-sm font-semibold text-gray-200">
          <Sparkles className="w-4 h-4 mr-2 text-blue-400" />
          Gemini Assistant
        </div>
        <button onClick={onClose} className="text-gray-400 hover:text-white">
          <X className="w-4 h-4" />
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {messages.map(msg => (
          <div key={msg.id} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[90%] rounded-lg p-3 text-sm ${msg.role === 'user' ? 'bg-blue-600 text-white' : 'bg-[#2a2d3e] text-gray-200'}`}>
              {msg.content}
            </div>
          </div>
        ))}
      </div>

      <div className="p-3 border-t border-accent bg-background">
        <div className="flex relative">
          <input 
            type="text" 
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            placeholder="Ask Gemini..."
            className="w-full bg-surface border border-accent rounded-l-md px-3 py-2 text-sm text-white focus:outline-none focus:border-blue-500"
          />
          <button 
            onClick={handleSend}
            className="bg-blue-600 hover:bg-blue-500 px-3 py-2 rounded-r-md flex items-center justify-center transition-colors"
          >
            <Send className="w-4 h-4 text-white" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default GeminiAssistantPanel;

