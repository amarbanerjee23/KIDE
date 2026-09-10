import { useState, useEffect } from 'react';
import { useEditorStore } from '../../stores/editorStore';
import Button from '../common/Button';
import Input from '../common/Input';
import { X } from 'lucide-react';

const NodePropertyForm = () => {
  const { files, selectedNodeId, setSelectedNodeId, updateFileContent } = useEditorStore();
  const activeFile = files.find(f => f.name.endsWith('.activity') || f.language === 'activitydsl');
  const [formData, setFormData] = useState<any>(null);

  useEffect(() => {
    if (!selectedNodeId || !activeFile) return;
    try {
      const data = JSON.parse(activeFile.content);
      const activity = data.activities?.find((a: any) => a.name === selectedNodeId);
      if (activity) {
        setFormData(activity);
      }
    } catch (e) {
      // invalid json
    }
  }, [activeFile, selectedNodeId]);

  const handleChange = (field: string, value: any) => {
    setFormData((prev: any) => ({ ...prev, [field]: value }));
  };

  const handleArrayChange = (field: string, value: string) => {
    const arr = value.split(',').map(s => s.trim()).filter(s => s.length > 0);
    setFormData((prev: any) => ({ ...prev, [field]: arr }));
  };

  const handleSave = () => {
    if (!activeFile) return;
    try {
      const data = JSON.parse(activeFile.content);
      const index = data.activities?.findIndex((a: any) => a.name === selectedNodeId);
      if (index !== -1 && index !== undefined) {
        data.activities[index] = formData;
        updateFileContent(activeFile.id, JSON.stringify(data, null, 2));
      }
    } catch (e) {
      alert("Cannot save: Invalid JSON in editor");
    }
  };

  if (!formData) return null;

  return (
    <div className="flex flex-col h-full bg-surface">
      <div className="flex items-center justify-between p-4 border-b border-accent">
        <h3 className="font-semibold text-white">Properties: {formData.name}</h3>
        <button onClick={() => setSelectedNodeId(null)} className="text-gray-400 hover:text-white">
          <X className="w-5 h-5" />
        </button>
      </div>

      <div className="p-4 space-y-4 flex-1 overflow-y-auto">
        <Input 
          label="Name" 
          value={formData.name || ''} 
          onChange={(e) => handleChange('name', e.target.value)}
          disabled
        />
        <div className="space-y-1">
          <label className="block text-sm font-medium text-gray-300">Requires Operation</label>
          <input 
            type="checkbox" 
            checked={!!formData.requiresOperation} 
            onChange={(e) => handleChange('requiresOperation', e.target.checked)}
            className="w-4 h-4 rounded bg-accent border-gray-600 text-highlight focus:ring-highlight focus:ring-offset-surface"
          />
        </div>
        <Input 
          label="Commands (comma separated)" 
          value={(formData.commands || []).join(', ')} 
          onChange={(e) => handleArrayChange('commands', e.target.value)}
        />
        <Input 
          label="Events (comma separated)" 
          value={(formData.events || []).join(', ')} 
          onChange={(e) => handleArrayChange('events', e.target.value)}
        />

        <div className="pt-4 border-t border-accent">
          <Button onClick={handleSave} className="w-full">Apply Changes</Button>
        </div>
      </div>
    </div>
  );
};

export default NodePropertyForm;

