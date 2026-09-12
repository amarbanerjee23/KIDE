import React from 'react';
import { CodeGenerationStudio } from './CodeGenerationStudio';

interface CodeGenerationModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const CodeGenerationModal: React.FC<CodeGenerationModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4 animate-in fade-in duration-200">
      <div className="w-full max-w-6xl h-[90vh]">
        <CodeGenerationStudio onClose={onClose} isEmbedded={false} />
      </div>
    </div>
  );
};
