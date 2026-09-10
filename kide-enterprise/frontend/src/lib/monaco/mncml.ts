import * as monaco from 'monaco-editor';

export const mncmlLanguageDef: monaco.languages.ILanguageExtensionPoint = {
  id: 'mncml',
  extensions: ['.mncml'],
  aliases: ['MNC-ML', 'mncml'],
};

export const mncmlLanguageConfig: monaco.languages.LanguageConfiguration = {
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

export const mncmlMonarchTokensProvider: monaco.languages.IMonarchLanguage = {
  keywords: [
    'Model', 'import', 'async', 'Command', 'Publish', 'Event', 'Response', 'Alarm', 'level', 'DataPoint',
    'SubscribableItemList', 'subscribedEvents', 'subscribedAlarms', 'subscribedDataPoints', 'Action',
    'raise', 'alarms', 'fire', 'commands', 'generate', 'events', 'trigger', 'data', 'execute', 'operations',
    'transition', 'states', 'expected', 'InterfaceDescription', 'uses', 'dataPoints', 'responses',
    'operatingStates', 'startStates', 'endStates', 'ControlNode', 'implements', 'interface', 'childNodes',
    'CommandResponseBlock', 'EventBlock', 'AlarmBlock', 'DataPointBlock', 'Generate', 'ResponseAggregation',
    'received', 'Responses', 'parameterTranslations', 'parameters', 'operation', 'Max', 'Value', 'Min',
    'Possible', 'Values', 'Validate', 'onFail', 'onSuccess', 'currentState', 'any', 'exitAction', 'nextState',
    'entryAction', 'inputParameters', 'translatedParameters', 'IPaddress', 'port', 'and', 'or', 'expectedResponse'
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

export const mncmlCompletionProvider: monaco.languages.CompletionItemProvider = {
  provideCompletionItems: (model, position) => {
    const word = model.getWordUntilPosition(position);
    const range = {
      startLineNumber: position.lineNumber,
      endLineNumber: position.lineNumber,
      startColumn: word.startColumn,
      endColumn: word.endColumn,
    };

    const keywords = mncmlMonarchTokensProvider.keywords as string[];
    const keywordCompletions: monaco.languages.CompletionItem[] = keywords.map(kw => ({
      label: kw,
      kind: monaco.languages.CompletionItemKind.Keyword,
      insertText: kw,
      range: range,
      documentation: `Keyword ${kw}`,
    }));

    const snippets: monaco.languages.CompletionItem[] = [
      {
        label: 'Model',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'Model ${1:ModelName} {',
          '\t${2}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        documentation: 'Create a new Model',
        range: range,
      },
      {
        label: 'InterfaceDescription',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'InterfaceDescription ${1:InterfaceName} {',
          '\tuses ${2:Type}',
          '\tdataPoints { ${3} }',
          '\tresponses { ${4} }',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        documentation: 'Create an InterfaceDescription',
        range: range,
      },
      {
        label: 'ControlNode',
        kind: monaco.languages.CompletionItemKind.Snippet,
        insertText: [
          'ControlNode ${1:NodeName} {',
          '\timplements interface ${2:InterfaceRef}',
          '\t${3}',
          '}'
        ].join('\n'),
        insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
        documentation: 'Create a ControlNode',
        range: range,
      }
    ];

    return {
      suggestions: [...keywordCompletions, ...snippets]
    };
  }
};
