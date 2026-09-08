import { loader } from '@monaco-editor/react';
import { mncmlLanguageDef, mncmlMonarchTokensProvider } from './mncml';
import { activitydslLanguageDef, activitydslMonarchTokensProvider } from './activitydsl';
import { dmldslLanguageDef, dmldslMonarchTokensProvider } from './dmldsl';
import { operationdslLanguageDef, operationdslMonarchTokensProvider } from './operationdsl';
import { capabilitydslLanguageDef, capabilitydslMonarchTokensProvider } from './capabilitydsl';

export function setupMonaco() {
  loader.init().then(monaco => {
    monaco.languages.register(mncmlLanguageDef);
    monaco.languages.setMonarchTokensProvider('mncml', mncmlMonarchTokensProvider as any);

    monaco.languages.register(activitydslLanguageDef);
    monaco.languages.setMonarchTokensProvider('activitydsl', activitydslMonarchTokensProvider as any);

    monaco.languages.register(dmldslLanguageDef);
    monaco.languages.setMonarchTokensProvider('dmldsl', dmldslMonarchTokensProvider as any);

    monaco.languages.register(operationdslLanguageDef);
    monaco.languages.setMonarchTokensProvider('operationdsl', operationdslMonarchTokensProvider as any);

    monaco.languages.register(capabilitydslLanguageDef);
    monaco.languages.setMonarchTokensProvider('capabilitydsl', capabilitydslMonarchTokensProvider as any);
  });
}

