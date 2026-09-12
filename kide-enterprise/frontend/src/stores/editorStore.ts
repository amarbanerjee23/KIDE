import { create } from 'zustand';
import { FileItem, TransformResult, ValidationError, ActivityDiagram } from '../types/models';

interface EditorState {
  projectId: number | null;
  projectName: string | null;
  files: FileItem[];
  activeFileId: string | null;
  selectedNodeId: string | null;
  transformResult: TransformResult | null;
  parsedModels: Map<string, any>;
  validationErrors: Record<string, ValidationError[]>;
  dirtyFileIds: string[];
  isTransforming: boolean;
  isParsing: boolean;
  isSaving: boolean;
  activeView: 'editor' | 'statemachine' | 'workflow' | 'simulator' | 'codegen' | 'split';
  
  setActiveView: (view: 'editor' | 'statemachine' | 'workflow' | 'simulator' | 'codegen' | 'split') => void;
  setProject: (id: number | null, name: string | null) => void;
  setFiles: (files: FileItem[]) => void;
  addFile: (file: FileItem) => void;
  updateFileContent: (id: string, content: string) => void;
  markFileSaved: (id: string) => void;
  removeFile: (id: string) => void;
  setActiveFileId: (id: string | null) => void;
  setSelectedNodeId: (id: string | null) => void;
  setTransformResult: (result: TransformResult | null) => void;
  setParsedModel: (fileId: string, ast: any) => void;
  setValidationErrors: (fileId: string, errors: ValidationError[]) => void;
  clearResults: () => void;
  setIsTransforming: (is: boolean) => void;
  setIsParsing: (is: boolean) => void;
  setIsSaving: (is: boolean) => void;
}

export const useEditorStore = create<EditorState>((set) => ({
  projectId: null,
  projectName: null,
  files: [],
  activeFileId: null,
  selectedNodeId: null,
  transformResult: null,
  parsedModels: new Map(),
  validationErrors: {},
  dirtyFileIds: [],
  isTransforming: false,
  isParsing: false,
  isSaving: false,
  activeView: 'editor',

  setActiveView: (view) => set({ activeView: view }),
  setProject: (id, name) => set({ projectId: id, projectName: name }),
  setFiles: (files) => set({ 
    files, 
    dirtyFileIds: [],
    activeFileId: files.length > 0 ? (files.find(f => f.name.endsWith('.activity') || f.name.endsWith('.json'))?.id || files[0].id) : null 
  }),
  addFile: (file) => set((state) => ({ files: [...state.files, file], activeFileId: file.id })),
  updateFileContent: (id, content) => set((state) => ({
    files: state.files.map(f => f.id === id ? { ...f, content } : f),
    dirtyFileIds: state.dirtyFileIds.includes(id) ? state.dirtyFileIds : [...state.dirtyFileIds, id]
  })),
  markFileSaved: (id) => set((state) => ({
    dirtyFileIds: state.dirtyFileIds.filter(fId => fId !== id)
  })),
  removeFile: (id) => set((state) => ({
    files: state.files.filter(f => f.id !== id),
    dirtyFileIds: state.dirtyFileIds.filter(fId => fId !== id),
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
  setIsSaving: (is) => set({ isSaving: is }),
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
