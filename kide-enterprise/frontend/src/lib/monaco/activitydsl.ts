export const activitydslLanguageDef = {
  id: 'activitydsl',
  extensions: ['.activity'],
  aliases: ['Activity', 'activity'],
};

export const activitydslMonarchTokensProvider = {
  keywords: [
    'ActivityDiagram', 'uses', 'Objects', 'on', 'context', 'physical', 'contexts',
    'produces', 'results', 'has', 'activities', 'Activity', 'description', 'inputData',
    'requireCapability', 'requireOperation', 'childActivityDiagram', 'conditions',
    'nextActivity', 'nextActivityDiagram', 'time', 'secs', 'mins', 'hrs', 'days',
    'interruptedBy', 'interrupts', 'final', 'result', 'if', 'outcome', 'is', 'and', 'or', 'from'
  ],
  operators: [
    '=>', ':', '{', '}', '[', ']', '(', ')', ',', '=', '<', '>'
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

