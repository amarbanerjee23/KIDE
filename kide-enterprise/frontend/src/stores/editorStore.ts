import { create } from 'zustand';
import { TransformResult, ValidationError } from '../types/models';

export interface FileItem {
  id: string;
  name: string;
  content: string;
  language: string; // 'activitydsl', 'capabilitydsl', 'operationdsl', 'dmldsl', 'json'
}

interface EditorState {
  files: FileItem[];
  activeFileId: string | null;
  transformResult: TransformResult | null;
  isTransforming: boolean;
  validationErrors: ValidationError[];
  selectedNodeId: string | null;
  
  setFiles: (files: FileItem[]) => void;
  addFile: (file: FileItem) => void;
  updateFileContent: (id: string, content: string) => void;
  setActiveFileId: (id: string | null) => void;
  removeFile: (id: string) => void;
  
  setTransformResult: (result: TransformResult | null) => void;
  setValidationErrors: (errors: ValidationError[]) => void;
  setIsTransforming: (val: boolean) => void;
  setSelectedNodeId: (id: string | null) => void;
  clearResults: () => void;
}

export const useEditorStore = create<EditorState>((set) => ({
  files: [
    {
      id: 'default-activity',
      name: 'Main.activity',
      language: 'activitydsl',
      content: '{\n  "name": "New Activity",\n  "defaultOperatingStates": [],\n  "activities": []\n}'
    }
  ],
  activeFileId: 'default-activity',
  transformResult: null,
  isTransforming: false,
  validationErrors: [],
  selectedNodeId: null,
  
  setFiles: (files) => set({ files }),
  addFile: (file) => set((state) => ({ files: [...state.files, file], activeFileId: file.id })),
  updateFileContent: (id, content) => set((state) => ({
    files: state.files.map(f => f.id === id ? { ...f, content } : f)
  })),
  setActiveFileId: (id) => set({ activeFileId: id }),
  removeFile: (id) => set((state) => ({
    files: state.files.filter(f => f.id !== id),
    activeFileId: state.activeFileId === id ? (state.files[0]?.id || null) : state.activeFileId
  })),
  
  setTransformResult: (transformResult) => set({ transformResult }),
  setValidationErrors: (validationErrors) => set({ validationErrors }),
  setIsTransforming: (isTransforming) => set({ isTransforming }),
  setSelectedNodeId: (selectedNodeId) => set({ selectedNodeId }),
  clearResults: () => set({ transformResult: null, validationErrors: [] })
}));
