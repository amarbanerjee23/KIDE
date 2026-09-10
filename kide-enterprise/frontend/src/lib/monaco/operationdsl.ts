import * as monaco from 'monaco-editor';

export const operationDslLanguageDef: monaco.languages.ILanguageExtensionPoint = {
  id: 'operationdsl',
  extensions: ['.operation'],
  aliases: ['Operation DSL', 'operationdsl'],
};

export const operationDslLanguageConfig: monaco.languages.LanguageConfiguration = {
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

export const operationDslMonarchTokensProvider: monaco.languages.IMonarchLanguage = {
  keywords: [
    'Operation', 'execute', 'return'
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

export const operationDslCompletionProvider: monaco.languages.CompletionItemProvider = {
  provideCompletionItems: (model, position) => {
    const word = model.getWordUntilPosition(position);
    const range = {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: word.startColumn,
      endColumn: word.endColumn,
    };

    const keywords = operationDslMonarchTokensProvider.keywords as string[];
    const keywordCompletions: monaco.languages.CompletionItem[] = keywords.map(kw => ({
      label: kw,
      kind: monaco.languages.CompletionItemKind.Keyword,
      insertText: kw,
      range: range,
      documentation: `Keyword ${kw}`,
    }));

    const snippets: monaco.languages.CompletionItem[] = [
      {
        label: 'Operation',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'Operation ${1:Name} {',
          '\texecute {',
          '\t\t${2}',
          '\t}',
          '\treturn ${3}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        documentation: 'Create a new Operation',
        range: range,
      }
    ];

    return {
      suggestions: [...keywordCompletions, ...snippets]
    };
  }
};
