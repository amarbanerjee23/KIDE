import * as monaco from 'monaco-editor';
import { dmlLanguageDef, dmlLanguageConfig, dmlMonarchTokensProvider, dmlCompletionProvider } from './dmldsl';
import { mncmlLanguageDef, mncmlLanguageConfig, mncmlMonarchTokensProvider, mncmlCompletionProvider } from './mncml';
import { activityDslLanguageDef, activityDslLanguageConfig, activityDslMonarchTokensProvider, activityDslCompletionProvider } from './activitydsl';
import { capabilityDslLanguageDef, capabilityDslLanguageConfig, capabilityDslMonarchTokensProvider, capabilityDslCompletionProvider } from './capabilitydsl';
import { operationDslLanguageDef, operationDslLanguageConfig, operationDslMonarchTokensProvider, operationDslCompletionProvider } from './operationdsl';
import {
  xtextDefinitionProvider,
  xtextLinkProvider,
  xtextHoverProvider,
  xtextCompletionProvider,
  xtextDocumentFormattingProvider
} from './xtextLanguageService';

let registered = false;

export function setupMonacoLanguages() {
  if (registered) return;
  registered = true;

  const languages = [
    { def: dmlLanguageDef, config: dmlLanguageConfig, monarch: dmlMonarchTokensProvider, fallbackComp: dmlCompletionProvider },
    { def: mncmlLanguageDef, config: mncmlLanguageConfig, monarch: mncmlMonarchTokensProvider, fallbackComp: mncmlCompletionProvider },
    { def: activityDslLanguageDef, config: activityDslLanguageConfig, monarch: activityDslMonarchTokensProvider, fallbackComp: activityDslCompletionProvider },
    { def: capabilityDslLanguageDef, config: capabilityDslLanguageConfig, monarch: capabilityDslMonarchTokensProvider, fallbackComp: capabilityDslCompletionProvider },
    { def: operationDslLanguageDef, config: operationDslLanguageConfig, monarch: operationDslMonarchTokensProvider, fallbackComp: operationDslCompletionProvider }
  ];

  for (const lang of languages) {
    monaco.languages.register(lang.def);
    monaco.languages.setLanguageConfiguration(lang.def.id, lang.config);
    monaco.languages.setMonarchTokensProvider(lang.def.id, lang.monarch);

    // Register Xtext IDE features
    monaco.languages.registerDefinitionProvider(lang.def.id, xtextDefinitionProvider);
    monaco.languages.registerLinkProvider(lang.def.id, xtextLinkProvider);
    monaco.languages.registerHoverProvider(lang.def.id, xtextHoverProvider);
    monaco.languages.registerCompletionItemProvider(lang.def.id, xtextCompletionProvider);
    monaco.languages.registerDocumentFormattingEditProvider(lang.def.id, xtextDocumentFormattingProvider);
  }
}

export const setupMonaco = setupMonacoLanguages;
