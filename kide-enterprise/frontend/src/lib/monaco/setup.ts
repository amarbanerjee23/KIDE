import { loader } from '@monaco-editor/react';
import { mncmlLanguageDef, mncmlMonarchTokensProvider } from './mncml';
import { activitydslLanguageDef, activitydslMonarchTokensProvider } from './activitydsl';

export function setupMonaco() {
  loader.init().then(monaco => {
    monaco.languages.register(mncmlLanguageDef);
    monaco.languages.setMonarchTokensProvider('mncml', mncmlMonarchTokensProvider as any);

    monaco.languages.register(activitydslLanguageDef);
    monaco.languages.setMonarchTokensProvider('activitydsl', activitydslMonarchTokensProvider as any);
  });
}

