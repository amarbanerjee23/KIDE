import React from 'react';
import { AlertCircle, AlertTriangle, CheckCircle } from 'lucide-react';
import { useEditorStore } from '../../stores/editorStore';

const ValidationPanel = () => {
  const { validationErrors, transformResult } = useEditorStore();
  const warnings = transformResult?.warnings || [];

  if (validationErrors.length === 0 && warnings.length === 0 && transformResult) {
    return (
      <div className="p-6 flex flex-col items-center justify-center h-full text-gray-400">
        <CheckCircle className="w-12 h-12 text-green-500 mb-4" />
        <p className="text-lg">No validation errors or warnings found.</p>
        <p className="text-sm mt-2">The model is perfectly valid.</p>
      </div>
    );
  }

  if (!transformResult && validationErrors.length === 0) {
     return <div className="p-6 text-gray-500">Run Transform to see output</div>;
  }

  return (
    <div className="p-4 h-full overflow-y-auto space-y-4">
      {validationErrors.map((err, i) => (
        <div key={`err-${i}`} className="bg-red-500/10 border border-red-500/30 rounded-md p-3 flex items-start">
          <AlertCircle className="w-5 h-5 text-red-500 mr-3 shrink-0 mt-0.5" />
          <div>
            <h4 className="text-sm font-medium text-red-200">Error</h4>
            <p className="text-sm text-red-400 mt-1">{err.message}</p>
          </div>
        </div>
      ))}
      
      {warnings.map((warn, i) => (
        <div key={`warn-${i}`} className="bg-yellow-500/10 border border-yellow-500/30 rounded-md p-3 flex items-start">
          <AlertTriangle className="w-5 h-5 text-yellow-500 mr-3 shrink-0 mt-0.5" />
          <div>
            <h4 className="text-sm font-medium text-yellow-200">Warning</h4>
            <p className="text-sm text-yellow-400 mt-1">{warn}</p>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ValidationPanel;

