import * as monaco from 'monaco-editor';
import { loader } from '@monaco-editor/react';
import { registerKideLanguagePlatform } from '../kide-language/registry';
export {
  registerNavigationHandler,
  navigateToDefinition
} from '../kide-language/languageService';

// Configure @monaco-editor/react to use local monaco-editor bundle directly instead of CDN
loader.config({ monaco });

let registered = false;

export function setupMonacoLanguages(monacoInstance: typeof monaco = monaco) {
  if (registered) return;
  registered = true;

  registerKideLanguagePlatform(monacoInstance);
}

export const setupMonaco = setupMonacoLanguages;
