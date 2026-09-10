import React from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { AlertCircle, AlertTriangle, Info } from 'lucide-react';

export const ValidationPanel: React.FC = () => {
  const { validationErrors, files } = useEditorStore();
  
  const entries = Object.entries(validationErrors);
  if (entries.length === 0) {
    return <div className="text-gray-400">No validation issues found.</div>;
  }

  const getIcon = (severity: string) => {
    switch (severity) {
      case 'error': return <AlertCircle className="text-red-500 w-4 h-4" />;
      case 'warning': return <AlertTriangle className="text-yellow-500 w-4 h-4" />;
      default: return <Info className="text-blue-500 w-4 h-4" />;
    }
  };

  return (
    <div className="space-y-4">
      {entries.map(([fileId, errors]) => {
        const file = files.find(f => f.id === fileId);
        if (!errors || errors.length === 0) return null;
        
        return (
          <div key={fileId} className="bg-surface rounded border border-accent p-2">
            <div className="text-sm font-semibold text-gray-200 border-b border-accent pb-1 mb-2">
              {file?.name || 'Unknown File'}
            </div>
            <ul className="space-y-2">
              {errors.map((err, i) => (
                <li key={i} className="flex items-start gap-2 text-sm text-gray-300 bg-background p-2 rounded">
                  {getIcon(err.severity)}
                  <div>
                    <span className="font-mono text-gray-500 text-xs mr-2">
                      [{err.line || '?'}:{err.column || '?'}]
                    </span>
                    {err.message}
                  </div>
                </li>
              ))}
            </ul>
          </div>
        );
      })}
    </div>
  );
};
