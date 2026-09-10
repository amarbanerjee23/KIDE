import * as monaco from 'monaco-editor';

export const activityDslLanguageDef: monaco.languages.ILanguageExtensionPoint = {
  id: 'activitydsl',
  extensions: ['.activity'],
  aliases: ['Activity DSL', 'activitydsl'],
};

export const activityDslLanguageConfig: monaco.languages.LanguageConfiguration = {
  comments: {
    lineComment: '//',
    blockComment: ['/*', '*/'],
  },
  brackets: [
    ['{', '}'],
    ['[', ']'],
    ['(', ')'],
  ],
  autoClosingPairs: [
    { open: '{', close: '}' },
    { open: '[', close: ']' },
    { open: '(', close: ')' },
    { open: '"', close: '"', notIn: ['string'] },
    { open: "'", close: "'", notIn: ['string', 'comment'] },
  ],
  surroundingPairs: [
    { open: '{', close: '}' },
    { open: '[', close: ']' },
    { open: '(', close: ')' },
    { open: '"', close: '"' },
    { open: "'", close: "'" },
  ],
};

export const activityDslMonarchTokensProvider: monaco.languages.IMonarchLanguage = {
  keywords: [
    'ActivityDiagram', 'uses', 'Objects', 'on', 'context', 'physical', 'contexts', 'produces', 'results',
    'has', 'activities', 'Activity', 'description', 'inputData', 'requireCapability', 'requireOperation',
    'childActivityDiagram', 'conditions', 'nextActivity', 'nextActivityDiagram', 'time', 'secs', 'mins',
    'hrs', 'days', 'interruptedBy', 'interrupts', 'final', 'result', 'if', 'outcome', 'is', 'and', 'or', 'from'
  ],
  operators: [
    '=', '>', '<', '!', '~', '?', ':', '==', '<=', '>=', '!=',
    '&&', '||', '++', '--', '+', '-', '*', '/', '&', '|', '^', '%',
    '<<', '>>', '>>>', '+=', '-=', '*=', '/=', '&=', '|=', '^=',
    '%=', '<<=', '>>=', '>>>='
  ],
  symbols: /[=><!~?:&|+\-*\/\^%]+/,
  tokenizer: {
    root: [
      [/[a-zA-Z_$][\w$]*/, {
        cases: {
          '@keywords': 'keyword',
          '@default': 'identifier'
        }
      }],
      { include: '@whitespace' },
      [/[{}()\[\]]/, '@brackets'],
      [/@symbols/, {
        cases: {
          '@operators': 'operator',
          '@default': ''
        }
      }],
      [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
      [/0[xX][0-9a-fA-F]+/, 'number.hex'],
      [/\d+/, 'number'],
      [/[;,.]/, 'delimiter'],
      [/"([^"\\]|\\.)*$/, 'string.invalid'],
      [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],
      [/'([^'\\]|\\.)*$/, 'string.invalid'],
      [/'/, { token: 'string.quote', bracket: '@open', next: '@string_single' }],
    ],
    string: [
      [/[^\\"]+/, 'string'],
      [/\\./, 'string.escape.invalid'],
      [/"/, { token: 'string.quote', bracket: '@close', next: '@pop' }]
    ],
    string_single: [
      [/[^\\']+/, 'string'],
      [/\\./, 'string.escape.invalid'],
      [/'/, { token: 'string.quote', bracket: '@close', next: '@pop' }]
    ],
    whitespace: [
      [/[ \t\r\n]+/, 'white'],
      [/\/\*/, 'comment', '@comment'],
      [/\/\/.*$/, 'comment'],
    ],
    comment: [
      [/[^\/*]+/, 'comment'],
      [/\/\*/, 'comment', '@push'],
      ["\\*/", 'comment', '@pop'],
      [/[\/*]/, 'comment']
    ],
  },
};

export const activityDslCompletionProvider: monaco.languages.CompletionItemProvider = {
  provideCompletionItems: (model, position) => {
    const word = model.getWordUntilPosition(position);
    const range = {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: word.startColumn,
      endColumn: word.endColumn,
    };

    const keywords = activityDslMonarchTokensProvider.keywords as string[];
    const keywordCompletions: monaco.languages.CompletionItem[] = keywords.map(kw => ({
      label: kw,
      kind: monaco.languages.CompletionItemKind.Keyword,
      insertText: kw,
      range: range,
      documentation: `Keyword ${kw}`,
    }));

    const snippets: monaco.languages.CompletionItem[] = [
      {
        label: 'ActivityDiagram',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'ActivityDiagram ${1:Name} {',
          '\thas activities {',
          '\t\t${2}',
          '\t}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        documentation: 'Create a new ActivityDiagram',
        range: range,
      },
      {
        label: 'Activity',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'Activity ${1:Name} {',
          '\tdescription "${2:Description}"',
          '\t${3}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        documentation: 'Create an Activity',
        range: range,
      }
    ];

    return {
      suggestions: [...keywordCompletions, ...snippets]
    };
  }
};
