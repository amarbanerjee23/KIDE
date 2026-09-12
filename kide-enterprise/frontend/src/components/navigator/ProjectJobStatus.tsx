import React from 'react';
import { useEditorStore } from '../../stores/editorStore';
import { Loader2 } from 'lucide-react';

export const ProjectJobStatus: React.FC = () => {
  const { isTransforming, isParsing, isSaving } = useEditorStore();

  if (!isTransforming && !isParsing && !isSaving) {
    return null;
  }

  let label = 'Processing project job...';
  if (isTransforming) {
    label = 'Synthesizing supervisor automata...';
  } else if (isParsing) {
    label = 'Validating DSL syntax & models...';
  } else if (isSaving) {
    label = 'Saving project files...';
  }

  return (
    <div className="px-3 py-2 border-t border-gray-800/80 bg-blue-950/20 text-blue-300 text-xs flex items-center gap-2">
      <Loader2 className="w-3.5 h-3.5 animate-spin text-blue-400 shrink-0" />
      <span className="truncate font-medium">{label}</span>
    </div>
  );
};

