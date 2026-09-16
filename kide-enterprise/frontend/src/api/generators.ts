import { fetchClient } from './client';

export interface GeneratorInfo {
  id: string;
  name: string;
  description: string;
  target_extension: string;
  builtin: boolean;
  file_id?: number;
  file_name?: string;
}

export interface GenerateResult {
  generator_id: string;
  model_name: string;
  files: Record<string, string>;
  file_count: number;
}

export interface GeneratorTemplateInfo {
  id: string;
  name: string;
  language: string;
  target_extension: string;
  specification_module?: string;
  description: string;
  template_source: string;
}

export const generatorsApi = {
  listGenerators: (projectId: number): Promise<GeneratorInfo[]> =>
    fetchClient(`/projects/${projectId}/generators`),

  createGenerator: (projectId: number, data: { name: string; code?: string }): Promise<{ message: string; file_id: number; name: string }> =>
    fetchClient(`/projects/${projectId}/generators`, { method: 'POST', body: JSON.stringify(data) }),

  generateCode: (projectId: number, data: { generator_id: string; save_to_project?: boolean; activity_file_id?: number }): Promise<GenerateResult> =>
    fetchClient(`/projects/${projectId}/generate`, { method: 'POST', body: JSON.stringify(data) }),

  getTemplate: (projectId: number): Promise<{ template: string }> =>
    fetchClient(`/projects/${projectId}/generators/template`),

  listTemplates: (projectId: number): Promise<GeneratorTemplateInfo[]> =>
    fetchClient(`/projects/${projectId}/generators/templates`),

  getTemplateSource: (projectId: number, generatorId: string): Promise<GeneratorTemplateInfo> =>
    fetchClient(`/projects/${projectId}/generators/templates/${generatorId}`),
};
