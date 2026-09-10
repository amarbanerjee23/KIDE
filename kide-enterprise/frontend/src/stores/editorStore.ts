import { create } from 'zustand';
import { FileItem, TransformResult, ValidationError, ActivityDiagram } from '../types/models';

interface EditorState {
  files: FileItem[];
  activeFileId: string | null;
  selectedNodeId: string | null;
  transformResult: TransformResult | null;
  parsedModels: Map<string, any>;
  validationErrors: Record<string, ValidationError[]>;
  isTransforming: boolean;
  isParsing: boolean;
  
  setFiles: (files: FileItem[]) => void;
  addFile: (file: FileItem) => void;
  updateFileContent: (id: string, content: string) => void;
  removeFile: (id: string) => void;
  setActiveFileId: (id: string | null) => void;
  setSelectedNodeId: (id: string | null) => void;
  setTransformResult: (result: TransformResult | null) => void;
  setParsedModel: (fileId: string, ast: any) => void;
  setValidationErrors: (fileId: string, errors: ValidationError[]) => void;
  clearResults: () => void;
  setIsTransforming: (is: boolean) => void;
  setIsParsing: (is: boolean) => void;
}

export const useEditorStore = create<EditorState>((set) => ({
  files: [],
  activeFileId: null,
  selectedNodeId: null,
  transformResult: null,
  parsedModels: new Map(),
  validationErrors: {},
  isTransforming: false,
  isParsing: false,

  setFiles: (files) => set({ files }),
  addFile: (file) => set((state) => ({ files: [...state.files, file], activeFileId: file.id })),
  updateFileContent: (id, content) => set((state) => ({
    files: state.files.map(f => f.id === id ? { ...f, content } : f)
  })),
  removeFile: (id) => set((state) => ({
    files: state.files.filter(f => f.id !== id),
    activeFileId: state.activeFileId === id ? (state.files.find(f => f.id !== id)?.id || null) : state.activeFileId
  })),
  setActiveFileId: (id) => set({ activeFileId: id }),
  setSelectedNodeId: (id) => set({ selectedNodeId: id }),
  setTransformResult: (result) => set({ transformResult: result }),
  setParsedModel: (fileId, ast) => set((state) => {
    const newModels = new Map(state.parsedModels);
    newModels.set(fileId, ast);
    return { parsedModels: newModels };
  }),
  setValidationErrors: (fileId, errors) => set((state) => ({
    validationErrors: { ...state.validationErrors, [fileId]: errors }
  })),
  clearResults: () => set({ transformResult: null, parsedModels: new Map(), validationErrors: {} }),
  setIsTransforming: (is) => set({ isTransforming: is }),
  setIsParsing: (is) => set({ isParsing: is }),
}));

export const useActiveFile = () => {
  const { files, activeFileId } = useEditorStore();
  return files.find(f => f.id === activeFileId);
};

export const useActivityFile = () => {
  const { files, parsedModels } = useEditorStore();
  const activityFile = files.find(f => f.language === 'activitydsl');
  if (activityFile && parsedModels.has(activityFile.id)) {
    return parsedModels.get(activityFile.id) as ActivityDiagram;
  }
  return null;
};
