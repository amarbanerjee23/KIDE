export const capabilitydslLanguageDef = {
  id: 'capabilitydsl',
  extensions: ['.capability'],
  aliases: ['Capability', 'capability'],
};

export const capabilitydslMonarchTokensProvider = {
  keywords: [
    'Capability', 'compatible', 'component', 'interface', 
    'providesControlCapabilities', 'providesOutcomes', 
    'fireable', 'commands', 'receivable', 'events', 'raised', 'alarms', 
    'subscribable', 'DataPoints', 'responses', 'dataPoints', 
    'Init', 'subscribe', 'fire', 'Commands', 'data', 'execute', 'Operations', 'responses=>',
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

