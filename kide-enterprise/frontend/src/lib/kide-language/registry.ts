/**
 * KIDE Language Platform Registry.
 * Registers all 5 KIDE DSLs with Monaco Editor along with complete IntelliSense capabilities:
 * completions, hover, cross-file definitions (F12), references (Shift+F12),
 * semantic rename (F2), hierarchical outline, AST folding, idempotent formatting,
 * quick fixes (Ctrl+.), and semantic tokens.
 */

import * as monaco from 'monaco-editor';
import { documentManager } from './documentManager';

// Import Monarch tokenizers and configs
import { dmlLanguageDef, dmlLanguageConfig, dmlMonarchTokensProvider } from '../monaco/dmldsl';
import { capabilityDslLanguageDef, capabilityDslLanguageConfig, capabilityDslMonarchTokensProvider } from '../monaco/capabilitydsl';
import { operationDslLanguageDef, operationDslLanguageConfig, operationDslMonarchTokensProvider } from '../monaco/operationdsl';
import { activityDslLanguageDef, activityDslLanguageConfig, activityDslMonarchTokensProvider } from '../monaco/activitydsl';
import { mncmlLanguageDef, mncmlLanguageConfig, mncmlMonarchTokensProvider } from '../monaco/mncml';

// Import Language Providers
import { kideCompletionProvider } from './completion';
import { kideHoverProvider } from './hover';
import { kideDefinitionProvider } from './definitions';
import { kideReferenceProvider } from './references';
import { kideRenameProvider } from './rename';
import { kideDocumentSymbolProvider, kideWorkspaceSymbolProvider } from './symbols';
import { kideFoldingRangeProvider } from './folding';
import { kideFormattingProvider } from './formatting';
import { kideCodeActionProvider } from './codeActions';
import { kideSemanticTokensProvider } from './semanticTokens';
import { kideSignatureHelpProvider } from './signatureHelp';

let isRegistered = false;

export function registerKideLanguagePlatform(monacoInstance: typeof monaco = monaco) {
  if (isRegistered) return;
  isRegistered = true;

  documentManager.setMonaco(monacoInstance);

  const langConfigs = [
    { def: dmlLanguageDef, config: dmlLanguageConfig, monarch: dmlMonarchTokensProvider },
    { def: capabilityDslLanguageDef, config: capabilityDslLanguageConfig, monarch: capabilityDslMonarchTokensProvider },
    { def: operationDslLanguageDef, config: operationDslLanguageConfig, monarch: operationDslMonarchTokensProvider },
    { def: activityDslLanguageDef, config: activityDslLanguageConfig, monarch: activityDslMonarchTokensProvider },
    { def: mncmlLanguageDef, config: mncmlLanguageConfig, monarch: mncmlMonarchTokensProvider }
  ];

  for (const lang of langConfigs) {
    const langId = lang.def.id;

    // Register language definition if not already registered
    const existing = monacoInstance.languages.getLanguages().find(l => l.id === langId);
    if (!existing) {
      monacoInstance.languages.register(lang.def);
    }

    // Set configuration & Monarch tokens
    monacoInstance.languages.setLanguageConfiguration(langId, lang.config);
    monacoInstance.languages.setMonarchTokensProvider(langId, lang.monarch);

    // Register Language Intelligence Providers
    monacoInstance.languages.registerCompletionItemProvider(langId, kideCompletionProvider);
    monacoInstance.languages.registerHoverProvider(langId, kideHoverProvider);
    monacoInstance.languages.registerDefinitionProvider(langId, kideDefinitionProvider);
    monacoInstance.languages.registerReferenceProvider(langId, kideReferenceProvider);
    monacoInstance.languages.registerRenameProvider(langId, kideRenameProvider);
    monacoInstance.languages.registerDocumentSymbolProvider(langId, kideDocumentSymbolProvider);
    monacoInstance.languages.registerFoldingRangeProvider(langId, kideFoldingRangeProvider);
    monacoInstance.languages.registerDocumentFormattingEditProvider(langId, kideFormattingProvider);
    monacoInstance.languages.registerCodeActionProvider(langId, kideCodeActionProvider);
    try {
      if ((monacoInstance.languages as any).registerDocumentSemanticTokensProvider) {
        // Optional semantic tokens provider
        monacoInstance.languages.registerDocumentSemanticTokensProvider(langId, kideSemanticTokensProvider);
      }
    } catch {
      // Standalone browser fallback
    }
    monacoInstance.languages.registerSignatureHelpProvider(langId, kideSignatureHelpProvider);
  }

  // Register global workspace symbol provider
  // (In Monaco, workspace symbols can be registered on any language or invoked via command palette)
  for (const lang of langConfigs) {
    try {
      // Some Monaco versions support registerWorkspaceSymbolProvider
      if ((monacoInstance.languages as any).registerWorkspaceSymbolProvider) {
        (monacoInstance.languages as any).registerWorkspaceSymbolProvider(lang.def.id, kideWorkspaceSymbolProvider);
      }
    } catch {
      // Optional provider in standalone browser Monaco
    }
  }

  console.info('[KIDE Language Platform] Native language services successfully registered for DML, Capability, Operation, Activity, and MNC-ML.');
}
