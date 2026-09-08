export const operationdslLanguageDef = {
  id: 'operationdsl',
  extensions: ['.operation'],
  aliases: ['Operation', 'operation'],
};

export const operationdslMonarchTokensProvider = {
  keywords: [
    'Operation', 'execute', 'return',
    // keywords from DML inherited
    'int', 'boolean', 'float', 'string', 'object', 'date', 
    'false', 'true'
  ],
  operators: [
    '=', '{', '}', '[', ']', '(', ')', ',', '.', ':'
  ],
  symbols:  /[=><!~?:&|+\-*\/\^%]+/,
  tokenizer: {
    root: [
      [/[a-zA-Z_$][\w$]*/, { cases: { '@keywords': 'keyword', '@default': 'identifier' } }],
      [/[{}()\[\]]/, '@brackets'],
      [/@symbols/, { cases: { '@operators': 'operator', '@default': '' } }],
      [/\d*\.\d+([eE][\-+]?\d+)?/, 'number.float'],
      [/\d+/, 'number'],
      [/"([^"\\]|\\.)*$/, 'string.invalid'],
      [/"/, { token: 'string.quote', bracket: '@open', next: '@string' }],
    ],
    string: [
      [/[^\\"]+/,  'string'],
      [/\\./,      'string.escape.invalid'],
      [/"/,        { token: 'string.quote', bracket: '@close', next: '@pop' } ]
    ],
  }
};

