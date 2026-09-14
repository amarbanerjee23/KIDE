/**
 * Centralized Language Identification and Specification Registry for KIDE Enterprise.
 * Ensures consistent language mapping across editor, models, parsers, formatters, and validators.
 */

export interface LanguageSpec {
  id: string;
  aliases: string[];
  extensions: string[];
  displayName: string;
  category: 'data' | 'capability' | 'operation' | 'workflow' | 'control' | 'config';
  description: string;
}

export const KIDE_LANGUAGES: Record<string, LanguageSpec> = {
  DML: {
    id: 'dmldsl',
    aliases: ['kide-dml', 'DML', 'dml', 'dmldsl'],
    extensions: ['.dml'],
    displayName: 'Data Model Language (DML)',
    category: 'data',
    description: 'Foundation data packages, primitives, and composite domain models.'
  },
  CAPABILITY: {
    id: 'capabilitydsl',
    aliases: ['kide-capability', 'Capability DSL', 'capability', 'cap', 'capabilitydsl'],
    extensions: ['.cap', '.capability'],
    displayName: 'Semantic Capability DSL',
    category: 'capability',
    description: 'Industrial device capability contracts, control capabilities, and outcomes.'
  },
  OPERATION: {
    id: 'operationdsl',
    aliases: ['kide-operation', 'Operation DSL', 'operation', 'op', 'operationdsl'],
    extensions: ['.op', '.operation'],
    displayName: 'Device Operation DSL',
    category: 'operation',
    description: 'Executable low-level scripts and device operations with typed parameters.'
  },
  ACTIVITY: {
    id: 'activitydsl',
    aliases: ['kide-activity', 'Activity DSL', 'activity', 'activitydsl'],
    extensions: ['.activity'],
    displayName: 'Activity Workflow DSL',
    category: 'workflow',
    description: 'Supervisory workflows, sequential steps, conditions, and capability bindings.'
  },
  MNC: {
    id: 'mncml',
    aliases: ['kide-mnc', 'MNC-ML', 'mnc', 'mncml', 'mncspec'],
    extensions: ['.mnc', '.mncspec'],
    displayName: 'MNC-ML Control Model',
    category: 'control',
    description: 'Component interface descriptions, operating states, and control node state machines.'
  },
  JSON: {
    id: 'json',
    aliases: ['json'],
    extensions: ['.json'],
    displayName: 'JSON Specification',
    category: 'config',
    description: 'Configuration, graph schemas, and metadata descriptors.'
  }
};

/**
 * Returns the exact Monaco language ID for a filename or artifact.
 * Guaranteed to produce the exact same ID everywhere in KIDE.
 */
export function getLanguageForArtifact(filenameOrArtifact: string | { name?: string; filename?: string } | null | undefined): string {
  if (!filenameOrArtifact) return 'plaintext';

  const filename = typeof filenameOrArtifact === 'string'
    ? filenameOrArtifact
    : (filenameOrArtifact.filename || filenameOrArtifact.name || '');

  const lower = filename.toLowerCase().trim();

  if (lower.endsWith('.dml')) return KIDE_LANGUAGES.DML.id;
  if (lower.endsWith('.cap') || lower.endsWith('.capability')) return KIDE_LANGUAGES.CAPABILITY.id;
  if (lower.endsWith('.op') || lower.endsWith('.operation')) return KIDE_LANGUAGES.OPERATION.id;
  if (lower.endsWith('.activity')) return KIDE_LANGUAGES.ACTIVITY.id;
  if (lower.endsWith('.mnc') || lower.endsWith('.mncspec')) return KIDE_LANGUAGES.MNC.id;
  if (lower.endsWith('.json')) return 'json';
  if (lower.endsWith('.py')) return 'python';
  if (lower.endsWith('.md')) return 'markdown';
  if (lower.endsWith('.ts') || lower.endsWith('.tsx')) return 'typescript';
  if (lower.endsWith('.js') || lower.endsWith('.jsx')) return 'javascript';

  return 'plaintext';
}

/**
 * Checks if a language ID or filename corresponds to a native KIDE DSL.
 */
export function isKideDsl(langOrFilename: string): boolean {
  const lang = getLanguageForArtifact(langOrFilename);
  return ['dmldsl', 'capabilitydsl', 'operationdsl', 'activitydsl', 'mncml'].includes(lang);
}

// Re-export unified parser API
export { parseArtifact, parseAst } from './languages/index';
