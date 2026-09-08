export const mncmlLanguageDef = {
  id: 'mncml',
  extensions: ['.mnc'],
  aliases: ['MNC', 'mnc'],
};

export const mncmlMonarchTokensProvider = {
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
    '=', '>', '<', ':', '->', '=>', ',', '.', '{', '}', '[', ']', '(', ')'
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

