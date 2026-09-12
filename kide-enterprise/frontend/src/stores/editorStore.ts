import { create } from 'zustand';
import { FileItem, TransformResult, ValidationError, ActivityDiagram } from '../types/models';

export type WorkspaceView = 
  | 'editor' 
  | 'statemachine' 
  | 'workflow' 
  | 'simulator' 
  | 'codegen' 
  | 'split' 
  | 'knowledgegraph';

export type SplitLayoutOption = 
  | 'code-workflow' 
  | 'code-statemachine' 
  | 'workflow-simulator';

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
  activeView: WorkspaceView;
  
  // Engineering Workflow Stage (1: Data, 2: Cap/Op, 3: Workflow, 4: Synthesis)
  activeStage: 1 | 2 | 3 | 4;
  isSynthesisStale: boolean;
  stageLastFileId: Record<number, string>;

  // Layout & Persistent Panel State
  splitMode: boolean;
  splitLayout: SplitLayoutOption;
  aiAssistantOpen: boolean;

  // Knowledge Graph Context State
  graphScope: 'project' | 'global';
  graphData: any | null;
  selectedGraphNode: any | null;
  selectedGraphEdge: any | null;

  // Actions
  setActiveView: (view: WorkspaceView) => void;
  setActiveStage: (stage: 1 | 2 | 3 | 4) => void;
  setIsSynthesisStale: (stale: boolean) => void;
  setStageLastFileId: (stage: number, fileId: string) => void;
  setSplitMode: (split: boolean | ((prev: boolean) => boolean)) => void;
  setSplitLayout: (layout: SplitLayoutOption) => void;
  setAiAssistantOpen: (open: boolean | ((prev: boolean) => boolean)) => void;
  setProject: (id: number | null, name: string | null) => void;
  setFiles: (files: FileItem[]) => void;
  addFile: (file: FileItem) => void;
  updateFileContent: (id: string, content: string) => void;
  markFileSaved: (id: string) => void;
  removeFile: (id: string) => void;
  setActiveFileId: (id: string | null) => void;
  setSelectedNodeId: (id: string | null) => void;
  setGraphScope: (scope: 'project' | 'global') => void;
  setGraphData: (data: any | null) => void;
  setSelectedGraphNode: (node: any | null) => void;
  setSelectedGraphEdge: (edge: any | null) => void;
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

  // Workflow stage defaults
  activeStage: 1,
  isSynthesisStale: false,
  stageLastFileId: {},

  // Layout & Assistant defaults
  splitMode: false,
  splitLayout: 'code-workflow',
  aiAssistantOpen: false,

  // Knowledge Graph Context State defaults
  graphScope: 'project',
  graphData: null,
  selectedGraphNode: null,
  selectedGraphEdge: null,

  setActiveView: (view) => set({ activeView: view }),
  setActiveStage: (stage) => set({ activeStage: stage }),
  setIsSynthesisStale: (stale) => set({ isSynthesisStale: stale }),
  setStageLastFileId: (stage, fileId) => set((state) => ({
    stageLastFileId: { ...state.stageLastFileId, [stage]: fileId }
  })),
  setSplitMode: (val) => set((state) => ({
    splitMode: typeof val === 'function' ? val(state.splitMode) : val
  })),
  setSplitLayout: (layout) => set({ splitLayout: layout }),
  setAiAssistantOpen: (val) => set((state) => ({
    aiAssistantOpen: typeof val === 'function' ? val(state.aiAssistantOpen) : val
  })),

  setProject: (id, name) => set({ projectId: id, projectName: name }),
  setFiles: (files) => set({ 
    files, 
    dirtyFileIds: [],
    activeFileId: files.length > 0 ? (files.find(f => f.name.endsWith('.activity') || f.name.endsWith('.json'))?.id || files[0].id) : null 
  }),
  addFile: (file) => set((state) => ({ files: [...state.files, file], activeFileId: file.id })),
  updateFileContent: (id, content) => set((state) => ({
    files: state.files.map(f => f.id === id ? { ...f, content } : f),
    dirtyFileIds: state.dirtyFileIds.includes(id) ? state.dirtyFileIds : [...state.dirtyFileIds, id],
    // If files are edited after synthesis, mark synthesis as STALE
    isSynthesisStale: state.transformResult !== null ? true : state.isSynthesisStale
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
  setGraphScope: (scope) => set({ graphScope: scope }),
  setGraphData: (data) => set({ graphData: data }),
  setSelectedGraphNode: (node) => set({ selectedGraphNode: node, selectedNodeId: node ? node.name : null }),
  setSelectedGraphEdge: (edge) => set({ selectedGraphEdge: edge }),
  setTransformResult: (result) => set({ 
    transformResult: result, 
    isSynthesisStale: false 
  }),
  setParsedModel: (fileId, ast) => set((state) => {
    const newModels = new Map(state.parsedModels);
    newModels.set(fileId, ast);
    return { parsedModels: newModels };
  }),
  setValidationErrors: (fileId, errors) => set((state) => ({
    validationErrors: { ...state.validationErrors, [fileId]: errors }
  })),
  clearResults: () => set({ 
    transformResult: null, 
    parsedModels: new Map(), 
    validationErrors: {},
    isSynthesisStale: false 
  }),
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
