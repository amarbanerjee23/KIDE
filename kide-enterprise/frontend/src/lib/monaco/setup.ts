import * as monaco from 'monaco-editor';
import { dmlLanguageDef, dmlLanguageConfig, dmlMonarchTokensProvider, dmlCompletionProvider } from './dmldsl';
import { mncmlLanguageDef, mncmlLanguageConfig, mncmlMonarchTokensProvider, mncmlCompletionProvider } from './mncml';
import { activityDslLanguageDef, activityDslLanguageConfig, activityDslMonarchTokensProvider, activityDslCompletionProvider } from './activitydsl';
import { capabilityDslLanguageDef, capabilityDslLanguageConfig, capabilityDslMonarchTokensProvider, capabilityDslCompletionProvider } from './capabilitydsl';
import { operationDslLanguageDef, operationDslLanguageConfig, operationDslMonarchTokensProvider, operationDslCompletionProvider } from './operationdsl';

export function setupMonacoLanguages() {
  // DML
  monaco.languages.register(dmlLanguageDef);
  monaco.languages.setLanguageConfiguration(dmlLanguageDef.id, dmlLanguageConfig);
  monaco.languages.setMonarchTokensProvider(dmlLanguageDef.id, dmlMonarchTokensProvider);
  monaco.languages.registerCompletionItemProvider(dmlLanguageDef.id, dmlCompletionProvider);

  // MNC-ML
  monaco.languages.register(mncmlLanguageDef);
  monaco.languages.setLanguageConfiguration(mncmlLanguageDef.id, mncmlLanguageConfig);
  monaco.languages.setMonarchTokensProvider(mncmlLanguageDef.id, mncmlMonarchTokensProvider);
  monaco.languages.registerCompletionItemProvider(mncmlLanguageDef.id, mncmlCompletionProvider);

  // Activity DSL
  monaco.languages.register(activityDslLanguageDef);
  monaco.languages.setLanguageConfiguration(activityDslLanguageDef.id, activityDslLanguageConfig);
  monaco.languages.setMonarchTokensProvider(activityDslLanguageDef.id, activityDslMonarchTokensProvider);
  monaco.languages.registerCompletionItemProvider(activityDslLanguageDef.id, activityDslCompletionProvider);

  // Capability DSL
  monaco.languages.register(capabilityDslLanguageDef);
  monaco.languages.setLanguageConfiguration(capabilityDslLanguageDef.id, capabilityDslLanguageConfig);
  monaco.languages.setMonarchTokensProvider(capabilityDslLanguageDef.id, capabilityDslMonarchTokensProvider);
  monaco.languages.registerCompletionItemProvider(capabilityDslLanguageDef.id, capabilityDslCompletionProvider);

  // Operation DSL
  monaco.languages.register(operationDslLanguageDef);
  monaco.languages.setLanguageConfiguration(operationDslLanguageDef.id, operationDslLanguageConfig);
  monaco.languages.setMonarchTokensProvider(operationDslLanguageDef.id, operationDslMonarchTokensProvider);
  monaco.languages.registerCompletionItemProvider(operationDslLanguageDef.id, operationDslCompletionProvider);
}

export const setupMonaco = setupMonacoLanguages;
