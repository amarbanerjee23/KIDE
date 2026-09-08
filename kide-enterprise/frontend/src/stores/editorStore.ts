import { create } from 'zustand';
import { TransformResult, ValidationError } from '../types/models';

interface EditorState {
  activityJson: string;
  transformResult: TransformResult | null;
  isTransforming: boolean;
  validationErrors: ValidationError[];
  selectedNodeId: string | null;
  setActivityJson: (json: string) => void;
  setTransformResult: (result: TransformResult | null) => void;
  setValidationErrors: (errors: ValidationError[]) => void;
  setIsTransforming: (val: boolean) => void;
  setSelectedNodeId: (id: string | null) => void;
  clearResults: () => void;
}

export const useEditorStore = create<EditorState>((set) => ({
  activityJson: '{\n  "name": "New Activity",\n  "defaultOperatingStates": [],\n  "activities": []\n}',
  transformResult: null,
  isTransforming: false,
  validationErrors: [],
  selectedNodeId: null,
  setActivityJson: (activityJson) => set({ activityJson }),
  setTransformResult: (transformResult) => set({ transformResult }),
  setValidationErrors: (validationErrors) => set({ validationErrors }),
  setIsTransforming: (isTransforming) => set({ isTransforming }),
  setSelectedNodeId: (selectedNodeId) => set({ selectedNodeId }),
  clearResults: () => set({ transformResult: null, validationErrors: [] })
}));

